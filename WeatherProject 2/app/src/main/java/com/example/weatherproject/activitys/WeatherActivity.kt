package com.example.weatherproject.activitys

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherproject.R
import com.example.weatherproject.WeatherViewModel
import com.example.weatherproject.WeatherViewModelFactory
import com.example.weatherproject.adapters.DailyWeatherAdapter
import com.example.weatherproject.adapters.HourlyWeatherAdapter
import com.example.weatherproject.databinding.MainWeatherBinding
import com.example.weatherproject.models.DailyWeather
import com.example.weatherproject.models.HourlyWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: MainWeatherBinding
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 获取城市名
        val cityName = intent.getStringExtra("NEW_CITY_DATA") ?: return

        // 设置背景
        setBackgroundGradient()
        setupTopToolbar()

        // 初始化 ViewModel
        viewModel = ViewModelProvider(this, WeatherViewModelFactory(application))
            .get(WeatherViewModel::class.java)

        // 加载数据
        loadWeatherData(cityName)

    }

    private fun setBackgroundGradient() {
        val gradientBackground = ContextCompat.getDrawable(this, R.drawable.blue_gradient_bg)
        binding.root.background = gradientBackground
    }

    private fun setupTopToolbar() {
        binding.exitbutton.setOnClickListener {
            startActivity(Intent(this, CityListActivity::class.java))
        }
    }

    private fun setupRainAlert(hourlyData: List<HourlyWeather>) {
        val sdf = SimpleDateFormat("HH", Locale.getDefault())
        val currentHour = sdf.format(Date()).toInt()

        // 获取当前之后的两个小时
        val nextTwoHours = hourlyData.filter {
            try {
                val hour = it.time.substringBefore(":").toInt()
                hour in (currentHour + 1)..(currentHour + 2)
            } catch (e: Exception) {
                false
            }
        }

        val willRain = nextTwoHours.any { it.status.contains("雨") }

        binding.tvRainAlertTitle.text = "降雨提醒"
        binding.tvRainAlertContent.text = if (willRain) {
            "未来两小时可能下雨，记得带伞"
        } else {
            "未来两小时不会降雨，可以放心出门"
        }
    }

    private fun loadWeatherData(city: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val citySummary = viewModel.fetchCitySummary(city)
            val hourlyList = viewModel.fetchHourlyWeather(city)
            val dailyList = viewModel.fetchDailyWeather(city)
            setupRainAlert(hourlyList)
            withContext(Dispatchers.Main) {
                citySummary?.let {
                    binding.tvCurrentTemp.text = it.wendu
                    binding.tvWeatherStatus.text = "${it.weather} ${it.wendu} ${it.cleathe}"
                    binding.location.text = it.position
                }

                setupHourlyForecast(hourlyList)
                setupDailyForecast(dailyList)
            }
        }
    }

    private fun setupHourlyForecast(data: List<HourlyWeather>) {
        binding.rvHourlyWeather.apply {
            layoutManager = LinearLayoutManager(this@WeatherActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = HourlyWeatherAdapter(data)
        }
    }

    private fun setupDailyForecast(data: List<DailyWeather>) {
        binding.rvDaylyWeather.apply {
            layoutManager = LinearLayoutManager(this@WeatherActivity)
            adapter = DailyWeatherAdapter(data)
        }
    }
}