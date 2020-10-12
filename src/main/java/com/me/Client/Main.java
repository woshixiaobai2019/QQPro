package com.me.Client;

import com.me.Client.UI.QEMU;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new QEMU();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
