package com.psl.inventorydemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.psl.inventorydemo.R;

import java.util.ArrayList;
import java.util.HashMap;

public class InventoryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    public ArrayList<HashMap<String, String>> tagList;
    private Context mContext;
    public int CURRENT_INDEX = -1;

    public InventoryAdapter(Context context, ArrayList<HashMap<String, String>> tagList) {
        this.mInflater = LayoutInflater.from(context);
        this.tagList = tagList;
        this.mContext = context;
    }
    @Override
    public int getCount() {
        return tagList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.inventory_adapter_layout, null);
            holder.textId = (TextView) convertView.findViewById(R.id.textId);
            holder.textKitId = (TextView) convertView.findViewById(R.id.textKitId);

            convertView.setTag(holder);
        } else {
            holder = (InventoryAdapter.ViewHolder) convertView.getTag();
        }

        int id = position+1;
        holder.textId.setText(""+id);
        holder.textKitId.setText(tagList.get(position).get("AssetName"));
        holder.textKitId.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.textId.setTextColor(mContext.getResources().getColor(R.color.black));
        if (position%2!=0) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.lightblue1));
        }
        else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.lemonyellow));
        }

        return convertView;
    }
    public final class ViewHolder {
        public TextView textId;
        public TextView textKitId;

    }
}
