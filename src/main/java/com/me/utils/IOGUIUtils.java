package com.me.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.Const.ALlMessageObject;
import com.me.Const.SingleChatObj;
import com.me.Const.SingleFileObj;
import com.me.Const.UserConst;

import javax.swing.*;
import java.io.*;

public class IOGUIUtils {
    public static final String prefix = System.getProperty("user.dir");
    public static final ObjectMapper mapper = new ObjectMapper();
    public static StringBuilder sendHistory = new StringBuilder();
    public static StringBuilder recvHistory = new StringBuilder();
    public static void input(JTextArea textArea,String msg){
        textArea.setText(msg);
    }
    public static void recv(JTextArea textArea, String s){
        recvHistory.append(s).append("\n");
        textArea.append(s+"\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    public static void recv(DefaultListModel<String> jList, String[] s) {
        jList.clear();
        for (String s1 : s) {
            jList.addElement(s1);
        }

    }

    public static void recv(JTextArea jTextArea,SingleChatObj obj){
        String s = obj.getFrom() + "->我:" + obj.getMsg();
        recvHistory.append(s);
        jTextArea.append(s+"\n");
    }

    public static String recv(SingleFileObj obj,InputStream is) throws IOException{
        String fileName = obj.getFileName();

        File file = new File(prefix + File.separator + obj.getFrom() + File.separator + fileName);
        if (!file.exists()){
            boolean mkdirs = file.getParentFile().mkdirs();
        }
        boolean newFile = file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        long read_size = 0;
        long file_size = obj.getSize();
        byte[] content = new byte[1024 * 500];
        int read = 0;
        while (read_size<file_size){
            read=is.read(content);
            fos.write(content,0,read);
            fos.flush();
            read_size += Math.max(read, 0);
        }
        fos.close();
        if (read_size == file_size){
            return UserConst.RECV_FINNISH;
        }else{
            return UserConst.FILE_BROKEN;
        }


    }

//    public static void send(JTextArea textArea, OutputStream os) throws IOException {
//        String text = textArea.getText();
//        sendHistory.append(text);
//        DataOutputStream dos = new DataOutputStream(os);
//        dos.writeUTF(text);
//        dos.flush();
//    }
//    public static void send(String text, OutputStream os) throws IOException {
//        sendHistory.append(text);
//        DataOutputStream dos = new DataOutputStream(os);
//        dos.writeUTF(text);
//        dos.flush();
//    }
//    public static void send(ALlMessageObject obj,OutputStream os) throws IOException {
//        String s = mapper.writeValueAsString(obj);
//        DataOutputStream outputStream = new DataOutputStream(os);
//        outputStream.writeUTF(s);
//        outputStream.flush();
//    }
//    public static void send(JTextArea textArea,ALlMessageObject obj,OutputStream os) throws IOException {
//        obj.setMsg(textArea.getText());
//        try {
//            String s = mapper.writeValueAsString(obj);
//            DataOutputStream outputStream = new DataOutputStream(os);
//            outputStream.writeUTF(s);
//            outputStream.flush();
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        //私聊
//    }

    public static void fileTransfer(String path,OutputStream os) throws IOException{
        FileInputStream fis = new FileInputStream(path);
        byte[] content = new byte[1024 * 500]; //每次读500k
        int read;
        while ((read=fis.read(content))!=-1){
            os.write(content,0,read);
            os.flush();
        }
        fis.close();
    }

    public static void send(SingleFileObj obj, OutputStream os) throws IOException {
        ObjectOutputStream ous = new ObjectOutputStream(os);
        ous.writeObject(obj);
        ous.flush(); //发送文件信息对象
        fileTransfer(obj.getPath(), os);
    }

}
