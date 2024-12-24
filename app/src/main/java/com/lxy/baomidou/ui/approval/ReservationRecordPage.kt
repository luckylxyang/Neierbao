package com.lxy.baomidou.ui.approval

import androidx.compose.foundation.clickable
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.accompanist.pager.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lxy.baomidou.data.AppointUIState
import com.lxy.baomidou.data.AppointViewModel
import com.lxy.baomidou.entity.AppointHistory
import com.lxy.baomidou.entity.SearchEntity
import com.lxy.baomidou.ui.common.EmptyContentPage

/**
 * @Author liuxy
 * @Date 2024/12/20
 * @Desc
 */

/**
 * 预约记录列表页面
 */
@Composable
fun ReservationRecordListPage(
    mViewModel: AppointViewModel = viewModel()
) {
    val uiState by mViewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

//        SearchContent()
        when (uiState) {
            is AppointUIState.InitOrEmpty -> EmptyContentPage()
            is AppointUIState.Success -> {
                val success = uiState as AppointUIState.Success
                // 预约列表
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(success.appointList) { item ->
                        ReservationCard(
                            item = item,
                            onItemClick = {

                            })
                    }
                }
            }
        }
    }
}


@Composable
private fun SearchContent(
    searchEntity: SearchEntity,
    onChange:()-> Unit
) {
    // 筛选条件区域
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 第一行：城市和地名
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 城市选择
            OutlinedCard(
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: 实现城市选择 */ }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("重庆")
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            // 地名选择
            OutlinedCard(
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: 实现地名选择 */ }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("光环购物公园店")
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
        }

        // 第二行：状态和类型
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 状态选择
            OutlinedCard(
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: 实现状态选择 */ }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("待使用")
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            // 类型选择
            OutlinedCard(
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: 实现类型选择 */ }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("默认")
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
        }
    }

    // 搜索框
    OutlinedTextField(
        value = "",
        onValueChange = { /* TODO: 实现搜索功能 */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        placeholder = { Text("请输入手机号") },
        trailingIcon = {
            TextButton(
                onClick = { /* TODO: 实现搜索功能 */ },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("搜索")
            }
        }
    )

}

@Composable
private fun ReservationCard(
    item: AppointHistory,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick()
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 店铺名称和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(item.shopName)
                Text(
                    text = item.getStatusDesc(),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 预约信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("预约日期")
                Text(text = item.appointmentDate)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("手机号")
                Text(text = item.phone)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("类型")
                Text(text = item.getTypeDesc())
            }
        }
    }
}