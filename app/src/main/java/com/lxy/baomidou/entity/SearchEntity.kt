package com.lxy.baomidou.entity

/**
 * @Author liuxy
 * @Date 2024/12/21
 * @Desc
 */
data class SearchEntity(
    val phone: String = "",
    val status: Int = AppointStatus.PENDING.value,
    val type: Int = AppointType.DEFAULT.value,
    val areaId: String = "",
    val areaName: String = "",
    val shopName: String = "",
    var shopId: String = "",
)
