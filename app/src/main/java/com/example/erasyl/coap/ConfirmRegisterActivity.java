package com.example.erasyl.coap;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConfirmRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private String phone;
    private String email;
    private String password;
    private String code;
    private Button btnSend;
    private EditText tvConfCode;
    private String URL ="http://java.coap.kz/kabinet/act_reg.php";
    private Dialogs dialogs = new Dialogs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_register);
        setTitle("Завершение регистраций");
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");
        code = intent.getStringExtra("code");
        password = intent.getStringExtra("psw");

        tvConfCode = (EditText) findViewById(R.id.tvConfCode);
        btnSend = (Button) findViewById(R.id.btnSend);

        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend: {
                if (code.equals(tvConfCode.getText().toString().trim())) {
                    Toast.makeText(this, "Загрузка..", Toast.LENGTH_SHORT);
                    httpParse();
                } else {
                    dialogs.alertDialog(this, "Непрвильный код, повторите пожалуйста");
                }
            }
        }
    }

    private void httpParse() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject dataJsonObj = new JSONObject(response);

                            int success = dataJsonObj.getInt("success");
                            String message = dataJsonObj.getString("message");
                            String userId = dataJsonObj.getString("u_id");

                            if (message.contains("NoConn")) {

                                Toast.makeText(ConfirmRegisterActivity.this, "Нет соединения с интернетом",Toast.LENGTH_LONG ).show();

                            } else if (success == 1){

                                SharedPrefer sharedPrefer = new SharedPrefer();
                                sharedPrefer.putPref(getApplicationContext(), "userId", "" + userId);
                                sharedPrefer.putPref(getApplicationContext(), "userEmail", email);
                                sharedPrefer.putPref(getApplicationContext(), "userPhone", phone);
                                sharedPrefer.putPref(getApplicationContext(), "userPassword", password);

                                Intent intent = new Intent(ConfirmRegisterActivity.this, MainActivity.class);
                                startActivity(intent);

                                finish();

                            } else {
                                dialogs.showMessage(ConfirmRegisterActivity.this, message, "");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ConfirmRegisterActivity.this,error.toString(),Toast.LENGTH_LONG ).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("email", email);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
