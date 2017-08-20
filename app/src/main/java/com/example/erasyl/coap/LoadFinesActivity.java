package com.example.erasyl.coap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.erasyl.coap.fragments.FragmentMyCars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class LoadFinesActivity extends Activity implements View.OnClickListener{

    private String url = "http://java.coap.kz/coap.php?";
    private Button btnBackFines;
    private Button btnShare;
    private TextView tvFine;

    private String name;
    private String email;
    private String phone;
    private String gn;
    private String pn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_fines);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");
        gn = intent.getStringExtra("gn");
        pn = intent.getStringExtra("pn");
        String save = "" + intent.getStringExtra("save");
        String id = "" + intent.getStringExtra("id");

        url = url + "name="+name+"&phone="+phone+"&gn="+gn+"&pn="+pn+"&email="+email;

        btnBackFines = (Button) findViewById(R.id.btnBackFines);
        tvFine = (TextView) findViewById(R.id.tvFine);
        btnShare = (Button) findViewById(R.id.btnShare);
        btnBackFines.setOnClickListener(this);
        btnShare.setOnClickListener(this);

        SharedPrefer sharedPrefer = new SharedPrefer();
        String lastCarInd = "" + sharedPrefer.getPref("lastCarInd", getApplicationContext());
        int tmpCarInd = 1;
        if (!lastCarInd.equals("null")) {
            tmpCarInd = Integer.parseInt(lastCarInd) + 1;
        }

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd 'в' hh:mm");

        if (save.equals("save")) {

            sharedPrefer.putPref(getApplicationContext(), "lastCarInd", "" + tmpCarInd);

            sharedPrefer.putPref(getApplicationContext(), "name_" + tmpCarInd, name);
            sharedPrefer.putPref(getApplicationContext(), "email_" + tmpCarInd, email);
            sharedPrefer.putPref(getApplicationContext(), "phone_" + tmpCarInd, phone);
            sharedPrefer.putPref(getApplicationContext(), "gn_" + tmpCarInd, gn);
            sharedPrefer.putPref(getApplicationContext(), "pn_" + tmpCarInd, pn);


            sharedPrefer.putPref(getApplicationContext(), "FineCheckedDate_" + tmpCarInd, ft.format(dNow));

            String carsInds = "" + sharedPrefer.getPref("carsInds", getApplicationContext());

            if (carsInds.equals("null")) {
                carsInds = "$1";
            } else {
                carsInds = carsInds + "$" + tmpCarInd;
            }

            sharedPrefer.putPref(getApplicationContext(), "carsInds", carsInds);
        } else {
            sharedPrefer.putPref(getApplicationContext(), "FineCheckedDate_" + id, ft.format(dNow));
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("URL", url);
                new ReadJSON().execute(url);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackFines: {
                finish();
                break;
            }
            case R.id.btnShare: {
                intentShare();
                break;
            }

        }
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

    // http parser
    class ReadJSON extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return readURL(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {
            try {
                Log.d("Fine content", content);
                tvFine.setText(Html.fromHtml(content));
                tvFine.setTypeface(null, Typeface.NORMAL);
                if (content.contains("Поздравляем")) {
                    tvFine.setText("У вас нет нарушений.Поздравляем!");
                    tvFine.setTypeface(null, Typeface.BOLD);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            Log.d("load finse:", content.toString());
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}