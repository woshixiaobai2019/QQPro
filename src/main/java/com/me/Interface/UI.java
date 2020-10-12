package com.me.Interface;

import com.me.Const.LoginObject;
import com.me.Const.SignObject;

public interface UI {
    void sign(SignObject o, String recv);
    void login(LoginObject o, String recv);
}
