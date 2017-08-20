package com.example.erasyl.coap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PDDTextActivity extends Activity implements View.OnClickListener {

    private Button btnBackPdd;
    private ListView lvPDDDetail;
    private PDDDataAdapter adapter;
    private List<PddDetail> mPddDetailList;

    private String URL = "http://java.coap.kz/pdd.php?art=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdd_text);

        Intent intent = getIntent();
        String titleID = intent.getStringExtra("titlePddID");
        URL = URL + titleID;
        Log.d("urlpdd", URL);
        HttpParse();


        btnBackPdd = (Button) findViewById(R.id.btnBackPdd);
        btnBackPdd.setOnClickListener(this);
    }

    private void HttpParse() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response != null && response.length() > 0) {
                                JSONArray jsonarray = new JSONArray(response);
                                ArrayList<PddDetail> pddBody = new ArrayList<PddDetail>();
                                for (int i = 0; i < jsonarray.length(); i++) {
                                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                                    int id = jsonobject.getInt("id");
                                    String title = jsonobject.getString("title");
                                    String content = "";
                                    content = jsonobject.getString("content");
                                    PddDetail coAPDetail = new PddDetail(id, title, content);
                                    Log.d("id", id + "");
                                    Log.d("title", title);
                                    Log.d("content", content);
                                    pddBody.add(coAPDetail);
                                }
                                toListView(pddBody);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PDDTextActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override


            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(PDDTextActivity.this);
        requestQueue.add(stringRequest);
    }

    private void toListView(ArrayList<PddDetail> pddDetail) {
        lvPDDDetail = (ListView) findViewById(R.id.lvPddDetail);
        mPddDetailList = pddDetail;
        adapter = new PDDDataAdapter(getApplicationContext(), mPddDetailList);
        lvPDDDetail.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackPdd:
                finish();
                break;
        }
    }
}
