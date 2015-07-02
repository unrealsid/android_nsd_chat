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
