package com.me.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class SQLPoolHelper {
    /**
     * 数据库连接池的工具类
     */
    private static Connection conn =null;
    private static DataSource ds = null;
    static{
        Properties pro = new Properties();
        //属性对象
        InputStream is = SQLPoolHelper.class.getClassLoader().getResourceAsStream("druid.properties");
        //获取文件流
        try {
            pro.load(is); //加载配置文件
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ds = DruidDataSourceFactory.createDataSource(pro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Connection getConn(){
        try {
            if(ds!=null) {
                conn = ds.getConnection();
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return conn;
    }
    public static DataSource getDs(){
        return ds;
    }

    public static void close(Connection conn,PreparedStatement pstm,ResultSet res){
        if(res!=null){
            try {
                res.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(pstm!=null){
            try {
                pstm.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
