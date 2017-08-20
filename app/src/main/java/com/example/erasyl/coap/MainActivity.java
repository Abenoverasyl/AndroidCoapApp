package com.example.erasyl.coap;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.erasyl.coap.fragments.FragmentCoAP;
import com.example.erasyl.coap.fragments.FragmentMainNews;
import com.example.erasyl.coap.fragments.FragmentMyCars;
import com.example.erasyl.coap.fragments.FragmentPDD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Button buttonCloseMenu;
    private Button floating_button;
    private ArrayList<String> notifications;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        SharedPrefer sharedPrefer = new SharedPrefer();
        userId = sharedPrefer.getPref("userId", getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String showMyCars = "" + intent.getStringExtra("showMyCars");

        buttonCloseMenu = (Button) findViewById(R.id.close_menu);
        floating_button = (Button) findViewById(R.id.floating_button);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (showMyCars.equals("showMyCars")) {
            loadMyCars();
        } else {
            loadMain();
        }

        floating_button.setVisibility(View.GONE);

        int delay = 1000; // delay for 0 sec.
        int period = 1000; // repeat every 10 sec.
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                new ReadJSON().execute("http://java.coap.kz/push.php?u_id=" + userId);

            }
        }, delay, period);

        buttonCloseMenu.setOnClickListener(this);
        floating_button.setOnClickListener(this);

        notifications = new ArrayList<>();


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setTitle("О программе");
            builder1.setMessage("Coap v1.1\n" +
                    "- Приложение позволяет посмотреть штрафы по камерам видеофиксаций.\n" +
                    "- В дальнейшем функционал будет увеличиваться.\n\n" +
                    "       Спасибо за Ваш выбор!");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Закрыть",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } else if (id == R.id.action_share) {
            intentShare();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (id == R.id.nav_main) {

            loadMain();

        } else if (id == R.id.nav_mycars) {

            loadMyCars();

        } else if (id == R.id.nav_koap) {

            FragmentCoAP fragmentCoAP = new FragmentCoAP();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(
                    R.id.relativelayout_for_fragment,
                    fragmentCoAP,
                    fragmentCoAP.getTag()
            ).commit();

        } else if (id == R.id.nav_pddrk) {
            FragmentPDD fragmentPDD = new FragmentPDD();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(
                    R.id.relativelayout_for_fragment,
                    fragmentPDD,
                    fragmentPDD.getTag()
            ).commit();

        } else if (id == R.id.nav_invite) {
            intentShare();
        }
        fragmentTransaction.commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void intentShare() {
        try {
            Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    b, "Title", null);
            Uri imageUri =  Uri.parse(path);
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            share.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String sAux = "\nhttps://play.google.com/store/apps/details?id=Orion.Soft \n\n";
            share.putExtra(Intent.EXTRA_TEXT, sAux);
            share.putExtra(Intent.EXTRA_TEXT, "Добрый день!\n Рекомендуем вам " +
                    "использовать приложение ЦОАП(центр оповещения об " +
                    "административных правонарушениях).\n Мобильное " +
                    "приложение доступно для скачивая в \n\nApp Store " +
                    "(для пользователей IOS)" + sAux + " и Play Market " +
                    "(для пользователей Android)" + sAux);
            startActivity(Intent.createChooser(share, "Поделиться с помощью"));
        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_menu: {
                DrawerLayout mDrawerLayout;
                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawers();
                break;
            }
            case R.id.floating_button:{

                Intent intent = new Intent(this, NotificationsActivity.class);
                intent.putStringArrayListExtra("notif", notifications);
                startActivity(intent);

                notifications.clear();
                floating_button.setVisibility(View.GONE);
                break;
            }
        }
    }

    private void loadMain() {
        FragmentMainNews fragmentMainNews = new FragmentMainNews();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(
                R.id.relativelayout_for_fragment,
                fragmentMainNews,
                fragmentMainNews.getTag()
        ).commit();
    }

    private void loadMyCars() {
        FragmentMyCars myCarsFragment = new FragmentMyCars();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(
                R.id.relativelayout_for_fragment,
                myCarsFragment,
                myCarsFragment.getTag()
        ).commit();
    }


    class ReadJSON extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return readURL(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {

            JSONObject dataJsonObj;
            if (content.length() > 0 && !content.contains("\"Err\"")) {
                try {
                    Log.d("notificationFinished", content);
                    dataJsonObj = new JSONObject(content);
                    JSONArray messages = dataJsonObj.getJSONArray("messages");

                    JSONObject firstMess = messages.getJSONObject(0);
                    String idN = firstMess.getString("id");
                    String titleN = firstMess.getString("title");
                    String messageN = firstMess.getString("message");
                    String urlN = firstMess.getString("url");
                    notifications.add(idN + "*" + titleN + "*" + messageN + "*" + urlN);
                    floating_button.setText("" + notifications.size());
                    floating_button.setVisibility(View.VISIBLE);
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

    @Override
    protected void onDestroy() {
        getApplicationContext().startService(new Intent(getApplicationContext(), MyService.class));
        super.onDestroy();
    }
}
