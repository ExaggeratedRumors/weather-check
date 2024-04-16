package com.ertools.weather_check.widgets

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.ertools.weather_check.R

class RemovableSpinnerAdapter (
    context: Context,
    private val items: List<String>
) : ArrayAdapter<String>(context, R.layout.item_removable_spinner, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        val textView = view.findViewById<TextView>(R.id.text_view)
        val deleteButton = view.findViewById<ImageButton>(R.id.delete_button)

        textView.text = getItem(position)

        deleteButton.setOnClickListener {
            remove(getItem(position))
            notifyDataSetChanged()
        }

        return view
    }
}