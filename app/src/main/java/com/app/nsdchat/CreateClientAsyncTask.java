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