package com.example.erasyl.coap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText loginEmail;
    private EditText loginPass;

    private Button btnForgotPass;
    private Button btnLogin;
    private Button btnToRegister;
    private String URL = "http://java.coap.kz/kabinet/auth.php";
    private String keyEmail = "email";
    private String keyPassword = "psw";
    private String userEmail, userPassword;

    Dialogs dialogs = new Dialogs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPrefer sharedPrefer = new SharedPrefer();
        String id = "";
        id = "" + sharedPrefer.getPref("userId", getApplicationContext());
        if (id.length() > 0 && id.charAt(0) != 'n') {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPass = (EditText) findViewById(R.id.loginPass);

        btnForgotPass = (Button) findViewById(R.id.btnForgotPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnToRegister = (Button) findViewById(R.id.btnToRegister);

        btnForgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnToRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Dialogs dialogs = new Dialogs();
        switch (v.getId()) {
            case R.id.btnForgotPass: {
                Intent intent = new Intent(this, ForgotPassword.class);
                startActivity(intent);
                break;
            }
            case R.id.btnLogin: {
                Animation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(100);
                btnLogin.setAnimation(animation);

                userEmail = loginEmail.getText().toString();
                userPassword = loginPass.getText().toString();
                if (userEmail.length() == 0) {
                    dialogs.showMessage(this, "Неправильные данные", "Введите E-mail!");
                } else if (userPassword.length() == 0) {
                    dialogs.showMessage(this, "Неправильные данные", "Введите пароль!");
                } else {
                    Toast.makeText(this, "Загрузка..", Toast.LENGTH_SHORT).show();
                    httpParse();
                }
                break;
            }
            case R.id.btnToRegister: {
                Animation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(100);
                btnToRegister.setAnimation(animation);

                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    private void httpParse() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("resp", response);
                        try {
                            JSONObject dataJsonObj = new JSONObject(response);

                            int success = dataJsonObj.getInt("success");
                            final String message = dataJsonObj.getString("message");
                            final String userId = dataJsonObj.getString("u_id");

                            if (message.contains("NoConn")) {

                                Toast.makeText(LoginActivity.this, "Нет соединения с интернетом",Toast.LENGTH_LONG ).show();

                            } else if (success == 1){

                                String userPhone = dataJsonObj.getString("phone");

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                SharedPrefer sharedPrefer = new SharedPrefer();
                                sharedPrefer.putPref(getApplicationContext(), "userId", userId);
                                sharedPrefer.putPref(getApplicationContext(), "userEmail", userEmail);
                                sharedPrefer.putPref(getApplicationContext(), "userPhone", userPhone);
                                sharedPrefer.putPref(getApplicationContext(), "userPassword", userPassword);

                                startActivity(intent);
                                finish();

                            }  else {
                                if (success == 2) {
                                    final String code = dataJsonObj.getString("code");
                                    final AlertDialog.Builder aBuilder = new AlertDialog.Builder(LoginActivity.this);
                                    aBuilder.setTitle(message);
                                    aBuilder.setMessage("Активируйте аккаунт");
                                    // aBuilder.setIcon(R.drawable.icon);
                                    aBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(final DialogInterface dialog, final int which) {
                                            dialog.dismiss();

                                            Intent intent = new Intent(LoginActivity.this, ConfirmRegisterActivity.class);
                                            intent.putExtra("email", userEmail);
                                            intent.putExtra("code", code);
                                            intent.putExtra("u_id", userId);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                                    aBuilder.show();
                                } else {
                                    dialogs.showMessage(LoginActivity.this, message, "");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG ).show();
                    }
                }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> map = new HashMap<String,String>();
                            map.put(keyEmail, userEmail);
                            map.put(keyPassword, userPassword);
                            return map;
                        }
                    };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public static void showMessage(final Context c, final String title, final String s) {
        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(c);
        aBuilder.setTitle(title);
        // aBuilder.setIcon(R.drawable.icon);
        aBuilder.setMessage(s);

        aBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }

        });

        aBuilder.show();
    }
}
