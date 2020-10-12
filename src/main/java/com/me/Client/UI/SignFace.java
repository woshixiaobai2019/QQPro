package com.me.Client.UI;

import com.me.Const.LoginObject;
import com.me.Const.SignObject;
import com.me.Const.UserConst;
import com.me.Interface.Act;
import com.me.Server.Service.UserService;
import com.me.Server.Service.UserServiceImpl;
import com.me.utils.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.Socket;

public class SignFace extends CommonUI{
    private final UserService userService = new UserServiceImpl();
    private QEMU qemu;
    public SignFace(Socket socket,QEMU qemu) throws IOException {
        super("注册",socket,"sign");
        this.qemu = qemu;
        this.quitBt.setText("返回");
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
                String user = userTxt.getText().trim();
                //获取用户所输入的密码
                String pwd = pwdTxt.getText().trim();
                signRequest(user,pwd);
            }
        }
        else
        {
            //验证成功创建一个主窗口
                this.qemu.setVisible(true);
                this.setVisible(false);
        }
    }

    @Override
    protected void signRequestBack(SignObject obj) {
        String sign = obj.getSign();
        if (sign.equals(UserConst.SIGN_SUCCESS)){
            this.alert(sign,"SUCCESS");
            //隐藏当前登录窗口
            this.setVisible(false);
            //验证成功创建一个主窗口
            this.qemu.setVisible(true);
        }else if(sign.equals(UserConst.HAVE_SIGNED)){
            this.alert(sign,"ERROR");
        } else{
            this.alert(UserConst.CHECK_COED_ERROR,"ERROR");
        }
    }

    @Override
    protected void loginRequestBack(LoginObject obj) {
        qemu.loginRequestBack(obj);
    }


    private void signRequest(String username,String password){
       String code = verCodeTxt.getText().trim();
       if (checkCode(code)) {
           SignObject signObject = new SignObject();
           signObject.setPassword(password);
           signObject.setUsername(username);
           uiCore.sign(signObject, Act.SEND);
       }else {
           alert(UserConst.CHECK_COED_ERROR,"ERROR");
       }
   }
}
