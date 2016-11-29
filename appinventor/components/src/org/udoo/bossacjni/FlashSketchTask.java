package org.udoo.bossacjni;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

public class FlashSketchTask extends AsyncTask<String, Void, Integer> {

    private ProgressDialog pd;
    private Activity context;

    public FlashSketchTask(Activity context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(context);
        pd.setTitle("Arduino");
        pd.setMessage("Flashing sketch. Please wait...");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected Integer doInBackground(String... params) {
        BossacManager dd = new BossacManager();
        return dd.BossacWriteImage(params[0], true);
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (pd.isShowing()) pd.dismiss();

        String message;
        if (result == 0) {
            message = "Sketch flashed successfully!";
        } else {
            message = "Error during flashing sketch!";
        }

        new AlertDialog.Builder(context)
            .setTitle("ADK Manager")
            .setMessage(message)
            .setNegativeButton("cancel", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface d, int arg1) {
                    d.cancel();
                };
            })
            .show();
    }

}

