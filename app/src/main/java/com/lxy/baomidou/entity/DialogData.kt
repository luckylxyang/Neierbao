package com.lxy.baomidou.entity

/**
 * @Author liuxy
 * @Date 2024/12/27
 * @Desc
 */
data class DialogData(
    val title: String = "",
    val label: String = "",
    val itemList: List<String> = emptyList(),
)
