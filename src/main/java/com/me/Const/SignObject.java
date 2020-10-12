package com.me.Const;

import com.me.Interface.Message;

import java.io.Serializable;

public class SignObject implements Message, Serializable {
    private String username;
    private String password;

    private String sign;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "SignObject{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
