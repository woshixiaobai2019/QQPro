package com.me.Const;

import com.me.Interface.Message;

import java.io.Serializable;
import java.util.Arrays;

public class LoginObject implements Message, Serializable {
    private String username;
    private String password;
    private String login;
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


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "LoginObject{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}
