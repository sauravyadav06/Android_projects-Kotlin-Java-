package com.example.loginandsignup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapter(var con : Context, var  list: List<UserDataClassItem>) : RecyclerView.Adapter<MyAdapter.ViewHolder>(){
    inner class ViewHolder(v : View) : RecyclerView.ViewHolder(v)
    {

        var img = v.findViewById<ImageView>(R.id.RV_Image)
        var tvNmae= v.findViewById<TextView>(R.id.RV_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view= LayoutInflater.from(con).inflate(R.layout.list_item,parent,  false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(con).load(list[position].avatar_url).into(holder.img)
        holder.tvNmae.text= list[position].login
    }

}