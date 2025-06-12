package com.example.weatherproject.models

import java.io.Serializable

// 城市数据类
data class City(
    val position: String,  // 城市名称/位置
    val weather: String,  // 天气状况
    val wendu: String,    // 温度
    val cleathe: String,  // 空气质量
    var isCurrent: Boolean = false
): Serializable { // 明确声明实现 Serializable

    // 添加兼容性标识符
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
