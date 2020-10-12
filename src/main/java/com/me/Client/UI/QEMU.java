package com.me.Client.UI;//模拟qq登录窗口
import com.me.Client.Core.Client;
import com.me.Const.LoginObject;
import com.me.Const.SignObject;
import com.me.Const.UserConst;
import com.me.Interface.Act;
import com.me.utils.ClientInitUtils;

import java.io.*;
import java.awt.event.*;
import java.net.Socket;
import javax.swing.*;

public class QEMU extends CommonUI{
    private String user;
    private SignFace signFace;
    //    private final UserService userService = new UserServiceImpl();
    public QEMU() throws IOException {
        super("登录",new Socket(ClientInitUtils.getHost(),ClientInitUtils.getPort()),"login");
    }
    public QEMU(Socket socket) throws IOException {
        super("登录",socket,"login");
    }
    @Override
    protected void action(ActionEvent e) {
        //获取产生事件的事件源强制转换
        JButton bt = (JButton)e.getSource();
        //获取按钮上显示的文本
        String str = bt.getText();
        if(this.name.equals(str))
        {
            if(checkIsNull())
            {
                //获取用户所输入的用户名
                user = userTxt.getText().trim();
                //获取用户所输入的密码
                String pwd = pwdTxt.getText().trim();

//                String login = userService.login(user, pwd);//
                // TODO: 2020/10/11 修改成从服务端请求数据
                loginRequest(user,pwd);

            }
        }
        else
        {
            //调用注册
            try {
                if (signFace ==null) {
                    signFace = new SignFace(new Socket(ClientInitUtils.getHost(),ClientInitUtils.getPort()), this);//注册和登录用同一个uiCore
                }else{
                    signFace.setVisible(true);
                }
                this.setVisible(false);
            } catch (IOException ioException) {
                alert(UserConst.CREAT_WINDOW_FAILED,"ERROR");
            }
        }
    }

    @Override
    protected void signRequestBack(SignObject obj) {
        signFace.signRequestBack(obj);
    }

    private void loginRequest(String username,String password){
        String code = verCodeTxt.getText().trim();
        if (checkCode(code)){
            LoginObject loginObject = new LoginObject();
            loginObject.setUsername(username);
            loginObject.setPassword(password);
            uiCore.login(loginObject, Act.SEND);
        }else{
            alert(UserConst.CHECK_COED_ERROR,"ERROR");
        }
    }
    @Override
    protected void loginRequestBack(LoginObject obj) {
        String login = obj.getLogin();
        if(login.equals(UserConst.LOGIN_SUCCESS))
        {
            // TODO: 2020/10/11 缺少验证用户是否在线
            //隐藏当前登录窗口
            //验证成功创建一个主窗口
            this.alert(login,"SUCCESS");
            try {
                this.client = new Client(this.user,new Socket(ClientInitUtils.getHost(),ClientInitUtils.getPort())); //如果成功了就把这个socket给客户端
                this.dispose();
                this.uiCore.socket.close();
                if (this.signFace!=null) {
                    this.signFace.uiCore.socket.close();
                }
            } catch (IOException ioException) {
                alert(UserConst.CREAT_WINDOW_FAILED,"ERROR");
            }
        }else{
            alert(login,"INFO");
        }
    }

}





