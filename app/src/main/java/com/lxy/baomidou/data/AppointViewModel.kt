package com.lxy.baomidou.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lxy.baomidou.entity.AppointHistory
import com.lxy.baomidou.entity.AppointStatus
import com.lxy.baomidou.entity.AppointType
import com.lxy.baomidou.entity.DialogData
import com.lxy.baomidou.entity.ShopConfig
import com.lxy.baomidou.entity.SearchEntity
import com.lxy.baomidou.entity.Shop
import com.supermap.sinfcloud.basecomponent.ext.launchSafety
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.map

/**
 * @Author liuxy
 * @Date 2024/12/21
 * @Desc
 */
sealed interface AppointUIState {
    val isLoading: Boolean
    val isSuccess: Boolean
    val errorMsg: String
    val search: SearchEntity
    val dialogDataList: Map<String, DialogData>

    data class InitOrEmpty(
        val isEmpty: Boolean,
        override val errorMsg: String,
        override val isSuccess: Boolean,
        override val isLoading: Boolean,
        override val search: SearchEntity,
        override val dialogDataList: Map<String, DialogData>,
    ) : AppointUIState

    data class Success(
        override val isLoading: Boolean,
        override val isSuccess: Boolean,
        override val errorMsg: String,
        override val dialogDataList: Map<String, DialogData>,
        override val search: SearchEntity,
        val appointList: List<AppointHistory>,
        val configList: List<ShopConfig>,
        val shopList: List<Shop>
    ) : AppointUIState
}

private data class AppointVMUIState(
    val isLoading: Boolean = true,
    val isSuccess: Boolean = false,
    val errorMsg: String = "",
    val isEmpty: Boolean = true,
    val search: SearchEntity = SearchEntity(),
    val appointList: List<AppointHistory> = emptyList(),
    val shopList: List<Shop> = emptyList(),
    val dialogDataList: Map<String, DialogData> = emptyMap(),
    val configList: List<ShopConfig> = emptyList()
) {
    fun toUIState(): AppointUIState = if (appointList.isEmpty()) {
        AppointUIState.InitOrEmpty(
            isEmpty = true,
            isLoading = isLoading,
            isSuccess = isSuccess,
            search = search,
            dialogDataList = dialogDataList,
            errorMsg = errorMsg
        )
    } else {
        AppointUIState.Success(
            isLoading = isLoading,
            isSuccess = isSuccess,
            errorMsg = errorMsg,
            appointList = appointList,
            search = search,
            configList = configList,
            dialogDataList = dialogDataList,
            shopList = shopList,
        )
    }
}

class AppointViewModel : ViewModel() {

    private val repo = AppRepo()

    private val _uiState = MutableStateFlow(AppointVMUIState())
    val uiState = _uiState.map(AppointVMUIState::toUIState)
        .stateIn(
            viewModelScope, SharingStarted.Eagerly,
            _uiState.value.toUIState()
        )

    init {
        getAppointList()
    }

    fun getAppointList() {
        // 获取预约列表
        viewModelScope.launchSafety(Dispatchers.IO) {
            val list = repo.getAppointHistoryList(uiState.value.search)
            _uiState.update { it.copy(appointList = list, isLoading = false) }
        }.onCatch{
            it.printStackTrace()
        }
    }

    fun refreshUiState(
        isLoading: Boolean? = null,
        isSuccess: Boolean? = null,
        errorMsg: String? = null,
        isEmpty: Boolean? = null,
        appointList: List<AppointHistory>? = null,
        shopList: List<Shop>? = null,
        configList: List<ShopConfig>? = null,
    ) {
        _uiState.update {
            it.copy(
                isLoading = isLoading ?: it.isLoading,
                isSuccess = isSuccess ?: it.isSuccess,
                errorMsg = errorMsg ?: it.errorMsg,
                isEmpty = isEmpty ?: it.isEmpty,
                appointList = appointList ?: it.appointList,
                configList = configList ?: it.configList,
                shopList = shopList ?: it.shopList,
            )
        }
    }

    fun refreshSearch(
        phone: String? = null,
        areaId: String? = null,
        areaName: String? = null,
        status: Int? = null,
        type: Int? = null,
        shopName: String? = null,
        shopId: String? = null,
    ) {
        _uiState.update { state ->
            val search = state.search.copy(
                phone = phone ?: state.search.phone,
                areaId = areaId ?: state.search.areaId,
                status = status ?: state.search.status,
                shopName = shopName ?: state.search.shopName,
                areaName = areaName ?: state.search.areaName,
                type = type ?: state.search.type,
                shopId = shopId ?: state.search.shopId,
            )
            state.copy(
                search = search
            )
        }
    }

    fun where(key: String, value: String) {
        when (key) {
            "phone" -> refreshSearch(phone = value)
            "city" -> {
                refreshSearch(areaName = value)
                getCityIdByName(cityName = value)
            }

            "status" -> {
                refreshSearch(status = AppointStatus.fromDesc(value).value)
            }

            "type" -> refreshSearch(type = AppointType.fromDesc(value).value)
            "shop" -> {
                // 同步更新店名
                refreshSearch(shopName = value)
                getShopIdsByName(value)
            }
        }
    }

    private fun getShopIdsByName(shopName: String) {
        if (_uiState.value.shopList.isEmpty()) return
        val city = _uiState.value.search.areaId
        _uiState.value.shopList.filterIndexed { idx, shop -> city == shop.id }// 找到选择的城市
            .map { shop -> shop.children!!.filter { item -> item.dictValue == shopName } } // 找到城市下选择的店铺
            .first()
            .map { shop -> refreshSearch(shopId = shop.remark) }

    }

    private fun getCityIdByName(cityName: String) {
        if (_uiState.value.shopList.isEmpty()) return
        val city = _uiState.value.shopList.first { shop -> cityName == shop.dictValue }
        refreshSearch(areaId = city.id)
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
        _uiState.update {
            it.copy(dialogDataList = map, shopList = list)
        }
    }

    fun onCitySelect(index: Int, list: List<Shop>) {
        val strList = list.filterIndexed { idx, shop -> index == idx }// 找到选择的城市
            .map { shop -> shop.children!!.map { item -> item.dictValue } }
            .first()
        _uiState.update {
            val copy = it.dialogDataList.getValue("shop").copy(itemList = strList)
            it.copy(dialogDataList = it.dialogDataList.toMutableMap().apply {
                this["shop"] = copy
            })
        }
    }

    /**
     * 更新电话号码
     */
    fun updatePhone(history: AppointHistory) {
        refreshUiState(isLoading = true)
        viewModelScope.launchSafety(Dispatchers.IO){
            repo.updatePhone(history)
        }.onSuccess {
            getAppointList()
        }.onCatch{
            it.printStackTrace()
        }.onComplete {
            refreshUiState(isLoading = false)
        }
    }

    /**
     * 取消预约
     */
    fun cancelApt(history: AppointHistory) {
        refreshUiState(isLoading = true)
        viewModelScope.launchSafety(Dispatchers.IO){
            repo.cancelApt(history)
        }.onSuccess {
            getAppointList()
        }.onCatch{
            it.printStackTrace()
        }.onComplete {
            refreshUiState(isLoading = false)
        }
    }
}