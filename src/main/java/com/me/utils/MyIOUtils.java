package com.me.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;

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
    public static void send(Object obj,OutputStream os) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
    }

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

}
