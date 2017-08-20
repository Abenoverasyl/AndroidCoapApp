package com.example.erasyl.coap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Erasyl on 20.03.2017.
 */

public class CoAPDataAdapter extends BaseAdapter {

    private Context mContext;
    private List<CoAPDetail> mCoAPDetailList;

    public CoAPDataAdapter(Context mContext, List<CoAPDetail> mCoAPDetailList) {
        this.mContext = mContext;
        this.mCoAPDetailList = mCoAPDetailList;
    }

    @Override
    public int getCount() {
        return mCoAPDetailList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCoAPDetailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.item_coaptext_list, null);
        TextView tvCoapTextArt = (TextView)view.findViewById(R.id.tvCoapTextArt);
        TextView tvCoapTextNarush = (TextView)view.findViewById(R.id.tvCoapTextNarush);
        TextView tvCoapTextFine1 = (TextView)view.findViewById(R.id.tvCoapTextFine1);
        TextView tvCoapTextFine2 = (TextView)view.findViewById(R.id.tvCoapTextFine2);
        // Set text for TextView
        tvCoapTextArt.setText(mCoAPDetailList.get(position).getArt());
        tvCoapTextNarush.setText(mCoAPDetailList.get(position).getNarush());
        tvCoapTextFine1.setText(mCoAPDetailList.get(position).getFine1());
        tvCoapTextFine2.setText(mCoAPDetailList.get(position).getFine2());
        // set coap text idto tag
        view.setTag(mCoAPDetailList.get(position).getId());

        return view;
    }
}
