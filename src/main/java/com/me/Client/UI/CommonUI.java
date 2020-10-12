package com.me.Client.UI;

import com.me.Client.Core.Client;
import com.me.Client.UI.Core.AbstractUIDispatcher;
import com.me.Const.LoginObject;
import com.me.Const.SignObject;
import com.me.Const.UserConst;
import com.me.Interface.Act;
import com.me.utils.MyIOUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;

public abstract class CommonUI extends JFrame implements ActionListener {
    protected UICore uiCore;
    protected JLabel userLa;
    protected JLabel pwdLa;
    protected JLabel verCodeLa;//验证码
    protected JTextField userTxt;
    protected JPasswordField pwdTxt;
    protected JTextField verCodeTxt;//验证码
    protected JButton sureBt;
    protected JButton quitBt;
    protected Mane mp;
    protected String name;
    protected Client client;
    protected Thread thread;
    //构造方法
    public CommonUI(String name,Socket socket,String component) throws IOException {
        this.name = name;
        this.uiCore = new UICore(name,socket);
        Init();
        this.thread = new Thread(uiCore, component);
        this.thread.start();
    }
    public void Init()
    {
        Frame frame = new Frame("QQ"+this.name);

        //创建出控件对象（因为上面只是声明出来，并没有给出实际的空间)

        //用户文本
        userLa = new JLabel();
        userLa.setText("用户名：");
        userLa.setSize(60, 50);
        userLa.setLocation(100, 80);

        //密码文本
        pwdLa = new JLabel();
        pwdLa.setText("密码：");
        pwdLa.setSize(50, 50);
        pwdLa.setLocation(100, 120);

        //用户输入框
        userTxt = new JTextField();
        userTxt.setSize(100, 20);
        //this.setSize(width, height)
        userTxt.setLocation(170, 95);

        //密码输入框
        pwdTxt = new JPasswordField();
        pwdTxt.setSize(100, 20);
        pwdTxt.setLocation(170, 135);

        //确认按钮
        sureBt = new JButton(this.name);
        sureBt.setSize(60, 25);
        sureBt.setLocation(135, 260);

        //退出按钮
        quitBt = new JButton("注册");
        quitBt.setSize(60, 25);
        quitBt.setLocation(240, 260);

        //验证码文本
        verCodeLa = new JLabel();
        verCodeLa.setText("验证码：");
        verCodeLa.setSize(60, 50);
        verCodeLa.setLocation(100,165);

        //验证码文本框
        verCodeTxt = new JTextField();
        verCodeTxt.setSize(100, 20);
        verCodeTxt.setLocation(170, 180);

        //验证码
        mp = new Mane();
        mp.setSize(100, 30);
        mp.setLocation(280, 175);

        this.setLayout(null);
        this.setSize(500, 400);
        this.add(userLa);
        this.add(pwdLa);
        this.add(userTxt);
        this.add(pwdTxt);
        this.add(sureBt);
        this.add(quitBt);
        this.add(verCodeLa);
        this.add(verCodeTxt);
        this.add(mp);
        sureBt.addActionListener(this);
        quitBt.addActionListener(this);
        this.addWindowListener(new CloseListener());
        this.setLocation(800,400);
        this.setVisible(true);
    }
    protected abstract void action(ActionEvent e);

    //切换验证码
    @Override
    public void actionPerformed(ActionEvent e)
    {
        action(e);
        mp.removeAll();
        mp.repaint();
        mp.updateUI();

//        this.Init();
    }
    protected boolean checkIsNull()
    {
        boolean flag = false;
        if(" ".equals(userTxt.getText().trim()))
        {
            flag = true;
        }
        else
        {
            if(" ".equals(pwdTxt.getText().trim()))
            {
                flag = true;
            }
        }
        return !flag;
    }

    /**
     * 检查验证码是否填写正确
     * @param code 用户填写的验证码
     * @return 正确就返回true
     */
    protected boolean checkCode(String code){
        return code.equalsIgnoreCase(mp.getSb().toString());
    }
    protected void alert(String msg,String type){
        //如果错误则弹出一个显示框
        JOptionPane pane = new JOptionPane(msg);
        JDialog dialog = pane.createDialog(this, type);
        dialog.show();
    }

    /**
     * 处理注册响应回来的请求
     * @param obj 服务器验证注册后返回的数据
     */
    protected abstract void signRequestBack(SignObject obj);

    /**
     * 处理登录回来的相应请求,
     * @param obj
     */
    protected abstract void loginRequestBack(LoginObject obj);
    class CloseListener implements WindowListener {
        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            try {
                uiCore.socket.close();
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
    class UICore extends AbstractUIDispatcher{
        UICore(String name,Socket socket) throws IOException {
            super(name,socket);

        }
        @Override
        public void login(LoginObject obj, String act) {
            if (act.equals(Act.SEND)){
                try {
                    MyIOUtils.send(obj,this.socket.getOutputStream());
                }  catch (IOException e) {
                    alert(UserConst.SERVER_CLOSED,"INFO");
                }
            }else{
                loginRequestBack(obj);
            }
        }

        @Override
        public void sign(SignObject obj, String act) {
            if (act.equals(Act.SEND)){
                try {
                    MyIOUtils.send(obj,socket.getOutputStream());
                } catch (IOException e) {
                    alert(UserConst.SERVER_CLOSED,"INFO");
                }
            }else{
                signRequestBack(obj);
            }
        }


    }
}
