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

//Helper class for Network Service Discovery Operations on Android.
package com.app.nsdchat;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class NSDHelper
{
    private NsdManager manager = null;
    private NsdServiceInfo serviceInfo = null;
    private NsdManager.RegistrationListener registrationListener = null;
    private NsdManager.DiscoveryListener discoveryListener = null;
    private NsdManager.ResolveListener resolveListener = null;

    private static final String TAG = "NSD_Helper_Debug";
    private static final String TAG_REGISTRATION = "NSD_Registration";
    private static final String TAG_DISCOVERY = "NSD_Discovery";
    private static final String TAG_RESOLVE = "NSD_Resolve";
    private static final int PORT_NUMBER = 30550;
    private static final int CLIENT_NUMBER = 1;

    private String SERVICE_TYPE = "";
    private String finalServiceName = "";
    private ServerSocket serverSocket;
    private CreateClientAsyncTask createClientAsyncTask;
    private CreateServerAsyncTask createServerAsyncTask;

    public INSDHelperServerThreadInitComplete serverThreadInitComplete;
    public INSDHelperClientThreadInitComplete clientThreadInitComplete;

    public NSDHelper(NsdManager manager) throws IOException
    {
        this.manager = manager;
        this.serviceInfo = new NsdServiceInfo();
        serverSocket = new ServerSocket();
    }

    //Getters
    public NsdManager getManager() { return manager; }
    public NsdServiceInfo getServiceInfo() { return serviceInfo; }
    public NsdManager.RegistrationListener getRegistrationListener() {return registrationListener;}
    public NsdManager.DiscoveryListener getDiscoveryListener() { return discoveryListener; }
    public NsdManager.ResolveListener getResolveListener() { return resolveListener;}
    public ServerSocket getServerSocket() { return serverSocket; }
    public CreateClientAsyncTask getCreateClientAsyncTask() { return createClientAsyncTask; }
    public CreateServerAsyncTask getCreateServerAsyncTask() { return createServerAsyncTask; }

    //
    //Server Ops
    //
    public void registerService(String serviceName, String serviceType) throws IOException
    {
        SERVICE_TYPE = serviceType;
        serviceInfo.setServiceName(serviceName);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(PORT_NUMBER);

        Log.d(TAG, "Port Number: " + PORT_NUMBER);

        manager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener = new NsdManager.RegistrationListener()
        {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode)
            {
                switch (errorCode) {
                    case NsdManager.FAILURE_ALREADY_ACTIVE:
                        Log.d(TAG_REGISTRATION, "Registration Failed. NSD Already Active");
                        break;
                    case NsdManager.FAILURE_INTERNAL_ERROR:
                        Log.d(TAG_REGISTRATION, "Registration Failed. Internal NSD Error.");
                        break;
                    case NsdManager.FAILURE_MAX_LIMIT:
                        Log.d(TAG_REGISTRATION, "Registration Failed. Max Limit Reached.");
                        break;
                    default:
                        Log.d(TAG_REGISTRATION, "Registration Error Occured.");
                        break;
                }
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                switch (errorCode) {
                    case NsdManager.FAILURE_ALREADY_ACTIVE:
                        Log.d(TAG_REGISTRATION, "UnRegistration Failed. NSD Already Active");
                        break;
                    case NsdManager.FAILURE_INTERNAL_ERROR:
                        Log.d(TAG_REGISTRATION, "UnRegistration Failed. Internal NSD Error.");
                        break;
                    case NsdManager.FAILURE_MAX_LIMIT:
                        Log.d(TAG_REGISTRATION, "UnRegistration Failed. Max Limit Reached.");
                        break;
                    default:
                        Log.d(TAG_REGISTRATION, "UnRegistration Error Occured.");
                        break;
                }
            }

            //
            //Server Ops
            //
            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo)
            {
                try
                {
                    if (serverSocket.isBound())
                    {
                        Log.d(TAG_REGISTRATION, "ServerSocket already bound to a local port. Can't open new one.");
                        return;
                    }

                    serverSocket = new ServerSocket(PORT_NUMBER);

                    Log.d(TAG_REGISTRATION, "Registration Succeeeded");
                    finalServiceName = serviceInfo.getServiceName();
                    Log.d(TAG_REGISTRATION, finalServiceName);

                    Log.d(TAG_REGISTRATION, serverSocket.getInetAddress().toString());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                for (int i = 0; i < CLIENT_NUMBER; i++)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    {
                        createServerAsyncTask = new CreateServerAsyncTask(PORT_NUMBER, serverSocket);
                        serverThreadInitComplete.onServerThreadInit(createServerAsyncTask);
                        createServerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
                    }
                    else
                    {
                        createServerAsyncTask = new CreateServerAsyncTask(PORT_NUMBER, serverSocket);
                        serverThreadInitComplete.onServerThreadInit(createServerAsyncTask);
                        createServerAsyncTask.execute(i);
                    }
                }
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo)
            {
                Log.d(TAG_REGISTRATION, "Service Unregistered");
            }
        });
    }

    //
    //Client Side Ops starting
    //
    public void discoverNetworkServices(String serviceType)
    {
        SERVICE_TYPE = serviceType;

        if (SERVICE_TYPE.equals("") || SERVICE_TYPE == null)
        {
            Log.d(TAG_DISCOVERY, "Service Name cannot be empty.");
            return;
        }

        manager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener = new NsdManager.DiscoveryListener()
        {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG_DISCOVERY, "Start Discovery Failed. " + errorCode);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG_DISCOVERY, "Stop Discovery Failed. " + errorCode);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(TAG_DISCOVERY, "Discovery Started");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG_DISCOVERY, "Discovery Stopped");
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d(TAG_DISCOVERY, "Service Found.");
                connectToService(serviceInfo);
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.d(TAG_DISCOVERY, "Service was lost.");
            }
        });
    }

    private void connectToService(NsdServiceInfo serviceInfo)
    {
        manager.resolveService(serviceInfo, resolveListener = new NsdManager.ResolveListener()
        {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode)
            {
                Log.d(TAG_RESOLVE, "Resolve Failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo)
            {
                InetAddress addr = serviceInfo.getHost();
                int port = serviceInfo.getPort();
                Log.d(TAG_RESOLVE, "Name of service is: "
                        + serviceInfo.getServiceName()
                        + " and host: "
                        + serviceInfo.getHost() + " "
                        + serviceInfo.getPort());

                //Connect to server
                createClientAsyncTask = new CreateClientAsyncTask();
                clientThreadInitComplete.onClientThreadInit(createClientAsyncTask);
                createClientAsyncTask.execute(new NetworkDataContainer(addr, port));
            }
        });
    }

    public void tearDown()
    {
        try
        {
            manager.unregisterService(registrationListener);
            manager.stopServiceDiscovery(discoveryListener);
            this.getServerSocket().close();
        }
        catch(IllegalArgumentException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public String getMachineIPAddress(Context context, MainActivity mainActivity)
    {
        WifiManager wm = (WifiManager) mainActivity.getSystemService(context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }
}
