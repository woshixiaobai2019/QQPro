package com.me.Server.Service;

import com.me.Const.UserConst;
import com.me.Server.dao.UserDao;
import com.me.Server.dao.UserDaoImpl;
import com.me.domain.User;

import java.util.List;

public class UserServiceImpl implements UserService{
    private final UserDao userDao = new UserDaoImpl();

    @Override
    public void deleteFriend(String username, String friend) {
        userDao.deleteFriend(username,friend);
    }

    @Override
    public void addFriend(String me,String username) {
        userDao.addFriend(me,username);
    }

    @Override
    public String sign(String username, String password) {
        if (username==null || password ==null || "".equals(username) || "".equals(password)){
            return UserConst.SIGN_FAILED;
        }
        else if (null==userDao.findUserByName(username)) {
            userDao.sign(username,password);
            return UserConst.SIGN_SUCCESS;
        }
        else {
            return UserConst.HAVE_SIGNED;
        }

    }

    @Override
    public String login(String username, String password) {
        User login = userDao.login(username, password);
        if (login ==null){
            return UserConst.USER_NOT_EXIT;
        }
        else {
            return UserConst.LOGIN_SUCCESS;
        }
    }

    @Override
    public List<User> getFriends(String name) {
        return userDao.getFriends(name);

    }
}
