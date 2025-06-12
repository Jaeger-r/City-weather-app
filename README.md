# City-weather-app
# 天气查询客户端

## 项目介绍

语言=》kotlin+java
本项目为抖音客户端训练营结项项目
writer（以下排名不分顺序，由首字母进行排序）

- 聂方正
- 王嘉明
- 张文杰

本项目将网络模块，数据模块，业务模块进行解耦

- 网络模块使用OkHTTP发起天气查询请求，并解析返回的JSON数据
- 数据模块：使用存储历史记录
- 业务模块：实现城市天气查询，城市添加，天气显示等等

## 项目结构

本项目项目文件结构如下
com.example.weatherproject
activities =》视图模块
	AddCityActivity

​	CityListActivity

​	temActivity

​	WeatherActivity
adapters =》适配器模块
​	DailyWeatherAdapter
​	HourlyWeatherAdapter
 db =》数据模块
​	WeatherData

​	WeatherDbHelper

​	WeatherJsonParser

​	WeatherRepository
 models =》数据类模块

​	 DailyWeather

​	HourlyWeather

​	City

network =》网络模块

​	WeatherNetwork

​	WeatherViewModel

​	WeatherViewModelFactory

## 总体设计

页面转换设计如下
![alt text](image-2.png)
系统流程图如下
![alt text](image-1.png)

## 详细设计

### 视图设计

Layout
├── city_list=>显示城市列表
├── item_city=>城市列表卡片
├── item_dayly_weather=》每天天气卡片
├── weather_item=>每小时天气卡片
└── main_weather=>主界面设计

以上视图设计均使用constraintlayout

### 数据库设计

### 网络模块设计

传入城市名字，首先通过和风天气API获取城市的ID和经纬度信息，再通过城市ID获取实时天气信息，每小时天气信息和一周内每天的天气信息，通过经纬度信息获取城市的空气质量信息。

WeatherNetwork（）主要实现了调用和风天气API获取信息的四个挂起函数，可以在其他模块中异步执行。

WeatherViewModel（）主要使用WeatherNetwork中的四个函数，分别对其数据进行处理和筛选，最后打包成hourlyList小时天气信息，dailyLIst一周内每天的天气信息，cityResult城市简要信息。

在AddCityActivity中可以对输入的城市进行判断该城市是否存在数据库中，若有则不进行插入，若没有则进行插入操作并进行网络接口的使用以获取天气信息。

