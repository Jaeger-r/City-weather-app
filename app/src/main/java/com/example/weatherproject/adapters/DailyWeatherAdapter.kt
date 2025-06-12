package com.example.weatherproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherproject.R
import com.example.weatherproject.models.DailyWeather

class DailyWeatherAdapter(
    private val items: List<DailyWeather>
) : RecyclerView.Adapter<DailyWeatherAdapter.DailyViewHolder>() {

    inner class DailyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: TextView = itemView.findViewById(R.id.tvDate)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvTempRange: TextView = itemView.findViewById(R.id.tvTemperatureRange)
        val ivIcon: TextView = itemView.findViewById(R.id.tvHumidity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dayly_weather, parent, false)
        return DailyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val item = items[position]
        holder.tvDay.text = item.day
        holder.tvStatus.text = item.status
        holder.tvTempRange.text = item.temperatureRange
        holder.ivIcon.text = item.iconDayResId
    }

    override fun getItemCount(): Int = items.size
}