package com.me.Const;

import com.me.Interface.Message;

import java.io.Serializable;
import java.util.Arrays;

public class DeleteFriendObject implements Message, Serializable {
    private String username;
    private String friend;
    private String[] friends;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String[] getFriends() {
        return friends;
    }

    public void setFriends(String[] friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "DeleteFriendObject{" +
                "username='" + username + '\'' +
                ", friend='" + friend + '\'' +
                ", friends=" + Arrays.toString(friends) +
                '}';
    }
}
