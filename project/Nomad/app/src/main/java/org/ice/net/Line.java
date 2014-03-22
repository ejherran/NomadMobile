package org.ice.net;

import java.util.Properties;
import java.io.*;
import com.jcraft.jsch.*;

public class Line
{
    private String host;
    private int port;
    private String user;
    private String pass;

    private JSch tunel;
    private Session session;

    private boolean isConnected;
    private boolean isRoot;

    public String debug = new String();

    public Line(String host, int port, String user, String pass)
    {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;

        this.tunel = new JSch();
    }

    public boolean connect()
    {
        if(this.user.compareToIgnoreCase("root") != 0)
        {
            try
            {
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");

                this.session = this.tunel.getSession(this.user, this.host, this.port);
                this.session.setConfig(config);
                this.session.setPassword(this.pass);
                this.session.connect(30000);

                this.isConnected = true;
            }
            catch (Exception ex)
            {
                this.debug = ex.toString();
                this.isConnected = false;
            }
        }
        else
            this.isConnected = false;

        return this.isConnected;
    }

    public void disconnect()
    {
        if(this.isConnected)
            this.session.disconnect();
    }

    public String send(String cmd, String supass)
    {
        String res = new String();

        try
        {
            Channel channel = this.session.openChannel("exec");
            ((ChannelExec)channel).setPty(true);
            ((ChannelExec)channel).setCommand(cmd);

            InputStream in = channel.getInputStream();
            OutputStream out = channel.getOutputStream();
            channel.connect();

            if(supass.compareToIgnoreCase("") != 0)
            {
                out.write((supass+"\n").getBytes());
                out.flush();
            }

            byte[] tmp = new byte[1048576];

            while(true)
            {
                while(in.available() > 0)
                {
                    int i = in.read(tmp, 0, 1048576);

                    if(i < 0)
                        break;

                    String trim = new String(tmp);
                    res = res + trim.substring(0, i);
                }

                if(channel.isClosed())
                    break;
            }

            channel.disconnect();

        }
        catch (Exception ex)
        {
            res = "$";
        }

        String parts[] = res.split("\n");

        if(parts.length > 1)
        {
            if(parts[0].trim().compareToIgnoreCase(supass) == 0)
            {
                res = "";
                for(int i = 1; i < parts.length; i++ )
                    res = res + parts[i] + "\n";
                res = res.trim();
            }
        }

        return res;
    }
}

