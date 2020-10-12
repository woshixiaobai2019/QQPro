package com.me.Server.Service;

import com.me.domain.User;

import java.util.List;

public interface UserService {
    String sign(String username,String password);
    String login(String username,String password);

    void addFriend(String me,String username);

    void deleteFriend(String username, String friend);

    List<User> getFriends(String name);
}
