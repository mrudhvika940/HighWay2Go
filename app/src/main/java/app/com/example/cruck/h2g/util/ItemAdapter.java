package app.com.example.cruck.h2g.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.example.cruck.h2g.R;
import app.com.example.cruck.h2g.model.Item;

/**
 * Created by cruck on 10-06-2016.
 */
public class ItemAdapter extends ArrayAdapter<Item> {

    Context context;
    int layoutResourceId;
    Item data[] = null;

    public ItemAdapter(Context context, int layoutResourceId, Item[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WeatherHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new WeatherHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.distView = (TextView)row.findViewById(R.id.distView);
            row.setTag(holder);
        }
        else
        {
            holder = (WeatherHolder)row.getTag();
        }

        Item weather = data[position];
        holder.txtTitle.setText(weather.title);
        holder.imgIcon.setImageResource(weather.icon);
        holder.distView.setText(weather.distance);
        return row;
    }

    static class WeatherHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        TextView distView;
    }
}