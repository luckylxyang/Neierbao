package com.lxy.baomidou.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lxy.baomidou.data.ShopUIState
import com.lxy.baomidou.data.ShopViewModel
import com.lxy.baomidou.entity.ShopConfig
import com.lxy.baomidou.ui.common.EmptyContentPage

/**
 * @Author liuxy
 * @Date 2024/12/21
 * @Desc 门店配置信息列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopConfigScreenPage(
    mViewModel: ShopViewModel = viewModel()
) {

    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val uiState by mViewModel.uiState.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState) {
            is ShopUIState.InitOrEmpty -> EmptyContentPage()
            is ShopUIState.Success -> {
                val successState = (uiState as ShopUIState.Success)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 12.dp)
                ) {
                    // 店铺列表
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(successState.shopList) { shop ->
                            ShopConfigItem(
                                item = shop,
                                onItemClick = {
                                    mViewModel.refreshUIState(selectedShop = shop)
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                }

                // 底部弹出编辑页面
                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = bottomSheetState,
                        dragHandle = { BottomSheetDefaults.DragHandle() }
                    ) {
                        EditShopConfigSheet(
                            item = successState.selectedShop,
                            onDismiss = { showBottomSheet = false },
                            onSave = { mViewModel.saveShopConfig(it) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onClick = {
                mViewModel.refreshUIState(selectedShop = ShopConfig())
                showBottomSheet = true
            },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "添加店铺")
        }
    }


}

@Composable
private fun ShopConfigItem(
    item: ShopConfig,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onItemClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 店铺名称和ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.shopName,
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ID: ${item.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 店铺地址
            Text(
                text = item.areaName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 每日数量
            Text(
                text = "每日数量: ${item.maxCountPerDay}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 备注信息
            Text(
                text = "备注: ${item.remark}",
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditShopConfigSheet(
    item: ShopConfig,
    onDismiss: () -> Unit,
    onSave: (ShopConfig) -> Unit
) {
    var shop by remember { mutableStateOf(item) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 顶部标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "关闭")
            }
            Text("编辑", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { onSave(shop) }) {
                Text("保存")
            }
        }

        // 门店选择区域
        Text(
            "门店",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 城市选择
            OutlinedTextField(
                value = shop.areaName,
                onValueChange = { shop = shop.copy(areaName = it) },
                label = { Text("城市") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            // 店铺选择
            OutlinedTextField(
                value = shop.shopName,
                onValueChange = { shop = shop.copy(shopName = it) },
                label = { Text("店铺") },
                modifier = Modifier.weight(2f),
                singleLine = true
            )
        }

        // 等待数量
        OutlinedTextField(
            value = "${shop.maxCountPerDay}",
            onValueChange = { shop = shop.copy(maxCountPerDay = it.toInt()) },
            label = { Text("每日数量") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // spts
        shop.spts?.let {
            OutlinedTextField(
                value = it,
                onValueChange = { shop = shop.copy(spts = it) },
                label = { Text("spts") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        // 备注
        shop.remark?.let {
            OutlinedTextField(
                value = it,
                onValueChange = { shop = shop.copy(remark = it) },
                label = { Text("备注") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                minLines = 3,
                maxLines = 5
            )
        }
    }
}