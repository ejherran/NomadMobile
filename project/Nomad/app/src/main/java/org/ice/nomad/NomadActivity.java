package org.ice.nomad;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.os.Message;
import android.widget.ProgressBar;
import android.os.Handler;
import android.webkit.WebSettings;
import android.widget.Toast;

import org.ice.net.Line;
import org.ice.web.NomadInterface;

@SuppressWarnings("ALL")
public class NomadActivity extends Activity
{
    private int exLevel;
    private int selMenu = 0;
    private boolean tunelOk = false;

    private String host;
    private String user;
    private String pass;
    private String root;
    private int port;

    private Transport tunel;

    private ProgressBar spiner;
    private Button button;
    private EditText txtHost, txtPort, txtUser, txtPass;
    private WebView web;

    private Dialog di;

    private Handler bridge = new Handler()
    {
        public void handleMessage(Message msg)
        {

            this.removeCallbacksAndMessages(null);

            if(exLevel == -1)
                render(msg.obj.toString());
            else if(exLevel == 0)
                recConect(msg.obj.toString());
            else if(exLevel == 1)
                isReady(msg.obj.toString());
            else if(exLevel == 2)
                sysInfo();
        }
    };

    private Handler jsLink = new Handler()
    {
        public void handleMessage(Message msg)
        {
            this.removeCallbacksAndMessages(null);

            if(msg.arg1 == 101)
                sysInfo();
            else if(msg.arg1 == 102)
                cmLaunch(msg.obj.toString());
            else if(msg.arg1 == 103)
                servStatus(msg.obj.toString());
            else if(msg.arg1 == 104)
                di.confirmDialog("Nomad Confirm", "Are you sure want to restart this server?", "rtserver");
            else if(msg.arg1 == 105)
                di.confirmDialog("Nomad Confirm", "Are you sure want to shutdown this server?", "sdserver");
            else if(msg.arg1 == 106)
                firewall(msg.obj.toString());
            else if(msg.arg1 == 107)
                logInsp(msg.obj.toString());
            else if(msg.arg1 == 108)
                routes(msg.obj.toString());
            else if(msg.arg1 == 109)
                sshCfg(msg.obj.toString());
            else if(msg.arg1 == 110)
                mysqlCfg(msg.obj.toString());
            else if(msg.arg1 == 111)
                ftpCfg(msg.obj.toString());
            else if(msg.arg1 == 112)
                webCfg(msg.obj.toString());
            else if(msg.arg1 == 113)
                mtaCfg(msg.obj.toString());
            else if(msg.arg1 == 114)
                sambaCfg(msg.obj.toString());
            else if(msg.arg1 == 115)
                squidCfg(msg.obj.toString());
            else if(msg.arg1 == 116)
                dnsCfg(msg.obj.toString());
            else if(msg.arg1 == 117)
                dhcpCfg(msg.obj.toString());
            else if(msg.arg1 == 118)
                filer(msg.obj.toString());
        }
    };

