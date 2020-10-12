package com.me.Interface;

import com.me.Const.*;

public interface ClientInterface {
    /**
     *
     * @param obj 文件对象
     * @param act 发送还是接收
     */
    void singleFile(SingleFileObj obj, String act);

    /**
     *
     * @param obj
     * @param act
     */
    void deleteFriend(DeleteFriendObject obj, String act);
    void addFriend(AddFriendObject obj, String act);
    void chat(ALlMessageObject obj,String act);

    /**
     * 用于获取当前在线的所有用户,还有某位用户对应的朋友
     * @param obj
     * @param act
     */
    void refresh(RefreshObject obj,String act);
    void introduceSelf(IntroduceObject obj);
}
