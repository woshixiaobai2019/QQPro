package com.me.Client.UI.Core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.Const.LoginObject;
import com.me.Const.SignObject;
import com.me.Const.UserConst;
import com.me.Interface.Act;
import com.me.Interface.Dispatcher;
import com.me.Interface.Message;
import com.me.Interface.UI;
import com.me.utils.Logger;
import com.me.utils.MyIOUtils;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public abstract class AbstractUIDispatcher implements Dispatcher, UI {
    private boolean running = true; //是否运行
    public final Socket socket;
    private final String name;
    public AbstractUIDispatcher(String name,Socket socket){
        this.socket = socket;
        this.name = name;
    }
    @Override
    public void dispatch() {
        try {
            Object o = MyIOUtils.recv(this.socket.getInputStream()); //同步
            if (o instanceof LoginObject){
                login((LoginObject) o, Act.RECV);
            }
            else if (o instanceof SignObject) {
                sign((SignObject) o, Act.RECV);
            }
        }
        catch (IOException e) {
           running = false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param msg 弹窗的消息
     * @param type 弹窗的类型:ERROR INFO
     */

    @Override
    public void run() {
        while (running){
            dispatch();
        }
    }

}
