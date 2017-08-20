package com.example.erasyl.coap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class RegistrationActivity extends AppCompatActivity  implements View.OnClickListener {

    private Button btnBack;
    private Button btnRegister;
    private EditText editTextPhoneNum;
    private EditText editTextEmail;
    private EditText editTextPass;
    private EditText editTextPassAgain;
    private String phoneNum;
    private String email;
    private String pass;
    private String passAgain;
    private String keyPhoneNum = "phone";
    private String keyEmail = "email";
    private String keyPass = "psw";
    private String keyCity = "city";
    private String city;
    Spinner spinner;

    private static String URL = "http://java.coap.kz/kabinet/reg.php";

    Dialogs dialogs = new Dialogs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        editTextPhoneNum = (EditText) findViewById(R.id.editTextPhoneNum);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPass = (EditText) findViewById(R.id.editTextPass);
        editTextPassAgain = (EditText) findViewById(R.id.editTextPassAgain);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegistrationActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.cities));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(arrayAdapter);

        btnBack.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack: {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.btnRegister: {
                phoneNum = editTextPhoneNum.getText().toString();
                email = editTextEmail.getText().toString();
                pass = editTextPass.getText().toString();
                passAgain = editTextPassAgain.getText().toString();
                city = "" + spinner.getSelectedItemPosition();
                if (city.equals("0")) {
                    dialogs.showMessage(this, "Неправильные данные", "Выберите город!");
                } else if (phoneNum.length() == 0) {
                    dialogs.showMessage(this, "Неправильные данные", "Введите Номер телефона!");
                } else if (email.length() == 0) {
                    dialogs.showMessage(this, "Неправильные данные", "Введите E-mail!");
                } else if (pass.length() == 0) {
                    dialogs.showMessage(this, "Неправильные данные", "Введите пароль!");
                } else if (passAgain.length() == 0) {
                    dialogs.showMessage(this, "Неправильные данные", "Повторите пароль");
                } else if (!pass.equals(passAgain)) {
                    dialogs.showMessage(this, "Неправильные данные", "Пароли не совпадают");
                } else {
                    Toast.makeText(this, "Загрузка..", Toast.LENGTH_SHORT).show();
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
                        try {
                            Log.d("resp reg", response);
                            JSONObject dataJsonObj = new JSONObject(response);
                            String message = dataJsonObj.getString("message");
                            int success = dataJsonObj.getInt("success");
                            String code = dataJsonObj.getString("code");

                            if (message.contains("NoConn")) {

                                Toast.makeText(RegistrationActivity.this, "Нет соединения с интернетом",Toast.LENGTH_LONG ).show();

                            } else if (success == 1){

                                Intent intent = new Intent(RegistrationActivity.this, ConfirmRegisterActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("code", code);
                                intent.putExtra("phone", phoneNum);
                                intent.putExtra("psw", pass);
                                intent.putExtra("city", city);
                                startActivity(intent);
                                finish();
                            } else {

                                dialogs.showMessage(RegistrationActivity.this, message, "");

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistrationActivity.this,error.toString(),Toast.LENGTH_LONG ).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put(keyPhoneNum, phoneNum);
                map.put(keyEmail, email);
                map.put(keyPass, pass);
                map.put(keyCity, city);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }





}
