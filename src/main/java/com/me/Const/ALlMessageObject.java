package com.me.Const;

import com.me.Interface.Message;

import java.io.Serializable;

public class ALlMessageObject implements Message , Serializable {
    String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ALlMessageObject{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
