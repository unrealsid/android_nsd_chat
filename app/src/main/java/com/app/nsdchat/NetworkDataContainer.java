package com.app.nsdchat;

import java.net.InetAddress;
import java.net.Socket;

public class NetworkDataContainer
{
    public InetAddress addr;
    public int port;
    public Socket socket;
    public NetworkIO networkIO;

    public NetworkDataContainer(Socket socket, NetworkIO networkIO)
    {
        this.addr = null;
        this.port = 0;
        this.socket = socket;
        this.networkIO = networkIO;
    }

    public NetworkDataContainer(InetAddress addr, int port)
    {
        this.addr = addr;
        this.port = port;
    }


}
