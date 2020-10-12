package com.me.Client.Core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.Const.*;

import com.me.Interface.Act;
import com.me.Interface.ClientInterface;
import com.me.Interface.Dispatcher;
import com.me.Interface.Message;
import com.me.utils.Logger;
import com.me.utils.MyIOUtils;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractClientDispatcher implements Dispatcher, ClientInterface {
        private boolean running = true;
        public Socket socket;
        private final String name;
        AbstractClientDispatcher(String name,Socket socket){
            this.socket = socket;
            this.name = name;
        }

        @Override
        public void run() {
            while (this.running){
                this.dispatch();
            }
        }

//        @Override
//        public void dispatch() {
//            try {
//                String recv = MyIOUtils.recv(socket.getInputStream());
//                ObjectMapper mapper = new ObjectMapper();
//                try {
//                    String[] strings = mapper.readValue(recv, String[].class);
//                    listRecv(strings);
//
//                } catch (JsonProcessingException ignore) {
//                    try {
//                         SingleFileObj file = mapper.readValue(recv, SingleFileObj.class);
//                        fileRecv(file);
//                    } catch (JsonProcessingException e) {
//                        try {
//                            SingleChatObj chat = mapper.readValue(recv, SingleChatObj.class);
//                            singleRecv(chat);
//                        } catch (JsonProcessingException jsonProcessingException) {
//                            this.recv(recv);
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                this.running = false;
//
//            }
//        }
        @Override
        public void dispatch() {
            try {
                Object o = MyIOUtils.recv(this.socket.getInputStream());
                    if (o instanceof ALlMessageObject){
                        chat(((ALlMessageObject) o),Act.RECV);
                    }else if (o instanceof RefreshObject){
                        refresh((RefreshObject) o,Act.RECV);
                    }
                    else if (o instanceof SingleFileObj){
                        singleFile((SingleFileObj) o, Act.RECV);
                    } else if (o instanceof AddFriendObject){
                        addFriend((AddFriendObject) o,Act.RECV);
                    }else if (o instanceof DeleteFriendObject){
                        deleteFriend((DeleteFriendObject) o,Act.RECV);
                    }
            } catch (IOException e) {
                this.running = false;
            }catch (ClassNotFoundException e){
                this.running = false;
                try {
                    this.socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }




    //        public abstract void listRecv(String[] recv);
//        public abstract void fileRecv(SingleFileObj file);
//        public abstract void singleRecv(SingleChatObj chat);
//        public abstract void recv(String msg);

}
