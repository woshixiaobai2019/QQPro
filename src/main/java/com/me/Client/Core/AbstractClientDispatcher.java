package com.me.Client.Core;

import com.me.Const.*;
import com.me.Interface.Act;
import com.me.Interface.ClientInterface;
import com.me.Interface.Dispatcher;
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

}
