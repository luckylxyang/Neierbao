package com.lxy.baomidou.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lxy.baomidou.entity.AppointStatus
import com.lxy.baomidou.entity.AppointType
import com.lxy.baomidou.entity.DialogData
import com.lxy.baomidou.entity.Shop
import com.lxy.baomidou.entity.ShopConfig
import com.supermap.sinfcloud.basecomponent.ext.launchSafety
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.set

/**
 * @Author liuxy
 * @Date 2024/12/26
 * @Desc
 */
class AppViewModel : ViewModel() {
    private val repo = AppRepo()

    private val _shopList =  MutableStateFlow(emptyList<Shop>())
    val shopList get() = _shopList.asStateFlow()

    private val _dialogDataList =  MutableStateFlow(emptyMap<String, DialogData>())
    val dialogDataList get() = _dialogDataList.asStateFlow()

    init {
        viewModelScope.launchSafety(Dispatchers.IO){
            val list = repo.getShopList()
            if (list.isSuccess()) {
                _shopList.update { list.data }
            }
        }.onCatch{
            it.printStackTrace()
        }
    }


    fun createDialogData(list: List<Shop>) {
        val map: MutableMap<String, DialogData> = mutableMapOf()
        // 店铺联动
        map["city"] = DialogData(title = "选择城市", label = "city", list.map { shop ->
            shop.dictValue
        })
        map["status"] = DialogData(
            title = "状态",
            label = "status",
            AppointStatus.getStatusList().map { status -> status.desc })
        map["type"] = DialogData(
            title = "类型", label = "type",
            AppointType.getStatusList().map { status -> status.desc })
        map["shop"] = DialogData(title = "店铺", label = "shop")
        _dialogDataList.update { map }
    }

    fun onCitySelect(index: Int) {
        val list = _shopList.value
        val strList = list.filterIndexed { idx, shop -> index == idx }// 找到选择的城市
            .map { shop -> shop.children!!.map { item -> item.dictValue } }
            .first()
        _dialogDataList.update {
            val copy = it.getValue("shop").copy(itemList = strList)
            it.toMutableMap().apply {
                this["shop"] = copy
            }
        }
    }
}