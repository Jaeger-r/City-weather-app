package com.example.weatherproject.activitys

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.withStyledAttributes
import com.example.weatherproject.R

// 数据类，用于表示每小时的天气信息
data class HourlyWeatherData(
    val time: String = "",
    val weather: String = "",
    val temperature: String = "",
    @ColorInt val textColor: Int = android.graphics.Color.WHITE
)

class WeatherHourlyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var tvTime: TextView
    private var tvWeather: TextView
    private var tvTemperature: TextView

    init {
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.weather_item, this, true)
        orientation = VERTICAL
        gravity = android.view.Gravity.CENTER

        // 查找视图
        tvTime = findViewById(R.id.tvTime)
        tvWeather = findViewById(R.id.tvWeather)
        tvTemperature = findViewById(R.id.tvTemperature)

        // 处理自定义属性
        context.withStyledAttributes(attrs, R.styleable.WeatherHourlyView) {
            // 从XML属性中获取颜色（如果设置了）
            val defaultColor = android.graphics.Color.WHITE
            val textColor = getColor(R.styleable.WeatherHourlyView_textColor, defaultColor)
            tvTime.setTextColor(textColor)
            tvWeather.setTextColor(textColor)
            tvTemperature.setTextColor(textColor)
        }
    }

    // 设置天气数据
    fun setWeatherData(data: HourlyWeatherData) {
        tvTime.text = data.time
        tvWeather.text = data.weather
        tvTemperature.text = data.temperature

        // 设置自定义颜色（如果提供）
        if (data.textColor != android.graphics.Color.WHITE) {
            tvTime.setTextColor(data.textColor)
            tvWeather.setTextColor(data.textColor)
            tvTemperature.setTextColor(data.textColor)
        }
    }

    // 辅助方法 - 使用字符串直接设置
    fun setWeatherData(time: String, weather: String, temperature: String) {
        setWeatherData(HourlyWeatherData(time, weather, temperature))
    }
}