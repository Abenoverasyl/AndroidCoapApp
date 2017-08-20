package com.example.erasyl.coap;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity  implements View.OnClickListener {

    private Button btnBack;
    private ListView lvNotifications;
    private NotificationAdapter adapter;
    private ArrayList<NotificationForUser> mNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        lvNotifications = (ListView) findViewById(R.id.lvNotifications);
        btnBack = (Button) findViewById(R.id.btnBack);

        Intent intent = getIntent();
        ArrayList<String> notifs = intent.getStringArrayListExtra("notif");
        mNotifications = new ArrayList<>();

        for (String notification : notifs) {
            String n[] = notification.split("[*]");
            Log.d("notific", n.length + "");
            String notific[] = new String[]{"", "", "", ""};
            for (int i = 0; i < n.length; ++i) {
                notific[i] = n[i];
            }
            mNotifications.add(new NotificationForUser(notific[0], notific[1], notific[2], notific[3]));
        }

        adapter = new NotificationAdapter(getApplicationContext(), mNotifications);
        lvNotifications.setAdapter(adapter);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack: {
                finish();
                break;
            }
        }
    }
}
