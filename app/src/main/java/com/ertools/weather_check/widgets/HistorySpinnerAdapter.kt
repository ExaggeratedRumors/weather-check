package com.ertools.weather_check.widgets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.History
import com.ertools.weather_check.model.DataManager
import com.ertools.weather_check.utils.Utils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class HistorySpinnerAdapter (
    private val source: History,
    private val context: Context,
) : BaseAdapter() {
    private var locations: MutableList<String> = mutableListOf("Select favorite")

    init {
        locations.addAll(source.locations.map { it.name })

    }

    override fun getCount(): Int {
        return locations.size
    }

    override fun getItem(position: Int): Any {
        return locations[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_removable_spinner,
            parent,
            false
        )
        val cityNameTextView = view.findViewById<MaterialTextView>(R.id.city_name)
        val removeButton = view.findViewById<MaterialButton>(R.id.delete_button)

        cityNameTextView.text = locations[position]

        removeButton.setOnClickListener {
            source.modify {
                this.removeIf { it.name == (getItem(position) as String) }
            }
            locations.remove(getItem(position))
            DataManager.writeObject(Utils.HISTORY_PATH, source, context)
            notifyDataSetChanged()
        }

        return view
    }

}