package com.example.erasyl.coap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by Erasyl on 13.03.2017.
 */

public class Dialogs {
     void alertDialog(Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(message)
                .setCancelable(false)
                .setPositiveButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

     void loadingDialog(Activity activity) {
         final ProgressDialog pd = ProgressDialog.show(activity, "", "Загрузка..", false, true);
         new Thread() {
             public void run() {
                 try {
                     sleep(2000);
                     Log.v("TAG","in try block");
                 } catch (Exception e) {
                     Log.e("tag", e.getMessage());
                 }
                 pd.dismiss();
             }
         }.start();
    }

    public static void showMessage(final Context c, final String title, final String mess) {
        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(c);
        aBuilder.setTitle(title);
        // aBuilder.setIcon(R.drawable.icon);
        aBuilder.setMessage(mess);

        aBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }

        });

        aBuilder.show();
    }
}
