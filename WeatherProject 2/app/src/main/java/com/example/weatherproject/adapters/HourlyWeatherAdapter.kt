package com.example.weatherproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherproject.R
import com.example.weatherproject.models.HourlyWeather

class HourlyWeatherAdapter(
    private val items: List<HourlyWeather>
) : RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder>() {

    inner class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvStatus: TextView = itemView.findViewById(R.id.tvWeather)
        val tvTemp: TextView = itemView.findViewById(R.id.tvTemperature)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.weather_item, parent, false)
        return HourlyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item = items[position]
        holder.tvTime.text = item.time
        holder.tvStatus.text = item.status
        holder.tvTemp.text = item.temperature
    }

    override fun getItemCount(): Int = items.size
}