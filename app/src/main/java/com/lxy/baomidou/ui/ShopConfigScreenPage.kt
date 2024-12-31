package com.lxy.baomidou.ui

import android.widget.Toast
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lxy.baomidou.data.AppViewModel
import com.lxy.baomidou.data.ShopUIState
import com.lxy.baomidou.data.ShopViewModel
import com.lxy.baomidou.entity.DialogData
import com.lxy.baomidou.entity.Shop
import com.lxy.baomidou.entity.ShopConfig
import com.lxy.baomidou.ui.approval.SearchItem
import com.lxy.baomidou.ui.common.CommonListDialog
import com.lxy.baomidou.ui.common.EmptyContentPage

/**
 * @Author liuxy
 * @Date 2024/12/21
 * @Desc 门店配置信息列表
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ShopConfigScreenPage(
    mViewModel: ShopViewModel = viewModel(),
    appModel: AppViewModel
) {

    val bottomSheetState = rememberModalBottomSheetState()
    val cityShop by appModel.shopList.collectAsState()
    val dialogDataList by appModel.dialogDataList.collectAsState()
    var showTipsDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(cityShop) {
        appModel.createDialogData(cityShop)
    }
    val uiState by mViewModel.uiState.collectAsState()

    // 添加下拉刷新状态
    val refreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = {
            mViewModel.refreshUIState(cityList = cityShop)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(refreshState)  // 添加下拉刷新
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
                                    mViewModel.refreshUIState(selectedShop = shop, showDialog = true)
                                }
                            )
                        }
                    }
                }

                // 底部弹出编辑页面
                if (uiState.showDialog) {
                    ModalBottomSheet(
                        onDismissRequest = { mViewModel.refreshUIState(showDialog = false) },
                        sheetState = bottomSheetState,
                        dragHandle = { BottomSheetDefaults.DragHandle() }
                    ) {
                        EditShopConfigSheet(
                            dialogData = dialogDataList,
                            shopList = cityShop,
                            item = successState.selectedShop,
                            onDismiss = { mViewModel.refreshUIState(showDialog = false) },
                            onSave = {
                                if (it.id > 0) {
                                    mViewModel.saveShopConfig(it)
                                } else {
                                    mViewModel.addShopConfig(it)
                                }
                            },
                            onCityClick = {
                                appModel.onCitySelect(it)
                            }
                        )
                    }
                }
            }
        }
        if (uiState.isSuccess){
            Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show()
            mViewModel.refreshUIState(isSuccess = false)
        }
        if (uiState.errorMsg.isNotEmpty()){
            // 弹 dialog
            AlertDialog(
                onDismissRequest = { showTipsDialog = false },
                title = { Text("操作失败") },
                text = { Text(uiState.errorMsg) },
                confirmButton = {
                    TextButton(onClick = {
                        showTipsDialog = false
                        mViewModel.refreshUIState(errorMsg = "")
                    }) {
                        Text("确定")
                    }
                }
            )
        }

        // 添加刷新指示器
        PullRefreshIndicator(
            refreshing = uiState.isLoading,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onClick = {
                mViewModel.refreshUIState(selectedShop = ShopConfig(), showDialog = true)
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
    dialogData: Map<String, DialogData>,
    shopList: List<Shop>,
    item: ShopConfig,
    onDismiss: () -> Unit,
    onSave: (ShopConfig) -> Unit,
    onCityClick: (Int) -> Unit,
) {
    var shopConfig by remember { mutableStateOf(item) }
    var showDialog by remember { mutableStateOf(false) }
    var selectDialogData by remember { mutableStateOf(DialogData()) }
    var selectItem by remember { mutableStateOf("") }

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
            TextButton(onClick = { onSave(shopConfig) }) {
                Text("保存")
            }
        }

        //TODO 门店选择通过弹窗
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
//            // 城市选择
//            OutlinedTextField(
//                value = shop.areaName,
//                onValueChange = { shop = shop.copy(areaName = it) },
//                label = { Text("城市") },
//                modifier = Modifier.weight(1f),
//                singleLine = true
//            )
//
//            // 店铺选择
//            OutlinedTextField(
//                value = shop.shopName,
//                onValueChange = { shop = shop.copy(shopName = it) },
//                label = { Text("店铺") },
//                modifier = Modifier.weight(2f),
//                singleLine = true
//            )
            // 城市选择
            SearchItem(
                text = shopConfig.areaName,
                modifier = Modifier.weight(1f),
                onClick = {
                    showDialog = !showDialog
                    selectDialogData = dialogData.getValue("city")
                    selectItem = "city"
                }
            )
            // 店铺选择
            SearchItem(
                text = shopConfig.shopName,
                modifier = Modifier.weight(1f),
                onClick = {
                    showDialog = !showDialog
                    selectDialogData = dialogData.getValue("shop")
                    selectItem = "shop"
                }
            )
        }

        // 等待数量
        OutlinedTextField(
            value = if (shopConfig.maxCountPerDay == 0) "" else "${shopConfig.maxCountPerDay}",
            onValueChange = { newValue ->
                shopConfig = when {
                    newValue.isEmpty() -> shopConfig.copy(maxCountPerDay = 0)
                    newValue.all { it.isDigit() } -> try {
                        shopConfig.copy(maxCountPerDay = newValue.toInt())
                    } catch (e: NumberFormatException) {
                        shopConfig
                    }
                    else -> shopConfig
                }
            },
            label = { Text("每日数量") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        // spts
        shopConfig.spts?.let {
            OutlinedTextField(
                value = it,
                onValueChange = { shopConfig = shopConfig.copy(spts = it) },
                label = { Text("spts") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        // 备注
        shopConfig.remark?.let {
            OutlinedTextField(
                value = it,
                onValueChange = { shopConfig = shopConfig.copy(remark = it) },
                label = { Text("备注") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                minLines = 3,
                maxLines = 5
            )
        }
    }

    if (showDialog) {
        CommonListDialog(
            title = selectDialogData.title,
            itemList = selectDialogData.itemList,
            onDismissRequest = { showDialog = false },
            onItemClick = { item, index ->
                if (selectItem == "city") {
                    onCityClick(index)
                    shopConfig = shopConfig.copy(areaName = item)
                }
                if (selectItem == "shop") {
                    getShopIdsByName(shopConfig, item, shopList = shopList)?.let {
                        shopConfig = shopConfig.copy(shopId = it.remark, shopName = it.dictValue)
                    }

                }
            }
        )
    }
}

private fun getShopIdsByName(
    shopConfig: ShopConfig,
    shopName: String,
    shopList: List<Shop>
): Shop? {
    if (shopList.isEmpty()) return null
    return shopList.filterIndexed { idx, shop -> shopConfig.areaName == shop.dictValue }// 找到选择的城市
        .map { shop -> shop.children!!.filter { item -> item.dictValue == shopName } } // 找到城市下选择的店铺
        .firstOrNull()?.firstOrNull()

}