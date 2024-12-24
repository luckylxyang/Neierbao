package com.lxy.baomidou.entity

/**
 * @Author liuxy
 * @Date 2024/12/21
 * @Desc
 */
data class SearchEntity(
    val phone: String = "",
    val status: String = "未使用",
    val config: ShopConfig = ShopConfig()
)
