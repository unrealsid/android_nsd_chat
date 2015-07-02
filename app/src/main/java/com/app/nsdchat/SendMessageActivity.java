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

import android.net.nsd.NsdManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;


public class SendMessageActivity extends ActionBarActivity implements IClientNetworkTaskComplete, INSDHelperClientThreadInitComplete
{
    private static final String TAG = "Debug_SndMsgAct";
    private NSDHelper nsdHelper;
    private NsdManager nsdManager;
    private static final String SERVICE_TYPE = "_http._tcp.";
    private static final String SERVICE_NAME = "NSD_Test";
    CreateClientAsyncTask clientAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        try
        {
            nsdManager = (NsdManager) this.getSystemService(NSD_SERVICE);
            nsdHelper = new NSDHelper(nsdManager);
            nsdHelper.clientThreadInitComplete = this;
            nsdHelper.discoverNetworkServices(SERVICE_TYPE);
        }
        catch (IOException | IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    protected void onPause()
    {
        try
        {
            if (nsdHelper != null || container.networkIO != null)
            {
                nsdHelper.tearDown();
                container.networkIO.closeStreams();
            }
        }
        catch(IllegalArgumentException | IOException | NullPointerException e)
        {
            e.printStackTrace();
        }

        super.onPause();
    }*/

    @Override
    protected void onDestroy()
    {
        try
        {
            nsdHelper.tearDown();
            container.networkIO.closeStreams();
        }
        catch (IOException | IllegalArgumentException | NullPointerException e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop()
    {
        try
        {
            nsdHelper.tearDown();
            container.networkIO.closeStreams();
        }
        catch (IOException | IllegalArgumentException | NullPointerException e)
        {
            e.printStackTrace();
        }
        super.onStop();
    }

    public void onMsgSend(View view)
    {
        if (container == null)
        {
            Toast.makeText(this, "Not connected to server yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText editTextMsg = (EditText) this.findViewById(R.id.msgEditText);
        final String msg = editTextMsg.getText().toString();
        editTextMsg.setText("");

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try { container.networkIO.writeDataToStream(msg); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }).start();
    }

    NetworkDataContainer container;
    @Override
    public void onClientNetworkTaskComplete(NetworkDataContainer networkDataContainer)
    {
        container = networkDataContainer;
    }

    // Client AsyncTask is complete
    // Init network functions

    @Override
    public void onClientThreadInit(CreateClientAsyncTask createClientAsyncTask)
    {
        Log.d(TAG, "Client thread init complete in SndMsgAct.");
        clientAsyncTask = createClientAsyncTask;
        clientAsyncTask.networkTaskComplete = this;
    }
}