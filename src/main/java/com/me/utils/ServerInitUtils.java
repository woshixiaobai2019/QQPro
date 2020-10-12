package com.me.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerInitUtils {
    public static Properties pro;
    static {
        pro = new Properties();
        //属性对象
        InputStream is = SQLPoolHelper.class.getClassLoader().getResourceAsStream("server.properties");
        //获取文件流
        try {
            pro.load(is); //加载配置文件
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static int getPort(){
        String port = pro.getProperty("port");
        return Integer.parseInt(port);
    }

}
