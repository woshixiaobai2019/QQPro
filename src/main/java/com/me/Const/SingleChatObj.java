package com.me.Const;

import com.me.Interface.Message;


public class SingleChatObj extends ALlMessageObject{
    private String from;
    private String to;

    public SingleChatObj() {

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
