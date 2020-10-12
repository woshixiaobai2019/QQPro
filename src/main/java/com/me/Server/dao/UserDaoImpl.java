package com.me.Server.dao;

import com.me.domain.User;
import com.me.utils.SQLPoolHelper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class UserDaoImpl implements UserDao{
    private final JdbcTemplate template = new JdbcTemplate(SQLPoolHelper.getDs());
    @Override
    public void sign(String username, String password) {
        String sql = "insert into user(username,password) values(?,?)";
        template.update(sql,username,password);
    }

    @Override
    public User login(String username, String password) {
        User user = null;
        String sql = "select * from user where username = ? and password = ?";
        try {
            user = template.queryForObject(sql,new BeanPropertyRowMapper<User>(User.class),username,password);
        } catch (DataAccessException ignored) {
            ;
        }
        return user;
    }

    @Override
    public User findUserByName(String username) {
        String sql = "select * from user where username =?";
        User user = null;
        try {
            user = template.queryForObject(sql, new BeanPropertyRowMapper<>(User.class),username);
        } catch (DataAccessException ignore) {

        }
        return user;
    }

    @Override
    public void addFriend(String me,String username) {
        User user = findUserByName(username);
        User m = findUserByName(me);
        Integer id = user.getId();
        Integer meId = m.getId();
        String sql = "insert into friends(who,own) values(?,?)";
        template.update(sql,id,meId);
    }

    @Override
    public void deleteFriend(String username, String friend) {
        User user = findUserByName(friend);
        User m = findUserByName(username);
        Integer id = user.getId();
        Integer meId = m.getId();
        String sql = "delete from friends where who = ? and own =?";
        template.update(sql,id,meId);
    }

    @Override
    public List<User> getFriends(String name) {
        User userByName = findUserByName(name);
        Integer id = userByName.getId();
        String sql = "select * from user where id in (select who from friends where own = ?)";
        return template.query(sql, new BeanPropertyRowMapper<User>(User.class), id);
    }
}
