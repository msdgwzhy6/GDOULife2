package com.fedming.gdoulife.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fedming.gdoulife.R;

import java.util.List;

/**
 * MySpinnerAdapter
 * <p>
 * Created by Bruce on 2016/10/28.
 */

public class MySpinnerAdapter extends BaseAdapter {

    private List<SparseArray<String>> list;
    private Context context;

    public MySpinnerAdapter(Context context, List<SparseArray<String>> list) {

        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);

            holder.textView = (TextView) convertView.findViewById(R.id.textView);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText("");

        return convertView;
    }

    private class ViewHolder {

        private TextView textView;
    }
}
