package org.ice.web;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.webkit.JavascriptInterface;

public class NomadInterface
{
    private Context context;
    private Handler link;

    public NomadInterface(Context context, Handler link)
    {
        this.context = context;
        this.link = link;
    }

    @JavascriptInterface
    public void showToast(String toast)
    {
        Toast.makeText(this.context, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void sysInfo()
    {
        Message msg = new Message();
        msg.arg1 = 101;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void cmLaunch(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 102;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void servStatus(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 103;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void rtServer()
    {
        Message msg = new Message();
        msg.arg1 = 104;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void sdServer()
    {
        Message msg = new Message();
        msg.arg1 = 105;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void firewall(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 106;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void logInsp(String log, String filter)
    {
        Message msg = new Message();
        if(filter.compareToIgnoreCase("*") == 0)
            msg.obj = log;
        else
            msg.obj = log+"|:|"+filter;
        msg.arg1 = 107;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void routes(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 108;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void sshCfg(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 109;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void mysqlCfg(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 110;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void ftpCfg(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 111;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void webCfg(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 112;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void mtaCfg(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 113;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void sambaCfg(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 114;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void squidCfg(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 115;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void dnsCfg(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 116;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void dhcpCfg(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 117;
        this.link.sendMessage(msg);
    }

    @JavascriptInterface
    public void filer(String cmd)
    {
        Message msg = new Message();
        msg.obj = cmd;
        msg.arg1 = 118;
        this.link.sendMessage(msg);
    }
}
