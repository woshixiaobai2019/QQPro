package com.me.Server;

import com.me.Server.Core.SocketServer;
import com.me.utils.Logger;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new SocketServer();
        } catch (IOException e) {
            Logger.error(e.getMessage());
            System.exit(0);
        }
    }
}
