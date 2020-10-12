package com.me.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static void info(String msg){

        System.out.println(getTime()+"---INFO::"+msg);
    }
    public static void deBug(String msg){
        System.out.println(getTime()+"---DEBUG::"+msg);
    }
    public static void error(String msg){
        System.out.println(getTime()+"---ERROR::"+msg);
    }
    private static String getTime(){
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        return dateFormat.format(date);
    }
}
