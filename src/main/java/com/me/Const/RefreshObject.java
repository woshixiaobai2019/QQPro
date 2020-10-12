package com.me.Const;

import com.me.Interface.Message;

import java.io.Serializable;
import java.util.Arrays;

public class RefreshObject implements Serializable, Message {
    private String[] online;
    private String[] friends;
    private String who;

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String[] getOnline() {
        return online;
    }

    public void setOnline(String[] online) {
        this.online = online;
    }

    public String[] getFriends() {
        return friends;
    }

    public void setFriends(String[] friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "RefreshObject{" +
                "online=" + Arrays.toString(online) +
                ", friends=" + Arrays.toString(friends) +
                ", who='" + who + '\'' +
                '}';
    }
}
