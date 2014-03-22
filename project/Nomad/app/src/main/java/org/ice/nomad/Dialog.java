package org.ice.nomad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Dialog
{
    private Activity ac;
    private Handler link;
    public String status;

    public Dialog(Activity ac, Handler link)
    {
        this.ac = ac;
        this.link = link;
    }

    public void superSuDialog()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(ac);

        alert.setTitle("Nomad Master Key");
        alert.setMessage("Type your \"sudo root\" password:");

        final EditText input = new EditText(ac);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(input);

        alert.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                status = input.getText().toString();
                Message msg = new Message();
                msg.obj = "rootpass";
                link.sendMessage(msg);
            }
        });

        alert.show();
    }

    public void confirmDialog(String title, String msg, String action)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(ac);

        alert.setTitle(title);
        alert.setMessage(msg);

        final String response = action;

        alert.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                status = "OK";
                Message msg = new Message();
                msg.obj = response;
                link.sendMessage(msg);
            }
        });

        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                status = "NO";
                Message msg = new Message();
                msg.obj = response;
                link.sendMessage(msg);
            }
        });

        alert.show();
    }
}
