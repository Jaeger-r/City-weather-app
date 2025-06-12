package com.example.weatherproject.activitys

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherproject.WeatherViewModel
import com.example.weatherproject.models.City
import com.example.weatherproject.ui.theme.WeatherProjectTheme
import com.example.weatherproject.models.DailyWeather
import com.example.weatherproject.models.HourlyWeather
import kotlinx.coroutines.launch

class AddCityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WeatherProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(
                        modifier = Modifier.padding(innerPadding),
                        onCityAdded = { city, hourlyList, dailyList ->
                            Toast.makeText(this, "添加成功：${city}", Toast.LENGTH_SHORT).show()
//                            val intent = Intent(this, CityListActivity::class.java).apply {
//                                putExtra("NEW_CITY_DATA", city)
//                            }
//                            startActivity(intent)
//                            finish()
                            val resultIntent = Intent().apply {
                                putExtra("NEW_CITY_DATA", city)
                            }
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        },
                        onError = { error ->
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    onCityAdded: (City, List<HourlyWeather>, List<DailyWeather>) -> Unit,
    onError: (String) -> Unit,
    viewModel: WeatherViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? AppCompatActivity
    var inputCity by remember { mutableStateOf("") }
    val errorMessage = viewModel.errorMessage
    val city = viewModel.cityResult
    val hourly = viewModel.hourlyList
    val daily = viewModel.dailyList

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            onError(errorMessage)
        }
    }

    LaunchedEffect(city) {
        city?.let { onCityAdded(it, hourly, daily) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = inputCity,
            onValueChange = { inputCity = it },
            label = { Text("输入城市名") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    val result = viewModel.fetchCitySummary(inputCity)
                    if (result != null) {
                        onCityAdded(result, viewModel.hourlyList, viewModel.dailyList)
                    } else {
                        onError("城市添加失败，请检查名称")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("添加城市")
        }
        Button(
            onClick = { activity?.finish() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("返回")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherPreview() {
    WeatherProjectTheme {
        WeatherScreen(
            onCityAdded = { _, _, _ -> },
            onError = { _ -> }
        )
    }
}