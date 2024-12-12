package com.example.myexcel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myexcel.Database.TransactionData
import com.example.myexcel.R

class TransactionAdapter(
    private val transactionList: MutableList<TransactionData>, // Use MutableList to support deletions
    private val onDeleteClicked: (Int) -> Unit // Callback for delete button
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val skuCode: TextView = itemView.findViewById(R.id.tvSkuCode)
        val skuName: TextView = itemView.findViewById(R.id.tvSkuName)
        val quantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.skuCode.text = transaction.skuCode
        holder.skuName.text = transaction.skuName
        holder.quantity.text = transaction.quantity

        // Set delete button action
        holder.deleteButton.setOnClickListener {
            onDeleteClicked(position)
        }
    }

    override fun getItemCount(): Int = transactionList.size
}
