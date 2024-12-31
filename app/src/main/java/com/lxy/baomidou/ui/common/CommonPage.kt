package com.lxy.baomidou.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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

/**
 * 加载中页面
 */
@Composable
fun LoadingPage(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = Color.White
        )
    }
}