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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;


public class ReceiveMessageActivity extends ActionBarActivity implements IServerMessageReceived, INSDHelperServerThreadInitComplete
{
    private static final String TAG = "Debug_ReceiveMsgAct";
    private NSDHelper nsdHelper;
    private NsdManager nsdManager;

    private static final String SERVICE_TYPE = "_http._tcp.";
    private static final String SERVICE_NAME = "NSD_Test";

    private CreateServerAsyncTask serverAsyncTask;

    private ArrayList<String> messages;
    private ArrayAdapter<String> messagesAdapter;
    private ListView messagesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_message);

        try
        {
            nsdManager = (NsdManager) this.getSystemService(NSD_SERVICE);
            nsdHelper = new NSDHelper(nsdManager);
            nsdHelper.serverThreadInitComplete = this;
            nsdHelper.registerService(SERVICE_NAME, SERVICE_TYPE);

            messages = new ArrayList<>();
            messagesAdapter = new ArrayAdapter<String>(this,
                    R.layout.message_view_layout,
                    R.id.messageTextView,
                    messages);

            messagesListView = (ListView) this.findViewById(R.id.listView);
            messagesListView.setAdapter(messagesAdapter);
        }
        catch (IOException | IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy()
    {
        try
        {
            nsdHelper.tearDown();
        }
        catch (IllegalArgumentException e)
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
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_receive_message, menu);
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

    // A message was received on the background client thread.
    // Update UI ListView
    @Override
    public void onServerMessageReceived(String msg)
    {
        messages.add(msg);
        messagesAdapter.notifyDataSetChanged();
    }

    // Server Thread Initialized
    // now initialise network functions
    @Override
    public void onServerThreadInit(CreateServerAsyncTask createServerAsyncTask)
    {
        Log.d(TAG, "Server Thread Init in RcdMsgAct");
        serverAsyncTask = createServerAsyncTask;
        serverAsyncTask.serverMessageReceived = this;
    }
}
