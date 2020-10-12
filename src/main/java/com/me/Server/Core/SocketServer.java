package com.me.Server.Core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.Const.*;
import com.me.Interface.Act;
import com.me.utils.Logger;
import com.me.Server.Service.UserService;
import com.me.Server.Service.UserServiceImpl;
import com.me.domain.User;
import com.me.utils.MyIOUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class SocketServer{
    private RefreshObject refreshObject = new RefreshObject();
    private boolean running = true;
    private final Object object = new Object();
    private final Map<String, Socket> users = new HashMap<String,Socket>();
    private Integer online = 0;
    private final ServerSocket serverSocket;
    private final UserService userService = new UserServiceImpl();
    private final ObjectMapper mapper = new ObjectMapper();
    public SocketServer() throws IOException {
        this.serverSocket = new ServerSocket(8888);
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
    private RefreshObject getRefreshObject(String name){
        //获取在线的人和朋友
        this.refreshObject.setFriends(getFriendsName(name));
        this.refreshObject.setOnline(getOnlineName());
        this.refreshObject.setWho(name);
        return this.refreshObject;
    }
    private String[] getOnlineName(){
        return users.keySet().toArray(new String[users.keySet().size()]);

    }
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
    private boolean isLogin(String name){
        return users.containsKey(name);
    }
    class Handler extends AbstractServerDispatcher{

        private String name;
        private boolean closed = false;
        Handler(Socket socket){
            super(socket);
//            spread(getOnlineName(),true);
            refresh(null,Act.SEND); // FIXME: 2020/10/11 用请求的方式获取在线的人
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

//        private void recv() {
//            try {
//                String recv = MyIOUtils.recv(this.socket.getInputStream());
//                try {
//                    mapper.readValue(recv, RefreshObject.class);
//                    refresh();
//                } catch (JsonProcessingException e) {
//                    try {
//                        SingleFileObj singleFileObj = mapper.readValue(recv, SingleFileObj.class);
//                        singleContact(singleFileObj);
//                    } catch (JsonProcessingException jsonProcessingException) {
//                        try {
//                            SingleChatObj singleChatObj = mapper.readValue(recv, SingleChatObj.class);
//                            singleContact(singleChatObj);
//                        } catch (JsonProcessingException processingException) {
//                            Logger.info(this.name+"->recvMsg:"+recv);
//                            spread(this.name+":"+recv,false);
//                        }
//                    }
//                }
//
//
//            } catch (IOException e) {
//                offLine();
//                spread(getOnlineName(),true);
//                refresh();
//                this.close();
//            }
//
//        }

        private void singleContact(SingleFileObj singleFileObj) {
            Socket socket = users.get(singleFileObj.getTo());
            try {
                Logger.info("::FILE:"+singleFileObj+"\nFILE_SIZE:"+(singleFileObj.getSize()/1024)+"kb");
                MyIOUtils.send(singleFileObj,socket.getOutputStream());
                MyIOUtils.swap(this.socket.getInputStream(),socket.getOutputStream(),singleFileObj.getSize());
            } catch (IOException e) {
                offLine_();
                this.close();
                Logger.error(e.getMessage());
            }

            



        }
        private void offLine(){
            online --;
            synchronized (object) {
                users.remove(this.name);
            }
            running = false;
            Logger.info("当前在线::"+ Arrays.toString(getOnlineName()));
        }
        private void singleContact(SingleChatObj singleChatObj){
            Socket socket = users.get(singleChatObj.getTo());
            try {
                MyIOUtils.send(singleChatObj,socket.getOutputStream());
                Logger.info(singleChatObj.getFrom()+"->"+singleChatObj.getTo()+"::MSG:"+singleChatObj.getMsg());
            } catch (IOException e) {
                offLine_();
                this.close();
                Logger.error(e.getMessage());
            }
        }
        private void spread(ALlMessageObject obj,boolean all){
            Logger.info(this.name+"-spread-ALL?"+all+"::"+obj.getMsg());
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
            users.put(this.name,this.socket);
            refresh(null,Act.SEND);
            Logger.info("当前在线::"+ Arrays.toString(getOnlineName()));
        }

        private void close(){
            try {
                this.closed = true;
                this.socket.close();
            } catch (IOException e) {
                Logger.error(e.getMessage());
            }
        }


        @Override
        public void singleFile(SingleFileObj obj, String act) {
            singleContact(obj);
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
