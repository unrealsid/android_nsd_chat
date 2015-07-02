package com.app.nsdchat;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.net.Socket;

public class CreateClientAsyncTask extends AsyncTask<NetworkDataContainer, String, NetworkDataContainer>
{
    private static final String TAG = "CreateClientAsync_Debug";
    private Socket socket;
    private NetworkIO networkIO;
    public IClientNetworkTaskComplete networkTaskComplete;

    @Override
    protected NetworkDataContainer doInBackground(NetworkDataContainer... params)
    {
        try
        {
            publishProgress("Starting Client Request");
            socket = new Socket(params[0].addr, params[0].port );
            publishProgress("Connected to: " + socket.getRemoteSocketAddress());
            networkIO = new NetworkIO(socket);

            Log.d(TAG, "Init Message: " + networkIO.readDataFromStream());
            networkIO.writeDataToStream("Send from mI client.");
        }
        catch (IOException e) { e.printStackTrace(); }

        return new NetworkDataContainer(socket, networkIO);
    }

    @Override
    protected void onProgressUpdate(String... values) { Log.d(TAG, values[0]); }

    @Override
    protected void onPostExecute(NetworkDataContainer networkDataContainer)
    {
        Log.d(TAG, "Network Task Complete. Connected to server.");
        networkTaskComplete.onClientNetworkTaskComplete(networkDataContainer);
    }
}