    private Handler diLink = new Handler()
    {
        public void handleMessage(Message msg)
        {
            this.removeCallbacksAndMessages(null);

            if(msg.obj.toString().compareToIgnoreCase("rootpass") == 0)
                proConnect();
            else if (msg.obj.toString().compareToIgnoreCase("rtserver") == 0)
            {
                if(di.status.compareToIgnoreCase("OK") == 0)
                    rtServer();
            }
            else if (msg.obj.toString().compareToIgnoreCase("sdserver") == 0)
            {
                if(di.status.compareToIgnoreCase("OK") == 0)
                    sdServer();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nomad);

        this.di = new Dialog(this, this.diLink);

        this.spiner = (ProgressBar) findViewById(R.id.spiner);

        txtHost = (EditText) findViewById(R.id.txtHost);
        txtPort = (EditText) findViewById(R.id.txtPort);
        txtUser = (EditText) findViewById(R.id.txtUser);
        txtPass = (EditText) findViewById(R.id.txtPass);


        button = (Button) findViewById(R.id.btnConnect);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                tryConect();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(this.selMenu == 0)
            getMenuInflater().inflate(R.menu.nomad, menu);
        else if(this.selMenu == 1)
            getMenuInflater().inflate(R.menu.manu_board, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_exit)
        {
            finish();
            return true;
        }
        else if (id == R.id.action_sysinfo)
        {
            sysInfo();
            return true;
        }
        else if (id == R.id.action_cmlaunch)
        {
            cmLaunch("");
            return true;
        }
        else if (id == R.id.action_servstatus)
        {
            servStatus("");
            return true;
        }
        else if (id == R.id.action_firewall)
        {
            firewall("");
            return true;
        }
        else if (id == R.id.action_loginspector)
        {
            logInsp("");
            return true;
        }
        else if (id == R.id.action_filecreator)
        {
            filer("");
            return true;
        }
        else if (id == R.id.action_routes)
        {
            routes("");
            return true;
        }
        else if (id == R.id.srv_ssh)
        {
            sshCfg("");
            return true;
        }
        else if (id == R.id.srv_mysql)
        {
            mysqlCfg("");
            return true;
        }
        else if (id == R.id.srv_ftp)
        {
            ftpCfg("");
            return true;
        }
        else if (id == R.id.srv_apache)
        {
            webCfg("");
            return true;
        }
        else if (id == R.id.srv_postfix)
        {
            mtaCfg("");
            return true;
        }
        else if (id == R.id.srv_samba)
        {
            sambaCfg("");
            return true;
        }
        else if (id == R.id.srv_squid)
        {
            squidCfg("");
            return true;
        }
        else if (id == R.id.srv_dns)
        {
            dnsCfg("");
            return true;
        }else if (id == R.id.srv_dhcp)
        {
            dhcpCfg("");
            return true;
        }
        else if (id == R.id.action_refresh)
        {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onDestroy()
    {
        if(this.tunelOk)
            this.tunel.finishIt();

        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    private void tryConect()
    {
        this.exLevel = 0;
        button.setClickable(false);
        spiner.setVisibility(ProgressBar.VISIBLE);

        this.host = this.txtHost.getText().toString();
        this.port = Integer.valueOf(this.txtPort.getText().toString()).intValue();
        this.user = this.txtUser.getText().toString();
        this.pass = this.txtPass.getText().toString();

        Line line = new Line(this.host, this.port, this.user, this.pass);
        this.tunel = new Transport(line, bridge);
        this.tunel.start();
        this.tunel.send("uname -a", "");

        this.tunelOk = true;
    }

    private void recConect(String msg)
    {
        boolean flag = false;

        if(msg.compareToIgnoreCase("$") == 0 || msg.compareToIgnoreCase("") == 0)
            msg = "IMPOSIBLE ESTABLECER CONEXIÓN";
        else
            flag = true;

        button.setClickable(true);
        spiner.setVisibility(ProgressBar.INVISIBLE);

        if(flag)
            di.superSuDialog();
        else
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private  void proConnect()
    {
        this.root = di.status;
        this.exLevel = 1;

        this.selMenu = 1;
        setContentView(R.layout.board);
        invalidateOptionsMenu();
        web = (WebView) findViewById(R.id.boardWebview);
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web.addJavascriptInterface(new NomadInterface(this, this.jsLink), "Nomad");

        render("<br /><center><b>Initial Configuration...</b></center><br />");

        this.tunel.send("if [ -f /usr/local/nomad/nomad.py ]; then echo \"SI\"; else echo \"NO\"; fi;", "");
    }

    private void isReady(String msg)
    {
        if(msg.trim().compareToIgnoreCase("NO") == 0)
        {
            String ord = "sudo -S -p '' mkdir -p /usr/local/nomad;";
            ord = ord + "sudo -S -p '' apt-get -y install unzip  >> /tmp/nomadlog;";
            ord = ord + "sudo -S -p '' wget -o /tmp/nomadget https://github.com/ejherran/NomadSandbox/archive/master.zip -O /usr/local/nomad/master.zip;";
            ord = ord + "sudo -S -p '' unzip /usr/local/nomad/master.zip -d /usr/local/nomad >> /tmp/nomadlog;";
            ord = ord + "sudo -S -p '' mv /usr/local/nomad/NomadSandbox-master/nomad/* /usr/local/nomad/;";
            ord = ord + "sudo -S -p '' rm -fr /usr/local/nomad/NomadSandbox-master/;";
            ord = ord + "sudo -S -p '' rm -fr /usr/local/nomad/master.zip;";
            ord = ord + "sudo -S -p '' chmod +x /usr/local/nomad/nomad.py;";
            ord = ord + "sudo -S -p '' python /usr/local/nomad/nomad.py isReady;";

            this.exLevel = 2;
            this.tunel.send(ord, this.root);
        }
        else
            sysInfo();
    }

    private void sysInfo()
    {
        this.exLevel = -1;
        render("<br /><center><b>Loading...</b></center><br />");
        this.tunel.send("sudo -S -p '' python /usr/local/nomad/nomad.py sysInfo;", this.root);
    }

    private void cmLaunch(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        this.tunel.send("sudo -S -p '' python /usr/local/nomad/nomad.py cmLaunch "+cmd, this.root);
    }

    private void servStatus(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        this.tunel.send("sudo -S -p '' python /usr/local/nomad/nomad.py servStatus "+cmd, this.root);
    }

    private void firewall(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        this.tunel.send("sudo -S -p '' python /usr/local/nomad/nomad.py firewall "+cmd, this.root);
    }

    private void logInsp(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        this.tunel.send("sudo -S -p '' python /usr/local/nomad/nomad.py logInsp "+cmd, this.root);
    }

    private void filer(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        this.tunel.send("sudo -S -p '' python /usr/local/nomad/nomad.py filer "+cmd, this.root);
    }

    private void routes(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        String xcmd = "sudo -S -p '' python /usr/local/nomad/nomad.py routes "+cmd;
        this.tunel.send(xcmd, this.root);
    }

    private void sshCfg(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        String xcmd = "sudo -S -p '' python /usr/local/nomad/nomad.py sshCfg "+cmd;
        this.tunel.send(xcmd, this.root);
    }

    private void mysqlCfg(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        String xcmd = "sudo -S -p '' python /usr/local/nomad/nomad.py mysqlCfg "+cmd;
        this.tunel.send(xcmd, this.root);
    }

    private void ftpCfg(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        String xcmd = "sudo -S -p '' python /usr/local/nomad/nomad.py ftpCfg "+cmd;
        this.tunel.send(xcmd, this.root);
    }

    private void webCfg(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        String xcmd = "sudo -S -p '' python /usr/local/nomad/nomad.py webCfg "+cmd;
        this.tunel.send(xcmd, this.root);
    }

    private void mtaCfg(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        String xcmd = "sudo -S -p '' python /usr/local/nomad/nomad.py mtaCfg "+cmd;
        this.tunel.send(xcmd, this.root);
    }

    private void sambaCfg(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        String xcmd = "sudo -S -p '' python /usr/local/nomad/nomad.py sambaCfg "+cmd;
        this.tunel.send(xcmd, this.root);
    }

   private void squidCfg(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        String xcmd = "sudo -S -p '' python /usr/local/nomad/nomad.py squidCfg "+cmd;
        this.tunel.send(xcmd, this.root);
    }

   private void dnsCfg(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        String xcmd = "sudo -S -p '' python /usr/local/nomad/nomad.py dnsCfg "+cmd;
        this.tunel.send(xcmd, this.root);
    }

   private void dhcpCfg(String cmd)
    {
        String inter = "Loading...";

        if(cmd.compareToIgnoreCase("") != 0)
        {
            inter = "Running Command...";
            cmd = Base64.encodeToString(cmd.getBytes(), Base64.DEFAULT);
            cmd = cmd.replaceAll("\n", "");
        }

        this.exLevel = -1;
        render("<br /><center><b>"+inter+"</b></center><br />");
        String xcmd = "sudo -S -p '' python /usr/local/nomad/nomad.py dhcpCfg "+cmd;
        this.tunel.send(xcmd, this.root);
    }

    private void rtServer()
    {
        this.exLevel = -2;
        render("<br /><center><b>Restart Server...</b></center><br />");
        this.tunel.send("sudo -S -p '' init 6", this.root);
    }

    private void sdServer()
    {
        this.exLevel = -2;
        render("<br /><center><b>Shutdown Server...</b></center><br />");
        this.tunel.send("sudo -S -p '' init 0", this.root);
    }

    private void render(String msg)
    {
        web.loadData(msg, "text/html", null);
    }

    private void refresh()
    {
        this.tunel.finishIt();
        this.tunel = null;
        this.bridge.removeCallbacksAndMessages(null);

        Line line = new Line(this.host, this.port, this.user, this.pass);
        this.tunel = new Transport(line, bridge);
        this.tunel.start();
    }
}
