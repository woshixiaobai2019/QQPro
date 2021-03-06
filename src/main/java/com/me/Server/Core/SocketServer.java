package com.me.Server.Core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.Const.*;
import com.me.Interface.Act;
import com.me.utils.Logger;
import com.me.Server.Service.UserService;
import com.me.Server.Service.UserServiceImpl;
import com.me.domain.User;
import com.me.utils.MyIOUtils;
import com.me.utils.ServerInitUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class SocketServer{
    private final RefreshObject refreshObject = new RefreshObject();
    private boolean running = true;
    private final Object object = new Object();
    private final Map<String, Socket> users = new HashMap<String,Socket>();
    private final Map<String, Socket> userFile = new HashMap<String,Socket>();
    private Integer online = 0;
    private final ServerSocket serverSocket;
    private final UserService userService = new UserServiceImpl();
    public SocketServer() throws IOException {
        this.serverSocket = new ServerSocket(ServerInitUtils.getPort());
        Logger.info(this.serverSocket+"-服务器正在运行中,等待连接...");
        this.accept(); // FIXME: 2020/10/12 在聊天和登录注册最好不使用同一个socket对象，否则会造成并发问题
    }

    private void accept() {
        while (this.running) {
            try {
                Socket accept = serverSocket.accept();
                Logger.info(accept+"连接");
                new Thread(new Handler(accept)).start();
            } catch (IOException e) {
                this.running = false;
                Logger.error(e.getMessage());
                System.exit(0);
            }
        }
    }

    /**
     * 获取当前在线的人和朋友们
     * @param name 获取谁的朋友
     * @return
     */
    private RefreshObject getRefreshObject(String name){
        //获取在线的人和朋友
        this.refreshObject.setFriends(getFriendsName(name));
        this.refreshObject.setOnline(getOnlineName());
        this.refreshObject.setWho(name);
        return this.refreshObject;
    }

    /**
     * 获取当前所有在线的人并返回
     * @return
     */
    private String[] getOnlineName(){
        return users.keySet().toArray(new String[users.keySet().size()]);

    }

    /**
     * 获取某位用户的所有朋友
     * @param name
     * @return
     */
    public String[] getFriendsName(String name){
        ArrayList<String> strings = new ArrayList<>();
        List<User> friends =  userService.getFriends(name);
        for (User friend : friends) {
            if (users.containsKey(friend.getUsername())){
                strings.add(friend.getUsername());
            }
        }
        return strings.toArray(new String[strings.size()]);
    }

    /**
     * 检查某位用户是否登录
     * @param name
     * @return
     */
    private boolean isLogin(String name){
        return users.containsKey(name);
    }
    class Handler extends AbstractServerDispatcher{
        Handler(Socket socket){
            super(socket);
//            spread(getOnlineName(),true);
            refresh(null,Act.SEND); // FIXME: 2020/10/11 用请求的方式获取在线的人
        }
        @Override
        public void singleFileBack(SingleFileBackObj obj, String recv) {
            Socket from = users.get(obj.getTo());
            Socket fileFrom = userFile.get(obj.getTo());
            Socket fileTo = userFile.get(obj.getFrom());
            try {
                MyIOUtils.send(obj,from.getOutputStream());
                if (obj.isConfirm()){
                    new Thread(()->{
                        try {
                            MyIOUtils.swap(fileFrom.getInputStream(), fileTo.getOutputStream(), obj.getSize());
                        } catch (IOException e) {
                            offLine_();
                            e.printStackTrace();
                        }
                    }).start(); //开一个新的线程去用于传输文件,不知道为啥不能开多线程
                    //接受来自对方的文件流
                }
            } catch (IOException e) {

                offLine_();

            }
        }
        @Override
        protected void singleChat(SingleChatObj obj) {
            Socket friend = users.get(obj.getTo());
            try {
                MyIOUtils.send(obj,friend.getOutputStream());
            } catch (IOException e) {
                offLine_();
            }
        }

        @Override
        protected void spread(ALlMessageObject obj, boolean all, String act) {
            if (act.equals(Act.RECV)){
                spread(obj,all);
            }
        }

        @Override
        protected void offLine_() {
            if (this.name!=null) {
                offLine();
                refresh(null,Act.SEND);
                Logger.info(name + ":已离线");
            }
            running = false;
        }
        private void singleContact(SingleFileObj singleFileObj) {


            



        }
        private void offLine(){
            online --;
            synchronized (object) {
                users.remove(this.name);
            }
            running = false;
            Logger.info("当前在线::"+ Arrays.toString(getOnlineName()));
        }
        private void spread(ALlMessageObject obj,boolean all){
            Logger.info(this.name+"-spread-ALL?"+all+"::"+obj.getMsg());
            obj.setMsg(this.name+":"+obj.getMsg());
            for (Socket value : users.values()) {
                if (all || value!=this.socket){
                    try {
                        MyIOUtils.send(obj,value.getOutputStream());
                    } catch (IOException e) {
                        offLine_();
                    }
                }
            }
        }
        @Override
        public void refresh(RefreshObject obj,String act){
            //只是用于登录时获取在线的人和朋友
            if (act.equals(Act.SEND)){
                for (String s : users.keySet()) {
                    try {
                        MyIOUtils.send(getRefreshObject(s),users.get(s).getOutputStream());
                    } catch (IOException e) {
                        try {
                            users.get(s).close();
                        } catch (IOException ioException) {
                            Logger.error(ioException.getMessage());
                        }
                    }
                }
            }else{
                refresh(null,Act.SEND);
            }
        }

        @Override
        public void introduceSelf(IntroduceObject obj) {
            this.name = obj.getName();
            this.type = obj.getType();
            if (obj.getType().equals(TransferType.user)) {
                users.put(this.name, this.socket);
                refresh(null, Act.SEND);
                Logger.info("当前在线::" + Arrays.toString(getOnlineName()));
            }else{
                userFile.put(this.name,this.socket);
            }
        }

        private void close(){
            try {
                this.socket.close();
            } catch (IOException e) {
                Logger.error(e.getMessage());
            }
        }


        @Override
        public void singleFile(SingleFileObj obj, String act) {
            Socket socket = users.get(obj.getTo());
            try {
                MyIOUtils.send(obj,socket.getOutputStream());
            } catch (IOException e) {
                offLine_();
                this.close();
                Logger.error(e.getMessage());
            }
        }

        @Override
        public void login(LoginObject obj, String act) {
            LoginObject loginObject = new LoginObject();
            String login;
            if (!isLogin(obj.getUsername())) {
                 login= userService.login(obj.getUsername(), obj.getPassword());
            }else{
                login = UserConst.HAVE_LOGGED;
            }
            Logger.info(obj.getUsername()+"登录结果:"+login);
            loginObject.setLogin(login);
            try {
                MyIOUtils.send(loginObject, socket.getOutputStream());
            } catch (IOException e) {
                offLine_();
            }
        }

        @Override
        public void sign(SignObject obj, String act) {
            String sign = userService.sign(obj.getUsername(), obj.getPassword());
            SignObject signObject = new SignObject();
            signObject.setSign(sign);
            Logger.info(obj.getUsername()+"注册结果:"+sign);
            try {
                MyIOUtils.send(signObject, this.socket.getOutputStream());
            } catch (IOException e) {
                offLine_();
            }
        }

        @Override
        public void deleteFriend(DeleteFriendObject obj, String act) {
            if (act.equals(Act.RECV)){
                userService.deleteFriend(obj.getUsername(),obj.getFriend());
                userService.deleteFriend(obj.getFriend(),obj.getUsername());
                DeleteFriendObject deleteFriendObject = new DeleteFriendObject();
                deleteFriendObject.setFriends(getFriendsName(this.name));
                deleteFriend(deleteFriendObject,Act.SEND);
                deleteFriendObject.setFriends(getFriendsName(obj.getFriend()));
                try {
                    MyIOUtils.send(deleteFriendObject,users.get(obj.getFriend()).getOutputStream());
                } catch (IOException e) {
                    offLine_();
                }
            }else{
                try {
                    MyIOUtils.send(obj,socket.getOutputStream());
                } catch (IOException e) {
                    offLine_();
                }
            }
        }

        @Override
        public void addFriend(AddFriendObject obj, String act) {
            if (act.equals(Act.RECV)){
                userService.addFriend(obj.getUsername(),obj.getFriend());
                userService.addFriend(obj.getFriend(),obj.getUsername());

                AddFriendObject addFriendObject = new AddFriendObject();
                addFriendObject.setFriends(getFriendsName(this.name));
                addFriend(addFriendObject,Act.SEND); //给用户发,还要给朋友也发一下
                addFriendObject.setFriends(getFriendsName(obj.getFriend()));

                try {
                    MyIOUtils.send(addFriendObject,users.get(obj.getFriend()).getOutputStream());
                } catch (IOException e) {
                    offLine_();
                }
            }else{
                try {
                    MyIOUtils.send(obj,socket.getOutputStream());
                } catch (IOException e) {
                    offLine_();
                }
            }
        }

        @Override
        public void chat(ALlMessageObject obj, String act) {

        }
    }
}
