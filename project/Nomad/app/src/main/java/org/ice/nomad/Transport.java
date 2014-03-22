package org.ice.nomad;

import android.os.Message;
import android.os.Handler;

import org.ice.net.Line;

public class Transport extends Thread
{
    private boolean isOk;
    private boolean newOrder;
    private String buffer;
    private String cmd;
    private String supass;

    private Line line;
    private Handler bridge;

    public Transport(Line line, Handler bridge)
    {
        this.line = line;
        this.bridge = bridge;

        this.isOk = true;
        this.newOrder = false;
    }

    public void run()
    {
        this.isOk = this.line.connect();

        if(!this.isOk)
            this.fixMsg("$");

        while(this.isOk)
        {
            if(this.newOrder)
            {
                this.newOrder = false;

                try
                {
                    Thread.sleep(100);

                    this.buffer = this.line.send(this.cmd, this.supass);
                    this.fixMsg(this.buffer);

                }
                catch (Exception ex)
                {/*---*/}
            }
        }

        this.line.disconnect();
    }

    public void send(String cmd, String supass)
    {
        this.buffer = "";
        this.supass = supass;
        this.cmd = cmd;
        this.newOrder = true;
    }

    public void finishIt()
    {
        this.isOk = false;
    }

    private void fixMsg(String data)
    {
        Message msg = new Message();
        msg.obj = data;
        this.bridge.sendMessage(msg);
    }
}
