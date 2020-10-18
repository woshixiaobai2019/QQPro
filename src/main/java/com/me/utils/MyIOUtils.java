package com.me.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.Const.UserConst;

import java.io.*;

public class MyIOUtils {
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
        byte[] content = new byte[UserConst.batch_size]; //每次最大传输100k
        while (readSize<size){
            read=is.read(content);
            readSize += Math.max(read, 0);
            Logger.info("batchRead:"+MyIOUtils.fileSizeFormat(read));
            Logger.info("readSize:"+MyIOUtils.fileSizeFormat(readSize));
            os.write(content,0,read);
            os.flush();
        }
        if (readSize == size){
            Logger.info("服务器接收文件完成");
        }else {
            Logger.error("文件传输过程中损坏");
        }
    }
    public static String fileSizeFormat(long size){
        if (size <1024){
            return size+"B";
        }else if(size<1024*1024){
            return size/1024 +"kb";
        }else if (size<1024*1024*1024){
            return size/1024/1024+"mb";
        }else{
            return size/1024/1024/1024+"gb";
        }
    }

}
