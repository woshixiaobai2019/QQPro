package com.me.Const;

import com.me.Interface.Message;

import java.io.Serializable;

public class LoginSuccessObject implements Message, Serializable {
    String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "LoginSuccessObject{" +
                "username='" + username + '\'' +
                '}';
    }
}
