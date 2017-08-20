package com.example.erasyl.coap;

/**
 * Created by Erasyl on 21.04.2017.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    NotificationManager nm;
    MyTimerTask mMyTimerTask;
    Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 5000L);

        return Service.START_STICKY;
    }

    void sendNotif(String messId, String title, String message, String url) {

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setTicker("ЦОАП")
                .setWhen(System.currentTimeMillis());

        Intent intent = new Intent(this, LoadSiteActivity.class)
                .putExtra("siteURL", url);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notificationBuilder.setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pIntent);
        //ставим флаг, чтобы уведомление пропало после нажатия
        notificationBuilder.setAutoCancel(true)
                .getNotification();
        Notification notification = notificationBuilder.getNotification();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        nm.notify(0, notification);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    class ReadJSON extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return readURL(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {

            JSONObject dataJsonObj;
            Log.d("MyContent", content);
            if (!content.contains("\"Err\":")) {
                try {
                    dataJsonObj = new JSONObject(content);
                    JSONArray messages = dataJsonObj.getJSONArray("messages");

                    JSONObject firstMess = messages.getJSONObject(0);
                    String id = firstMess.getString("id");
                    String title = firstMess.getString("title");
                    String message = firstMess.getString("message");
                    String url = firstMess.getString("url");
                    sendNotif(id, title, message, url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String readURL(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            SharedPrefer sharedPrefer = new SharedPrefer();
            String userId = sharedPrefer.getPref("userId", getApplicationContext());
            new ReadJSON().execute("http://java.coap.kz/push.php?u_id=" + userId);
        }
    }
}