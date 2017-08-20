package com.example.erasyl.coap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ForgotPassword extends Activity implements View.OnClickListener{

    private Button btnSend;
    private Button btnBack;

    private EditText etPhone;
    private EditText etEmail;

    private String phone;
    private String email;

    private Dialogs dialogs = new Dialogs();

    private String URL = "http://java.coap.kz/kabinet/remind.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Забыли пароль");
        setContentView(R.layout.activity_forgot_password);

        etPhone = (EditText) findViewById(R.id.tvPhoneForg);
        etEmail = (EditText) findViewById(R.id.etEmailForg);

        btnBack = (Button) findViewById(R.id.btnBackForgotPass);
        btnSend = (Button) findViewById(R.id.btnSendForgPass);

        btnBack.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackForgotPass: {
                finish();
                break;
            }
            case R.id.btnSendForgPass: {
                phone = etPhone.getText().toString();
                email = etEmail.getText().toString();

                if (phone.length() > 0 && email.length() > 0) {
                    httpParse();
                }
                break;
            }
        }
    }

    private void httpParse() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("confirm pass", response);
                        try {
                            JSONObject dataJsonObj = new JSONObject(response);

                            int success = dataJsonObj.getInt("success");
                            String message = dataJsonObj.getString("message");

                            if (message.contains("NoConn")) {

                                Toast.makeText(ForgotPassword.this, "Нет соединения с интернетом",Toast.LENGTH_LONG ).show();

                            } else if (success == 1){

                                Toast.makeText(ForgotPassword.this, "На ваш номер отправлен СМС",Toast.LENGTH_LONG ).show();
                                Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                dialogs.showMessage(ForgotPassword.this, message, "");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ForgotPassword.this,error.toString(),Toast.LENGTH_LONG ).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("phone", phone);
                map.put("email", email);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
