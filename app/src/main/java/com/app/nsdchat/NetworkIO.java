package com.app.nsdchat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkIO
{
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;

    public DataInputStream getInputStream() { return inputStream; }
    public DataOutputStream getOutputStream() { return outputStream; }

    public NetworkIO(Socket socket) throws IOException
    {
        inputStream = new DataInputStream( socket.getInputStream() );
        outputStream = new DataOutputStream( socket.getOutputStream() );
    }

    public void writeDataToStream(String message) throws IOException
    {
        outputStream.writeUTF(message);
        outputStream.flush();
    }

    public String readDataFromStream() throws IOException
    {
        return inputStream.readUTF();
    }

    public void closeStreams() throws IOException
    {
        this.inputStream.close();
        this.outputStream.close();
    }
}
