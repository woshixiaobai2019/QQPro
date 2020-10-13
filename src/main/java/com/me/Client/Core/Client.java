package com.me.Client.Core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.Const.*;
import com.me.Interface.Act;
import com.me.Server.Service.UserService;
import com.me.Server.Service.UserServiceImpl;
import com.me.utils.Logger;
import com.me.utils.MyIOUtils;
import com.me.utils.IOGUIUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;


public class Client extends JFrame {
    // TODO: 2020/10/11 完善登录和注册功能,去掉refresh直接改为接受返回值
    private final String username;
    private final ClientCore clientCore;
    private JTextArea recv;
    private JTextArea send;
    private DefaultListModel<String> online;
    private DefaultListModel<String> friends;
    private JList<String> onlineList;
    private JList<String> friendsList;
    private String path = null;
    private JButton singleButton;
    private JButton fileSend;
    private JButton deleteButton;
    private JButton addButton;
    private JButton fileChooser;
    public Client(String username,Socket socket) throws IOException {
        this.username = username;
        this.clientCore = new ClientCore(socket);
        this.init();
        this.recv.setEnabled(false);
        new Thread(clientCore).start();
        IntroduceObject introduceObject = new IntroduceObject(); //等界面加载完毕之后再发送
        introduceObject.setName(username);
        clientCore.introduceSelf(introduceObject);
    }
    private void init() {
        JFrame window = new JFrame("MyQQ-"+username);
        window.setLayout(null);
        window.setSize(600, 800);//设置大小
        window.setLocationRelativeTo(null);//设置居中
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置可关闭
        window.setLayout(null);//设置绝对布局（窗口里面的内容不会随着窗口的改变而改变）
        window.setResizable(false);//设置窗口不可拉伸改变大小


        //fileSend
        JLabel fileLabel = new JLabel("选择文件");
        fileLabel.setBounds(75,0,100,100);
        fileChooser = new JButton("选择文件");
        fileChooser.setBounds(75, 60, 100, 100);
        fileChooser.addActionListener(new FileChooseListener());
        window.add(fileChooser);
        fileSend = new JButton("发送文件");
        fileSend.setBounds(75, 245, 100, 50);
        fileSend.addActionListener(new FileSendListener());
        window.add(fileSend);
        //friendsList
        JLabel friend_label =new JLabel("好友:");
        friend_label.setBounds(225,0,100,100);
        window.add(friend_label);
        friends = new DefaultListModel<String>();
        friendsList = new JList<String>(friends);
        JScrollPane friendScrollPane = new JScrollPane();
        friendsList.setBounds(225, 60, 175, 180);
        friendScrollPane.setViewportView(friendsList);
        friendScrollPane.setBounds(225, 60, 175, 180);
        window.add(friendScrollPane);

        singleButton = new JButton("私信");
        singleButton.addActionListener(new SingleSendListener());
        singleButton.setBounds(225, 245, 75, 50);
        window.add(singleButton);

        deleteButton = new JButton("删除");
        deleteButton.addActionListener(new DeleteFriendListener());
        deleteButton.setBounds(325, 245, 75, 50);
        window.add(deleteButton);

        //Online List
        JLabel online_label =new JLabel("在线:");
        online_label.setBounds(450,0,100,100);
        window.add(online_label);
        online = new DefaultListModel<String>();
        onlineList = new JList<String>(online);
        JScrollPane scrollPane0 = new JScrollPane();
        onlineList.setBounds(450, 60, 100, 180);
        scrollPane0.setViewportView(onlineList);
        scrollPane0.setBounds(450, 60, 100, 180);
        window.add(scrollPane0);

        addButton = new JButton("添加好友");
        addButton.addActionListener(new AddFriendListener());
        addButton.setBounds(450, 245, 100, 50);
        window.add(addButton);
        //设置接受标签
        JLabel username_label =new JLabel("接收:");
        username_label.setBounds(100,300,100,100);
        window.add(username_label);
        //设置发送标签
        JLabel password_label =new JLabel("发送:");
        password_label.setBounds(100,450,100,100);
        window.add(password_label);

        //设置接收文本框

        recv=new JTextArea();
        recv.setLineWrap(true);
        recv.setWrapStyleWord(true);
        recv.setBounds(150, 300, 300, 100);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(150, 300, 300, 100);
        scrollPane.setViewportView(recv);
        window.add(scrollPane);
        //设置发送文本框
        send=new JTextArea();
        send.setLineWrap(true);
        send.setWrapStyleWord(true);
        send.setBounds(150, 450, 300, 100);
        JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setBounds(150, 450, 300, 100);
        scrollPane1.setViewportView(send);
        window.add(scrollPane1);
        //设置按钮
        JButton sendButton = new JButton("发送");
        sendButton.addActionListener(new SendListener());
        sendButton.setBounds(250, 600, 100, 50);
        window.add(sendButton);
        window.addWindowListener(new CloseListener());
        window.setVisible(true);//设置面板可见

    }

