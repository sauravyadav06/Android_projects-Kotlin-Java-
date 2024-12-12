package com.example.myexcel

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import java.util.Locale


class SearchableAdapter(private val context: Context, private val originalData: ArrayList<String>) :
    BaseAdapter(), Filterable {
    private var filteredData: ArrayList<String>

    init {
        this.filteredData = originalData
    }

    override fun getCount(): Int {
        return filteredData.size
    }

    override fun getItem(position: Int): Any {
        return filteredData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.simple_list_item_1, parent, false)

        }

        val item = filteredData[position]

        // Customize the view as needed
        val textView = convertView?.findViewById<TextView>(R.id.text1)
        if (textView != null) {
            textView.text = item
        }

        return convertView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterString = charSequence.toString().lowercase(Locale.getDefault())

                val results: FilterResults = FilterResults()

                val list = originalData

                val count = list.size
                val nlist = ArrayList<String>(count)

                for (item in list) {
                    if (item.lowercase(Locale.getDefault()).contains(filterString)) {
                        nlist.add(item)
                    }
                }

                results.values = nlist
                results.count = nlist.size

                return results
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                filteredData = filterResults.values as ArrayList<String>
                notifyDataSetChanged()
            }
        }
    }
}