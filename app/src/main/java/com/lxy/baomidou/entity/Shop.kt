package com.lxy.baomidou.entity

/**
 * @Author liuxy
 * @Date 2024/12/27
 * @Desc
 */
data class Shop(
    val id: String = "",
    val parentId: String = "",
    val dictValue: String = "",
    val remark: String = "",
    val children: List<Shop>? = emptyList(),
)
