package com.example.erasyl.coap;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Erasyl on 20.03.2017.
 */

public class PDDDataAdapter extends BaseAdapter {

    private Context mContext;
    private List<PddDetail> mPDDDetailList;

    public PDDDataAdapter(Context mContext, List<PddDetail> mPDDDetailList) {
        this.mContext = mContext;
        this.mPDDDetailList = mPDDDetailList;
    }

    @Override
    public int getCount() {
        return mPDDDetailList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPDDDetailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.item_pddtext_list, null);
        TextView tvPDDTextTitle = (TextView)view.findViewById(R.id.tvPDDTextTitle);
        TextView tvPDDTextContent = (TextView)view.findViewById(R.id.tvPDDTextContent);
        // Set text for TextView
//        tvPDDTextID.setText(mPDDDetailList.get(position).getId());
        tvPDDTextTitle.setText(mPDDDetailList.get(position).getTitle());
        tvPDDTextContent.setText(Html.fromHtml(mPDDDetailList.get(position).getContent()));
        // set coap text idto tag
        view.setTag(mPDDDetailList.get(position).getId());

        return view;
    }
}
