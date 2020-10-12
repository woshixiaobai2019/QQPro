package com.me.Server.dao;

import com.me.domain.User;

import java.util.List;

public interface UserDao {
    void sign(String username,String password);
    User login(String username, String password);
    User findUserByName(String username);

    void addFriend(String me,String username);

    void deleteFriend(String username, String friend);

    List<User> getFriends(String name);
}
