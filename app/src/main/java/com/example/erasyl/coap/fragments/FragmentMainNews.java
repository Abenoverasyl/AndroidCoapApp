package com.example.erasyl.coap.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.example.erasyl.coap.AddCarActivity;
import com.example.erasyl.coap.Car;
import com.example.erasyl.coap.CustomCarsAdapter;
import com.example.erasyl.coap.CustomSwipeAdapter;
import com.example.erasyl.coap.News;
import com.example.erasyl.coap.R;
import com.example.erasyl.coap.SharedPrefer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentMainNews#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMainNews extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private Button btnCheckFine;

    private ArrayList<News> arrayNews;
    private ArrayList<Car> arrayCars;
    private ListView lvNews;
    private ListView lvCars;
    private Set<Integer> showMoreNewsSet = new HashSet<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FragmentMainNews() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMainNews.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMainNews newInstance(String param1, String param2) {
        FragmentMainNews fragment = new FragmentMainNews();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Главная");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_news, container, false);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.content_fragment);
        swipeRefreshLayout.setColorSchemeResources(R.color.appBarColor);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPrefer sharedPrefer = new SharedPrefer();
                        String id = sharedPrefer.getPref("userId", getActivity().getApplicationContext());
                        new ReadJSON().execute("http://java.coap.kz/jnews.php?u_id=" + id);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });

        SharedPrefer sharedPrefer = new SharedPrefer();
        String lastCarInd = "" + sharedPrefer.getPref("lastCarInd", getActivity().getApplicationContext());


        btnCheckFine = (Button) view.findViewById(R.id.btnCheckFine);
        btnCheckFine.setOnClickListener(this);

        if (!lastCarInd.equals("null")) {
            btnCheckFine.setVisibility(View.GONE);
        }

        arrayNews = new ArrayList<>();
        arrayCars = new ArrayList<>();
        lvNews = (ListView) view.findViewById(R.id.lvNews);
        lvCars = (ListView) view.findViewById(R.id.lvFines);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPrefer sharedPrefer = new SharedPrefer();
                String id = sharedPrefer.getPref("userId", getActivity().getApplicationContext());

                new ReadJSON().execute("http://java.coap.kz/jnews.php?u_id=" + id);
            }
        });

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCheckFine: {
                Intent intent = new Intent(getActivity(), AddCarActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRefresh() {

    }

    class ReadJSON extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return readURL(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {

            setCar();
            setNews(content);
        }
    }

    private void setNews(String content) {
        try {
            JSONArray jsonArray = new JSONArray(content);
            arrayNews.clear();
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject productObject = jsonArray.getJSONObject(i);
                arrayNews.add(new News(
                        productObject.getString("big_icon1"),
                        productObject.getString("big_icon2"),
                        productObject.getString("big_icon3"),
                        productObject.getString("big_icon4"),
                        productObject.getString("date"),
                        productObject.getString("title"),
                        productObject.getString("content")
                ));
                Log.d(i + "img1", productObject.getString("big_icon1"));
                Log.d("img2", productObject.getString("big_icon2"));
                Log.d("img3", productObject.getString("big_icon3"));
                Log.d("img4", productObject.getString("big_icon4"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CustomNewsAdapter adapter = new CustomNewsAdapter(
                getActivity().getApplicationContext(), R.layout.custom_news_layout, arrayNews
        );
        lvNews.setAdapter(adapter);
    }
    private void setCar() {

        SharedPrefer sharedPrefer = new SharedPrefer();
        String carsInds = "" + sharedPrefer.getPref("carsInds", getActivity().getApplicationContext());

        if (carsInds.length() > 0 && carsInds.charAt(0) == '$') {
            carsInds = carsInds.substring(1);
        }
        arrayCars.clear();
        if (!carsInds.equals("null")) {
            String carsArr[] = carsInds.split("[$]");
            for (String tmpCarInd : carsArr) {
                String tmpGn = "" + sharedPrefer.getPref("gn_" + tmpCarInd, getActivity().getApplicationContext());
                if (!tmpGn.equals("null")) {
                    String date = "" + sharedPrefer.getPref("FineCheckedDate_" + tmpCarInd, getActivity().getApplicationContext());
                    arrayCars.add(new Car(
                            tmpCarInd,
                            "http://stonebridge-autoelectrics.co.uk/communities/2/000/001/523/882//images/6851173_533x304.png",
                            date,
                            tmpGn,
                            "***"
                    ));
                }
            }
        }
        CustomCarsAdapter adapter = new CustomCarsAdapter(
                getActivity().getApplicationContext(), R.layout.custom_cars_layout, arrayCars
        );
        lvCars.setAdapter(adapter);
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

    public class CustomNewsAdapter extends ArrayAdapter<News> {

        ArrayList<News> newses;
        Context context;
        int resource;


        public CustomNewsAdapter(Context context, int resource, ArrayList<News> newses) {
            super(context, resource, newses);
            this.newses = newses;
            this.context = context;
            this.resource = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getContext()
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.custom_news_layout, null, true);
            }
            final News news = getItem(position);

            TextView tvDateNews = (TextView) convertView.findViewById(R.id.tvDateNews);
            TextView tvTitleNews = (TextView) convertView.findViewById(R.id.tvTitleNews);
            final TextView tvContentNews = (TextView) convertView.findViewById(R.id.tvContentNews);
            final Button btnViewMore = (Button) convertView.findViewById(R.id.btnViewMore);


            tvDateNews.setText(news.getDate());
            tvTitleNews.setText(news.getTitle());

            String content = news.getContent();


            if (content.length() > 250 && !showMoreNewsSet.contains(position)) {
                content = content.substring(0, 200) + "...";
                btnViewMore.setVisibility(View.VISIBLE);
            } else {
                btnViewMore.setVisibility(View.GONE);
            }

            tvContentNews.setText(content);

            btnViewMore.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    tvContentNews.setText(news.getContent());
                    SharedPrefer sharedPrefer = new SharedPrefer();
                    String id = sharedPrefer.getPref("userId", getActivity().getApplicationContext());
                    showMoreNewsSet.add(position);
                    new ReadJSON().execute("http://java.coap.kz/jnews.php?u_id=" + id);

                }
            });

            ArrayList<String> imgs = new ArrayList<>();
            if (!news.getImage1().equals("http://java.coap.kz/")) {
                imgs.add(news.getImage1());
            }
            if (!news.getImage2().equals("http://java.coap.kz/")) {
                imgs.add(news.getImage2());
            }
            if (!news.getImage3().equals("http://java.coap.kz/")) {
                imgs.add(news.getImage3());
            }
            if (!news.getImage4().equals("http://java.coap.kz/")) {
                imgs.add(news.getImage4());
            }
            String imageURLs[] = new String[imgs.size()];
            for (int i = 0; i < imgs.size(); ++i) {
                imageURLs[i] = imgs.get(i);
            }
            CustomSwipeAdapter customSwipeAdapter = new CustomSwipeAdapter(getContext(), imageURLs);
            ViewPager viewPager = (ViewPager) convertView.findViewById(R.id.view_pager);

            viewPager.setAdapter(customSwipeAdapter);
            return convertView;
        }
    }

}
