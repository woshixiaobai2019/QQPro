package com.me.Server.Core;
import com.me.Const.*;
import com.me.Interface.*;
import com.me.utils.Logger;
import com.me.utils.MyIOUtils;


import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public abstract class AbstractServerDispatcher implements Dispatcher, UI, ClientInterface{
    protected boolean running = true;
    protected final Socket socket;
    protected String name;
    protected String type;
    AbstractServerDispatcher(Socket socket){
        this.socket = socket;
    }
    @Override
    public void dispatch() {
        try {
            Object  o = MyIOUtils.recv(this.socket.getInputStream());
            if (o instanceof SingleChatObj) {
                SingleChatObj singleChatObj = (SingleChatObj) o;
                Logger.info("SingleChat:" + singleChatObj.getFrom() + "->" + singleChatObj.getTo() + "MSG::" + singleChatObj.getMsg());
                singleChat(singleChatObj);
            } else if (o instanceof ALlMessageObject) {
                ALlMessageObject aLlMessageObject = (ALlMessageObject) o;
                Logger.info("AllMessageObject:" + aLlMessageObject.getMsg());
                spread(aLlMessageObject, false, Act.RECV);
            } else if (o instanceof SingleFileObj) {
                Logger.info("SingleFileObj:");
                SingleFileObj singleFileObj = (SingleFileObj) o;
                Logger.info("::FILE:" + singleFileObj + "\nFILE_SIZE:" + MyIOUtils.fileSizeFormat(singleFileObj.getSize()));
                singleFile(singleFileObj, Act.RECV);
            } else if (o instanceof LoginObject) {
                Logger.info("LoginObject:" + ((LoginObject) o).getUsername() + "登录");
                login((LoginObject) o, Act.RECV);
            } else if (o instanceof SignObject) {
                Logger.info("SignObject:" + ((SignObject) o).getUsername() + "注册" + "-密码:" + ((SignObject) o).getPassword());
                sign((SignObject) o, Act.RECV);
            } else if (o instanceof AddFriendObject) {
                Logger.info("AddFriendObject:" + ((AddFriendObject) o).getUsername() + "添加" + ((AddFriendObject) o).getFriend() + "为好友");
                AddFriendObject addFriendObject = (AddFriendObject) o;
                addFriend(addFriendObject, Act.RECV);
            } else if (o instanceof DeleteFriendObject) {
                Logger.info("DeleteFriendObject:" + ((DeleteFriendObject) o).getUsername() + "从好友中删除" + ((DeleteFriendObject) o).getFriend());
                deleteFriend((DeleteFriendObject) o, Act.RECV);
            } else if (o instanceof IntroduceObject) {
                IntroduceObject obj = (IntroduceObject) o;
                if (obj.getType().equals(TransferType.user)){
                    Logger.info((obj).getName() + "登录成功");
                }else{
                    Logger.info((obj).getName() + "文件传输连接成功");
                    running = false;
                }
                introduceSelf((IntroduceObject) o);
            } else if (o instanceof SingleFileBackObj) {
                SingleFileBackObj obj = (SingleFileBackObj) o;
                Logger.info("SingleFileBackObj::" + obj);
                Logger.info((obj.getFrom() + "接受了" + obj.getTo() + "的文件?" + obj.isConfirm()));
                singleFileBack(obj, Act.RECV);
            }else{
                Logger.error("");
            }
        } catch (EOFException e){
            //是登录的socket断开-
            offLine_();
        } catch (IOException ioException) {
            offLine_();
            ioException.printStackTrace();
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

    /**
     * 处理私聊
     * @param obj
     */
    protected abstract void singleChat(SingleChatObj obj);

    /**
     * 处理群聊
     * @param obj
     * @param all 是否发给所有人,true就是发给包括自己的信息
     * @param act
     */
    protected abstract void spread(ALlMessageObject obj,boolean all,String act);

    /**
     * 结束与一个客户端的链接
     */
    protected abstract void offLine_();


}
