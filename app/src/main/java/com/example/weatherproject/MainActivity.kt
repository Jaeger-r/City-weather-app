package com.example.weatherproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.weatherproject.activitys.CityListActivity
import com.example.weatherproject.activitys.WeatherActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 直接启动MemoListActivity
        startActivity(Intent(this, CityListActivity::class.java))
        finish()  // 关闭当前Activity
    }
}