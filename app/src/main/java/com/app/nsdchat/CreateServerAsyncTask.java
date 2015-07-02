/*  Copyright 2015 XanderWraik

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.app.nsdchat;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CreateServerAsyncTask extends AsyncTask< Integer, String, Void >
{
    private static final String TAG = "Debug_CreateServerAsync";
    private static final String TAG_IO = "Server_IO";
    private int port;
    private ServerSocket serverSocket;
    private NetworkIO networkIO;
    public IServerMessageReceived serverMessageReceived;

    public CreateServerAsyncTask(int port, ServerSocket serverSocket)
    {
        this.port = port;
        this.serverSocket = serverSocket;
    }

    @Override
    protected Void doInBackground(Integer... params)
    {
        int finalI = params[0];
        try
        {
            Log.d(TAG, "Server Thread " + finalI + " Init");
            Socket connectionSocket = serverSocket.accept();
            connectionSocket.setReuseAddress(true);

            publishProgress("Thread " + finalI + " Connected to: " + connectionSocket.getRemoteSocketAddress().toString());
            networkIO = new NetworkIO(connectionSocket);

            networkIO.writeDataToStream("I is Server");

            String msg = "";
            while( !msg.equals("stop") )
            {
                msg = networkIO.readDataFromStream();
                publishProgress("Thread " + finalI + " " + msg);
                Log.d(TAG_IO, finalI + " " + msg);
            }
        }
        catch (IOException e) { e.printStackTrace(); }

        try { networkIO.closeStreams(); }
        catch (IOException | NullPointerException e) { e.printStackTrace(); }

        return null;
    }

    //Updates the UI in ReceivedMessageActivity when a new message is received
    protected void onProgressUpdate(String... values)
    {
        Log.d(TAG, "Message Update.");
        serverMessageReceived.onServerMessageReceived(values[0]);
    }
}
