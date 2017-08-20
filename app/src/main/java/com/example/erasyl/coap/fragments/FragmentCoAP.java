package com.example.erasyl.coap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.erasyl.coap.CoapTextActivity;
import com.example.erasyl.coap.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentCoAP#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCoAP extends Fragment {

    private final String URL = "http://java.coap.kz/coap_chapters.php";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentCoAP() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCoAP.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCoAP newInstance(String param1, String param2) {

        FragmentCoAP fragment = new FragmentCoAP();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.coap_name);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_titles_coap, container, false);

        HttpParse(view, inflater, container);

        return view;
    }

    // Parser http
    private void HttpParse(final View view, final LayoutInflater inflater, final ViewGroup container) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response != null && response.length() > 0) {
                                TreeMap<Integer, String> titleMap = new TreeMap<>();
                                JSONArray jsonarray = new JSONArray(response);
                                for (int i = 0; i < jsonarray.length(); i++) {
                                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                                    int id = jsonobject.getInt("id");
                                    String title = jsonobject.getString("title");
                                    titleMap.put(id, title);
                                }
                                // set to fragment
                                getFragmentView(view, inflater, container, titleMap);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FragmentCoAP.this.getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override


            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(FragmentCoAP.this.getActivity());
        requestQueue.add(stringRequest);
    }

    // Fragment
    private void getFragmentView(View view, LayoutInflater inflater, ViewGroup container, TreeMap<Integer, String> titleMap) {

        final ArrayList<PDDRKTitle> titles = new ArrayList<PDDRKTitle>();
        for (int id: titleMap.keySet()) {
            titles.add(new PDDRKTitle(id, titleMap.get(id)));
        }
        BindDictionary<PDDRKTitle> dictionary = new BindDictionary<>();

        dictionary.addStringField(R.id.tvID, new StringExtractor<PDDRKTitle>() {
            @Override
            public String getStringValue(PDDRKTitle product, int position) {
                return "" + product.getId();
            }
        });

        dictionary.addStringField(R.id.tvTitle, new StringExtractor<PDDRKTitle>() {
            @Override
            public String getStringValue(PDDRKTitle product, int position) {
                return product.getId() + ". " + product.getTitle();
            }
        });

        FunDapter adapter = new FunDapter(
                FragmentCoAP.this.getActivity(),
                titles,
                R.layout.item_parttitle_layout, dictionary
        );

        ListView lvProduct = (ListView) view.findViewById(R.id.lvTitle);
        lvProduct.setAdapter(adapter);

        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PDDRKTitle selectedTitle = titles.get(position);

                Intent intent = new Intent(FragmentCoAP.this.getActivity(), CoapTextActivity.class);
                intent.putExtra("titleCoAPID", selectedTitle.getId() + "");
                intent.putExtra("title", selectedTitle.getTitle() + "");
                startActivity(intent);
            }
        });
    }
}
