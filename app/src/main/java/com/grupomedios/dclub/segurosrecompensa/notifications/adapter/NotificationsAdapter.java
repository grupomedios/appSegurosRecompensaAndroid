package com.grupomedios.dclub.segurosrecompensa.notifications.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grupomedios.dclub.segurosrecompensa.R;
import com.grupomedios.dclub.segurosrecompensa.notifications.model.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Discount adapter list
 *
 * @author jhon
 */
public class NotificationsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Notification> discountsList;

    public NotificationsAdapter(Context context, List<Notification> discountModels) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        discountsList = discountModels;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return discountsList.size();
    }

    @Override
    public Object getItem(int position) {
        return discountsList != null && position >= 0 && position < discountsList.size() ? discountsList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        convertView = mInflater.inflate(R.layout.item_notification, parent, false);
        holder = new ViewHolder();
        holder.date = (TextView) convertView.findViewById(R.id.item_notification_date);
        holder.title = (TextView) convertView.findViewById(R.id.item_notification_title);
        holder.message = (TextView) convertView.findViewById(R.id.item_notification_message);
        convertView.setTag(holder);

        Notification notification = (Notification) getItem(position);

        if (notification != null) {
            holder.date.setText(getDate(notification.getQueuedAt()));
            if (notification.getHeadings().getEs() != null && !notification.getHeadings().getEs().isEmpty())
                holder.title.setText(notification.getHeadings().getEs());
            else {
                if (notification.getHeadings().getEn() != null && !notification.getHeadings().getEn().isEmpty())
                    holder.title.setText(notification.getHeadings().getEn());
                else {
                    holder.title.setText("");
                }
            }

            if (notification.getContents().getEs() != null && !notification.getContents().getEs().isEmpty())
                holder.message.setText(notification.getContents().getEs());
            else {
                if (notification.getContents().getEn() != null && !notification.getContents().getEn().isEmpty())
                    holder.message.setText(notification.getContents().getEn());
                else {
                    holder.message.setText("");
                }
            }
        } else {
            holder.message.setText("");
            holder.title.setText("");
            holder.date.setText("");
        }
        return convertView;
    }

    private String getDate(long timeStamp) {

        try {
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date netDate = (new Date(timeStamp * 1000L));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "";
        }
    }

    private static class ViewHolder {
        public TextView date;
        public TextView title;
        public TextView message;
    }
}
