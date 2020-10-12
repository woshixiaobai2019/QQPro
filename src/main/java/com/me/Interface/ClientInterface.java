package com.me.Interface;

import com.me.Const.*;

public interface ClientInterface {
    void singleFile(SingleFileObj obj, String act);
    void deleteFriend(DeleteFriendObject obj, String act);
    void addFriend(AddFriendObject obj, String act);
    void chat(ALlMessageObject obj,String act);
    void refresh(RefreshObject obj,String act);
    void introduceSelf(IntroduceObject obj);
}
