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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoapTextActivity extends Activity implements View.OnClickListener {

    private ListView lvCoAPDetail;
    private CoAPDataAdapter adapter;
    private List<CoAPDetail> mCoAPDetailList;
    private Button btnBackCoAP;
    
    private String URL = "http://java.coap.kz/coap_chapters.php?art=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coap_text);

        Intent intent = getIntent();
        String titleID = intent.getStringExtra("titleCoAPID");
        String title= intent.getStringExtra("title");
        TextView tvTilte = (TextView) findViewById(R.id.tvTitle);
        tvTilte.setText(title);
        URL = URL + titleID;
        HttpParse();

        btnBackCoAP = (Button) findViewById(R.id.btnBackCoAP);
        btnBackCoAP.setOnClickListener(this);
    }

    private void HttpParse() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response != null && response.length() > 0) {
                                JSONArray jsonarray = new JSONArray(response);
                                ArrayList<CoAPDetail> coapBody = new ArrayList<CoAPDetail>();
                                for (int i = 0; i < jsonarray.length(); i++) {
                                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                                    int id = jsonobject.getInt("id");
                                    String art = jsonobject.getString("art");
                                    String narush = jsonobject.getString("narush");
                                    String fine1 = jsonobject.getString("fine1");
                                    String fine2 = jsonobject.getString("fine2");
                                    CoAPDetail coAPDetail = new CoAPDetail(id, art, narush, fine1, fine2);
                                    coapBody.add(coAPDetail);
                                }
                                toListView(coapBody);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CoapTextActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override


            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CoapTextActivity.this);
        requestQueue.add(stringRequest);
    }

    private void toListView(ArrayList<CoAPDetail> coAPDetail) {
        lvCoAPDetail = (ListView) findViewById(R.id.lvCoapDetail);
        mCoAPDetailList = coAPDetail;
        adapter = new CoAPDataAdapter(getApplicationContext(), mCoAPDetailList);
        lvCoAPDetail.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackCoAP:
                finish();
                break;
        }
    }
}
