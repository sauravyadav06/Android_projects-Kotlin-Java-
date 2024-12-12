package com.psl.inventorydemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.psl.inventorydemo.DashboardActivity;
import com.psl.inventorydemo.R;
import com.psl.inventorydemo.model.DashboardModel;

import java.util.List;

public class CustomRecyclerViewDashboardAdapter extends RecyclerView.Adapter {
    List<DashboardModel> dashboardModelList;
    Context context;
    public CustomRecyclerViewDashboardAdapter(Context context, List<DashboardModel> dashboardModelList) {
        this.context = context;
        this.dashboardModelList = dashboardModelList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.imagetextbutton, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // set the data in items
        MyViewHolder viewHolder= (MyViewHolder)holder;

        String textTitle = dashboardModelList.get(position).getTextTitle();
        int menu_id = dashboardModelList.get(position).getMenuID();
        switch (menu_id){
            case 1:
                viewHolder.name.setText("Asset Registration");
                viewHolder.image.setImageResource(R.drawable.assetregistration);
                break;
            case 2:
                viewHolder.name.setText("Check IN/ Check OUT");
                viewHolder.image.setImageResource(R.drawable.checkinoutnew);
                    break;
            case 3:
                viewHolder.name.setText("Asset Inventory");
                viewHolder.image.setImageResource(R.drawable.inventorynew);
                break;
            case 4:
                viewHolder.name.setText("Asset Search");
                viewHolder.image.setImageResource(R.drawable.searchnew);
                break;
            case 5:
                viewHolder.name.setText("Asset Pick");
                viewHolder.image.setImageResource(R.drawable.pick);
                break;
            case 6:
                viewHolder.name.setText("Asset Put");
                viewHolder.image.setImageResource(R.drawable.put);
                break;

            default:
                viewHolder.name.setText("Check IN/ Check OUT");
                viewHolder.image.setImageResource(R.drawable.checkin);
                break;
        }

         //implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context,"Person : "+(position+1),Toast.LENGTH_SHORT).show();
                if (context instanceof DashboardActivity) {
                    ((DashboardActivity) context).gridClicked(position,dashboardModelList.get(position).getMenuID(),dashboardModelList.get(position).getTextTitle().toString());
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return dashboardModelList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView name;
        ImageView image;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.text);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}