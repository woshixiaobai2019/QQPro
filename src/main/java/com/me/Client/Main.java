package com.me.Client;

import com.me.Client.UI.QEMU;
import com.me.utils.ClientInitUtils;
import com.me.utils.Logger;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length == 1){
                ClientInitUtils.setHost(args[0]);
            }
            new QEMU();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
