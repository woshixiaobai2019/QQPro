package com.me.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.Const.ALlMessageObject;
import com.me.Const.LoginObject;
import com.me.Const.RefreshObject;
import com.me.Const.SignObject;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.lang.ref.Reference;

public class MyIOUtils {
    public static final ObjectMapper mapper = new ObjectMapper();
    public static String input(String comment,InputStream is) throws IOException{
        //获取键盘输入数据
        System.out.print(comment);
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        return bf.readLine();

    }
    public static Object recv(InputStream is) throws IOException, ClassNotFoundException {
        //用于接收socket的消息
        DataInputStream di = new DataInputStream(is);
        ObjectInputStream objectInputStream = new ObjectInputStream(di);
        return objectInputStream.readObject();
    }

    public static void send(String msg,OutputStream os) throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        out.writeUTF(msg);
        out.flush();
    }
    public static void send(Object obj,OutputStream os) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
    }
//    public static void send(RefreshObject obj,OutputStream os)throws IOException{
//        DataOutputStream ous = new DataOutputStream(os);
//        ous.writeUTF(mapper.writeValueAsString(obj));
//        ous.flush();
//    }

    public static void swap(InputStream is,OutputStream os,long size) throws IOException {
        long readSize = 0;
        int read;
        byte[] content = new byte[1024 * 500]; //每次最大传输1k
        while (readSize<size){
            read=is.read(content);
            readSize += Math.max(read, 0);
            os.write(content,0,read);
            os.flush();
            Logger.info("batchRead:"+read/1024 +"kb");
            Logger.info("readSize:"+readSize/1024 +"kb");
        }
        if (readSize == size){
            Logger.info("服务器接收文件完成");
        }else {
            Logger.error("文件传输过程中损坏");
        }
    }


//    public static void send(ALlMessageObject obj, OutputStream os)throws IOException {
//        DataOutputStream ous = new DataOutputStream(os);
//        ous.writeUTF(mapper.writeValueAsString(obj));
//        ous.flush();
//    }
//
//    public static void send(LoginObject obj, OutputStream os)throws IOException {
//        DataOutputStream ous = new DataOutputStream(os);
//        ous.writeUTF(mapper.writeValueAsString(obj));
//        ous.flush();
//    }
//
//    public static void send(SignObject obj, OutputStream os) throws IOException{
//        DataOutputStream ous = new DataOutputStream(os);
//        ous.writeUTF(mapper.writeValueAsString(obj));
//        ous.flush();
//    }
}
