package com.example.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;


public class CustomAdapter extends BaseAdapter
{

    private List<RssResponse> filteredData = null;
    private LayoutInflater mInflater;


    public CustomAdapter(Context context, List<RssResponse> data)
    {
        this.filteredData = data ;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public List<RssResponse> getFilteredData() {
        return filteredData;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);

            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(android.R.id.text1);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(filteredData.get(position).getTitle());
        return convertView;
    }

    static class ViewHolder
    {
        TextView text;
    }
}