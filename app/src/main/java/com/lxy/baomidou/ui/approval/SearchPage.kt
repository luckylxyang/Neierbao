package com.lxy.baomidou.ui.approval

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lxy.baomidou.entity.AppointStatus
import com.lxy.baomidou.entity.AppointType
import com.lxy.baomidou.entity.DialogData
import com.lxy.baomidou.entity.SearchEntity
import com.lxy.baomidou.entity.Shop
import com.lxy.baomidou.ui.common.CommonListDialog
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager

/**
 * @Author liuxy
 * @Date 2024/12/27
 * @Desc 搜索条件的
 */


@Composable
fun SearchBarPage(
    searchEntity: SearchEntity = SearchEntity(),
    dialogData: Map<String, DialogData> = mapOf(),
    onChange: (String, String) -> Unit,
    onCityClick: (Int)-> Unit,
    onSearch:()-> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectDialogData by remember { mutableStateOf(DialogData()) }
    var selectItem by remember { mutableStateOf("") }
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
            SearchItem(
                text = searchEntity.areaName,
                modifier = Modifier.weight(1f),
                onClick = {
                    showDialog = !showDialog
                    selectDialogData = dialogData.getValue("city")
                    selectItem = "city"
                }
            )
            // 店铺选择
            SearchItem(
                text = searchEntity.shopName,
                modifier = Modifier.weight(1f),
                onClick = {
                    showDialog = !showDialog
                    selectDialogData = dialogData.getValue("shop")
                    selectItem = "shop"
                }
            )
        }

        // 第二行：状态和类型
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 状态选择
            SearchItem(
                text = AppointStatus.fromValue(searchEntity.status).desc,
                modifier = Modifier.weight(1f),

                onClick = {
                    showDialog = !showDialog
                    selectDialogData = dialogData.getValue("status")
                    selectItem = "status"
                }
            )

            // 类型选择
            SearchItem(
                text = AppointType.fromValue(searchEntity.type).desc,
                modifier = Modifier.weight(1f),
                onClick = {
                    showDialog = !showDialog
                    selectDialogData = dialogData.getValue("type")
                    selectItem = "type"
                }
            )
        }
    }

    // 搜索框
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current as? ComponentActivity
    
    OutlinedTextField(
        value = searchEntity.phone,
        onValueChange = { newValue ->
            if (newValue.length <= 11 && newValue.all { it.isDigit() }) {
                onChange("phone", newValue)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        placeholder = { Text("请输入手机号") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { 
                focusManager.clearFocus() // 清除焦点
                (context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(context.window?.decorView?.windowToken, 0)
                onSearch()
            }
        ),
        trailingIcon = {
            TextButton(
                onClick = { 
                    focusManager.clearFocus() // 清除焦点
                    (context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(context?.window?.decorView?.windowToken, 0)
                    onSearch()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("搜索")
            }
        }
    )

    if (showDialog) {
        CommonListDialog(
            title = selectDialogData.title,
            itemList = selectDialogData.itemList,
            onDismissRequest = { showDialog = false },
            onItemClick = {item, index->
                if (selectItem == "city"){
                    onCityClick(index)
                }
                onChange(selectItem, item)
            }
        )

    }

}


@Composable
fun SearchItem(
    modifier: Modifier = Modifier,
    text: String = "请选择",
    onClick: () -> Unit,
) {
    // 状态选择
    OutlinedCard(
        modifier = modifier,
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }
    }
}