package com.me.Server.Core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.Const.*;
import com.me.Interface.*;
import com.me.utils.Logger;
import com.me.utils.MyIOUtils;


import java.io.IOException;
import java.net.Socket;

public abstract class AbstractServerDispatcher implements Dispatcher, UI, ClientInterface{
    protected boolean running = true;
    protected final Socket socket;
    private final ObjectMapper mapper = new ObjectMapper();
    AbstractServerDispatcher(Socket socket){
        this.socket = socket;
    }
    @Override
    public void dispatch() {
        try {
            Object o = MyIOUtils.recv(this.socket.getInputStream());

                if (o instanceof SingleChatObj){
                    Logger.info("SingleChat:");
                    SingleChatObj singleChatObj = (SingleChatObj) o;
                    singleChat(singleChatObj);
                }else if (o instanceof ALlMessageObject){
                    Logger.info("AllMessageObject:");
                    ALlMessageObject aLlMessageObject = (ALlMessageObject) o;
                    spread(aLlMessageObject,false,Act.RECV);
                }
                else if (o instanceof SingleFileObj){
                    Logger.info("SingleFileObj:");
                    SingleFileObj singleFileObj = (SingleFileObj) o;
                    singleFile(singleFileObj, Act.RECV);
                }
                else if (o instanceof  LoginObject){
                    Logger.info("LoginObject:");
                    login((LoginObject) o,Act.RECV);
                }
                else if (o instanceof SignObject){
                    Logger.info("SignObject:");
                    sign((SignObject) o,Act.RECV);
                }else if (o instanceof AddFriendObject){
                    Logger.info("AddFriendObject:");
                    AddFriendObject addFriendObject = (AddFriendObject) o;
                    addFriend(addFriendObject,Act.RECV);
                }else if (o instanceof DeleteFriendObject){
                    Logger.info("DeleteFriendObject:");
                    deleteFriend((DeleteFriendObject) o,Act.RECV);
                }else if (o instanceof IntroduceObject){
                    introduceSelf((IntroduceObject) o);
                }

            } catch (IOException ioException) {
            offLine_();
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        }
    }
    @Override
    public void run() {
        while (running){
            dispatch();
        }
    }
    protected abstract void singleChat(SingleChatObj obj);
    protected abstract void spread(ALlMessageObject obj,boolean all,String act);
    protected abstract void offLine_();


}
