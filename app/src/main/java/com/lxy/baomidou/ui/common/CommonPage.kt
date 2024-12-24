package com.lxy.baomidou.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * @Author liuxy
 * @Date 2024/12/21
 * @Desc 通用的页面组件
 */


/**
 * 空数据展示
 */
@Composable
fun EmptyContentPage(
    modifier: Modifier = Modifier,
    message: String = "暂无数据",
){
    Box(
        modifier = modifier.fillMaxSize()
    ){
        Text(text = message, modifier = Modifier.align(Alignment.Center))
    }
}