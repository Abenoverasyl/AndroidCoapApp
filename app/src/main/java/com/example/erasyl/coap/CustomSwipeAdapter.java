package com.example.erasyl.coap;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Erasyl on 18.04.2017.
 */

public class CustomSwipeAdapter extends PagerAdapter {

    private String imageURLs[];
    private Context context;
    private LayoutInflater layoutInflater;

    public CustomSwipeAdapter(Context context, String [] imageURLs) {
        this.context = context;
        this.imageURLs = imageURLs;
    }

    @Override
    public int getCount() {
        return imageURLs.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.swipe_layout, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
        Picasso.with(context).load(imageURLs[position]).into(imageView);

        TextView textView1 = (TextView) view.findViewById(R.id.text_view1);
        TextView textView2 = (TextView) view.findViewById(R.id.text_view2);
        TextView textView3 = (TextView) view.findViewById(R.id.text_view3);
        TextView textView4 = (TextView) view.findViewById(R.id.text_view4);


        textView1.setTextColor(view.getResources().getColor(R.color.colorHint));
        textView2.setTextColor(view.getResources().getColor(R.color.colorHint));
        textView3.setTextColor(view.getResources().getColor(R.color.colorHint));
        textView4.setTextColor(view.getResources().getColor(R.color.colorHint));

        if (imageURLs.length < 4) {
            textView4.setVisibility(View.GONE);
        } else if (imageURLs.length < 3) {
            textView3.setVisibility(View.GONE);
        } else if (imageURLs.length < 2) {
            textView2.setVisibility(View.GONE);
        }

        if (position == 0) {
            textView1.setTextColor(view.getResources().getColor(R.color.colorAccent));
        } else if (position == 1) {
            textView2.setTextColor(view.getResources().getColor(R.color.colorAccent));
        } else if (position == 2) {
            textView3.setTextColor(view.getResources().getColor(R.color.colorAccent));
        } else if (position == 3) {
            textView4.setTextColor(view.getResources().getColor(R.color.colorAccent));
        }
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}

