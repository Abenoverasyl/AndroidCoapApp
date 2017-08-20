package com.example.erasyl.coap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Erasyl on 21.04.2017.
 */

public class NotificationAdapter extends BaseAdapter {

    private Context mContext;
    private List<NotificationForUser> mNotificationList;

    public NotificationAdapter(Context mContext, List<NotificationForUser> mNotificationList) {
        this.mContext = mContext;
        this.mNotificationList = mNotificationList;
    }

    @Override
    public int getCount() {
        return mNotificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNotificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.item_notification_list, null);
        TextView tvTitle = (TextView)view.findViewById(R.id.tvTitle);
        TextView tvMessage = (TextView)view.findViewById(R.id.tvMessage);
        // Set text for TextView
        tvTitle.setText(mNotificationList.get(position).getTitle());
        tvMessage.setText(mNotificationList.get(position).getMessage());
        // set coap text idto tag
        view.setTag(mNotificationList.get(position).getId());

        return view;
    }
}