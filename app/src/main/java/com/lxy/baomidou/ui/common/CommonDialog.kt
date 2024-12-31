package com.lxy.baomidou.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * @Author liuxy
 * @Date 2024/12/27
 * @Desc 通用的弹窗
 */

@Composable
fun CommonListDialog(
    title: String = "提示",
    itemList: List<String> = emptyList(),
    onDismissRequest: ()-> Unit,
    onItemClick:(String, Int)-> Unit
){
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            LazyColumn(modifier = Modifier
                .heightIn(max = 300.dp) // 设置最大高度
                .fillMaxWidth()
            ) {
                itemsIndexed(itemList) { index, item ->
                    ListItem(
                        headlineContent = { Text(item) },
                        modifier = Modifier.clickable {
                            onDismissRequest()
                            onItemClick(item, index)
                        }
                    )
                    Divider()
                }
            }
        },
        confirmButton = {}
    )
}