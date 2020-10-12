package com.me.Const;

import com.me.Interface.Message;

import java.io.Serializable;

public class IntroduceObject implements Message, Serializable {
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