    private void alert(String msg,String type){
        //如果错误则弹出一个显示框
        JOptionPane pane = new JOptionPane(msg);
        JDialog dialog = pane.createDialog(this, type);
        dialog.show();
    }

    /**
     *
     * @param path 选择的文件的路径
     * @param from 由谁发送的
     * @param to 发送给谁
     * @return 返回包装好的文件对象
     */
    private SingleFileObj parse(String path,String from,String to){
        File file = new File(path);
        SingleFileObj obj = new SingleFileObj();
        obj.setTo(to);
        obj.setFrom(from);
        obj.setSize(file.length());
        String[] split = file.getName().split("\\\\");
        obj.setFileName(split[split.length-1]);
        obj.setPath(path);
        return obj;
    }

    /**
     * 私聊的事件监听对象
     */
    class SingleSendListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            this.singleSend();
        }

        private void singleSend() {
            String selectedValue = friendsList.getSelectedValue();
            if (selectedValue!=null){
                String msg = send.getText();
                if (msg!=null && !"".equals(msg)) {
                    SingleChatObj singleChatObj = new SingleChatObj();
                    singleChatObj.setMsg(msg);
                    singleChatObj.setFrom(username);
                    singleChatObj.setTo(selectedValue);
                    clientCore.chat(singleChatObj, Act.SEND);
                }else{
                    alert(UserConst.EMPTY_MSG,"ERROR");
                }
            }else{
                alert(UserConst.FRIEND_NOT_CHOOSE,"INFO");
            }
        }
    }

    /**
     * 发送文件的时间监听对象
     */
    class FileSendListener implements  ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            this.sendFile();
        }

        private void sendFile() {
            if (path!=null){
                String selectedValue = friendsList.getSelectedValue();
                if (selectedValue!=null){
                    if (new File(path).isFile()) {
                        SingleFileObj obj = parse(path, username, selectedValue);
                        clientCore.singleFile(obj,Act.SEND);
                    }else{
                        alert(UserConst.CAN_NOT_DIR,"ERROR");
                    }
                }else{
                    alert(UserConst.FRIEND_NOT_CHOOSE,"ERROR");
                }
            }else{
                alert(UserConst.FILE_NOT_CHOOSE,"INFO");
            }
        }
    }

    /**
     * 选择文件的事件监听对象
     */
    class FileChooseListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.showDialog(new JLabel(), "选择");
            File fileChoose = chooser.getSelectedFile();
            try {
                path = fileChoose.getAbsolutePath();
            } catch (Exception ignore) {}
        }
    }

    /**
     * 删除朋友按钮的时间监听对象
     */
    class DeleteFriendListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            this.delete(e);
        }


        private void delete(ActionEvent e) {
            String friend = friendsList.getSelectedValue();
            if (friend!=null){
                DeleteFriendObject deleteFriendObject = new DeleteFriendObject();
                deleteFriendObject.setUsername(username);
                deleteFriendObject.setFriend(friend);
                clientCore.deleteFriend(deleteFriendObject,Act.SEND);
            }else{
                alert(UserConst.FRIEND_NOT_CHOOSE,"ERROR");
            }

        }
    }

    /**
     * 添加朋友的事件监听对象
     */
    class AddFriendListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            this.addFriend(e);
        }
        private void addFriend(ActionEvent e) {
            String friend = onlineList.getSelectedValue();
            if (friend!=null &&!friend.equals(username) ){
                AddFriendObject addFriendObject = new AddFriendObject();
                addFriendObject.setUsername(username);
                addFriendObject.setFriend(friend);
                clientCore.addFriend(addFriendObject,Act.SEND);
            }else if(friend==null){
                alert(UserConst.USER_NOT_CHOOSE,"ERROR");

            }else {
                alert(UserConst.ADD_SELF_ERROR,"ERROR");
            }

        }
    }

    /**
     * 窗口关闭的事件监听对象,关闭时同时需要关闭socket
     */
    class CloseListener implements WindowListener{
        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            try {
                clientCore.socket.close();
            } catch (IOException ignore) {}
            System.exit(0);
        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }
    }
    class SendListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String msg = send.getText();
            if (msg!=null && !"".equals(msg)) {
                ALlMessageObject aLlMessageObject = new ALlMessageObject();
                aLlMessageObject.setMsg(msg);
                clientCore.chat(aLlMessageObject, Act.SEND);
            }else{
                alert(UserConst.EMPTY_MSG,"ERROR");
            }

        }
    }
    class ClientCore extends AbstractClientDispatcher {
        ClientCore(Socket socket) throws IOException {
            super(username,socket);
            //client连接到了服务端
            //第一步就是发送一个refresh请求,得到当前在线的人和朋友

        }
        public void onlineChange(boolean flag){
            onlineList.setEnabled(flag);
            addButton.setEnabled(flag);
        }
        public void singleChange(boolean flag){
            fileChooser.setEnabled(flag);
            friendsList.setEnabled(flag);
            fileSend.setEnabled(flag);
            singleButton.setEnabled(flag);
            deleteButton.setEnabled(flag);
        }
        @Override
        public void singleFile(SingleFileObj obj,String act) {
            if (act.equals(Act.RECV)){
                try {
                    alert(IOGUIUtils.recv(obj,this.socket.getInputStream()),"INFO");
                } catch (IOException e) {
                    alert(UserConst.SERVER_CLOSED,"INFO");
                    System.exit(0);
                }
            }else if (act.equals(Act.SEND)){
                try {
                    IOGUIUtils.send(obj,socket.getOutputStream());
                    alert(UserConst.SEND_FINISH,"INFO");
                } catch (IOException e) {
                    alert(UserConst.SERVER_CLOSED,"INFO");
                }

            }


        }
        @Override
        public void deleteFriend(DeleteFriendObject obj, String act) {
            // TODO: 2020/10/11 将IOUtils改写为dispatcher,需要将数据发送到服务器再接受回来
            if (act.equals(Act.SEND)){
                try {
                    MyIOUtils.send(obj,socket.getOutputStream());
                } catch (IOException e) {
                    alert(UserConst.SERVER_CLOSED,"INFO");
                }
            }else{
                String[] recv = obj.getFriends();
                IOGUIUtils.recv(friends,recv);
                singleChange(friends.size()>0);
            }

        }

        @Override
        public void addFriend(AddFriendObject obj, String act) {

            // TODO: 2020/10/11 发送数据到服务器，再接受最新的friends数据
            if (act.equals(Act.SEND)){
                try {
                    MyIOUtils.send(obj,socket.getOutputStream());
                } catch (IOException e) {
                    alert(UserConst.SERVER_CLOSED,"INFO");
                }
            }else{
                String[] recv = obj.getFriends();
                IOGUIUtils.recv(friends,recv);
                singleChange(friends.size()>0);
            }
        }

        @Override
        public void chat(ALlMessageObject obj,String act) {
            if (act.equals(Act.RECV)){
                if (obj instanceof SingleChatObj){
                    IOGUIUtils.recv(recv, (SingleChatObj) obj);
                }
                else{
                    IOGUIUtils.recv(recv,obj.getMsg());
                }
            }else{
                if (obj instanceof SingleChatObj){
                    try {
                        MyIOUtils.send(obj,socket.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recv.append("我->"+((SingleChatObj) obj).getTo()+":"+send.getText()+"\n");
                }else{
                    try {
                        MyIOUtils.send(obj,socket.getOutputStream());
                    } catch (IOException e) {
                        alert(UserConst.SERVER_CLOSED,"INFO");
                    }
                    recv.append("我:"+obj.getMsg()+"\n");
                }
                send.setText("");

            }
        }

        @Override
        public void refresh(RefreshObject obj,String act) {
                if (act.equals(Act.SEND)){
                    try {
                        MyIOUtils.send(obj,socket.getOutputStream());
                    } catch (IOException e) {
                        alert(UserConst.SERVER_CLOSED,"INFO");
                    }
                }else {
                    IOGUIUtils.recv(online, obj.getOnline());
                    IOGUIUtils.recv(friends, obj.getFriends());
                    onlineChange(online.size() > 1);
                    singleChange(friends.size() > 0);
                }

        }

        @Override
        public void introduceSelf(IntroduceObject obj) {
            try {
                MyIOUtils.send(obj,this.socket.getOutputStream());
            } catch (IOException e) {
                alert(UserConst.SERVER_CLOSED,"INFO");
            }
        }
    }
}
