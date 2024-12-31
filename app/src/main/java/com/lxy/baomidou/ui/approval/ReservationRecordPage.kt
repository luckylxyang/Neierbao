package com.lxy.baomidou.ui.approval

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lxy.baomidou.data.AppViewModel
import com.lxy.baomidou.data.AppointUIState
import com.lxy.baomidou.data.AppointViewModel
import com.lxy.baomidou.entity.AppointHistory
import com.lxy.baomidou.ui.common.EmptyContentPage
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import com.lxy.baomidou.ui.common.LoadingPage
import androidx.compose.ui.graphics.Color

/**
 * @Author liuxy
 * @Date 2024/12/20
 * @Desc
 */

/**
 * 预约记录列表页面
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReservationRecordListPage(
    mViewModel: AppointViewModel = viewModel(),
    appModel: AppViewModel
) {
    var showDetailSheet by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var selectedAppoint by remember { mutableStateOf<AppointHistory?>(null) }
    val bottomSheetState = rememberModalBottomSheetState()
    val uiState by mViewModel.uiState.collectAsState()
    val shopList by appModel.shopList.collectAsState()
    LaunchedEffect(shopList) {
        mViewModel.createDialogData(shopList)
    }
    val refreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = {
            mViewModel.refreshUiState(isLoading = true)
            mViewModel.getAppointList()
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(refreshState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SearchBarPage(
                searchEntity = uiState.search,
                dialogData = uiState.dialogDataList,
                onCityClick = {
                    mViewModel.onCitySelect(it, shopList)
                },
                onSearch = {
                    mViewModel.refreshUiState(isLoading = true)
                    mViewModel.getAppointList()
                },
                onChange = { key, value ->
                    // 搜索回调
                    mViewModel.where(key, value)
                }
            )

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
                                    selectedAppoint = item
                                    showDetailSheet = true
                                },
                                onCancel = {
                                    selectedAppoint = item
                                    showCancelDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
        if (uiState.isLoading) {
            LoadingPage()
        }

        // 刷新指示器
        PullRefreshIndicator(
            refreshing = uiState.isLoading,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    // 底部弹出的详情页
    if (showDetailSheet && selectedAppoint != null) {
        ModalBottomSheet(
            onDismissRequest = { showDetailSheet = false },
            sheetState = bottomSheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            AppointDetailContent(
                appoint = selectedAppoint!!,
                onPhoneChange = { newPhone ->
                    // 处理电话号码修改
                    selectedAppoint = selectedAppoint?.copy(phone = newPhone)
                },
                onDismiss = { showDetailSheet = false },
                onSave = {
                    // 处理保存逻辑
                    mViewModel.updatePhone(selectedAppoint!!)
                    showDetailSheet = false
                }
            )
        }
    }
    if (showCancelDialog && selectedAppoint != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("取消预约") },
            text = { Text("确定要取消预约吗？") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    mViewModel.cancelApt(selectedAppoint!!)
                }) {
                    Text("确定")
                }
            },
            dismissButton = { TextButton(onClick = { showCancelDialog = false }) { Text("取消") } }
        )
    }
}

@Composable
private fun ReservationCard(
    item: AppointHistory,
    onItemClick: () -> Unit,
    onCancel: () -> Unit
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "取消预约",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { onCancel() }
                )
            }
        }
    }
}

@Composable
private fun AppointDetailContent(
    appoint: AppointHistory,
    onPhoneChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
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
            Text("预约详情", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onSave) {
                Text("保存")
            }
        }

        // 可编辑的电话号码
        OutlinedTextField(
            value = appoint.phone,
            onValueChange = { newValue ->
                if (newValue.length <= 11 && newValue.all { it.isDigit() }) {
                    onPhoneChange(newValue)
                }
            },
            label = { Text("电话号码") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        // 只读信息展示
        DetailItem("门店", appoint.shopName)
        DetailItem("预约日期", appoint.appointmentDate)
        DetailItem("门票类型", appoint.ticketName)
        DetailItem("状态", appoint.getStatusDesc())
        DetailItem("类型", appoint.getTypeDesc())
        DetailItem("创建时间", appoint.createTime)
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}