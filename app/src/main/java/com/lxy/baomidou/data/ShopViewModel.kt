package com.lxy.baomidou.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lxy.baomidou.entity.AppointHistory
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
import okhttp3.Dispatcher

/**
 * @Author liuxy
 * @Date 2024/12/21
 * @Desc
 */

sealed interface ShopUIState {
    val isLoading: Boolean
    val isSuccess: Boolean
    val errorMsg: String
    val showDialog: Boolean

    data class InitOrEmpty(
        val isEmpty: Boolean,
        override val errorMsg: String,
        override val showDialog: Boolean,
        override val isSuccess: Boolean,
        override val isLoading: Boolean
    ) : ShopUIState

    data class Success(
        override val isLoading: Boolean,
        override val isSuccess: Boolean,
        override val errorMsg: String,
        val shopList: List<ShopConfig>,
        val cityList: List<Shop>,
        val selectedShop: ShopConfig,
        val isDeleteModel: Boolean,
        override val showDialog: Boolean
    ) : ShopUIState
}

private data class ShopVMUIState(
    val isLoading: Boolean = true,
    val isSuccess: Boolean = false,
    val errorMsg: String = "",
    val isEmpty: Boolean = true,
    val selectedShop: ShopConfig = ShopConfig(),
    val showDialog: Boolean = false,
    val shopList: List<ShopConfig> = emptyList(),
    val cityList: List<Shop> = emptyList(),
    val isDeleteModel: Boolean = false
) {
    fun toUIState(): ShopUIState = if (shopList.isEmpty()) {
        ShopUIState.InitOrEmpty(
            isEmpty = true,
            isLoading = isLoading,
            isSuccess = isSuccess,
            showDialog = showDialog,
            errorMsg = errorMsg
        )
    } else {
        ShopUIState.Success(
            isLoading = isLoading,
            isSuccess = isSuccess,
            errorMsg = errorMsg,
            shopList = shopList,
            isDeleteModel = isDeleteModel,
            selectedShop = selectedShop,
            cityList = cityList,
            showDialog = showDialog,
        )
    }
}

class ShopViewModel : ViewModel() {

    private val repo = AppRepo()

    private val _uiState = MutableStateFlow(ShopVMUIState())
    val uiState = _uiState.map(ShopVMUIState::toUIState)
        .stateIn(
            viewModelScope, SharingStarted.Eagerly,
            _uiState.value.toUIState()
        )

    init {
        getShopConfigList()
    }

    fun getShopConfigList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repo.getShopConfigList()
            _uiState.update { it.copy(shopList = list, isLoading = false) }
        }
    }

    fun refreshUIState(
        isLoading: Boolean? = null,
        isSuccess: Boolean? = null,
        errorMsg: String? = null,
        isEmpty: Boolean? = null,
        selectedShop: ShopConfig? = null,
        shopList: List<ShopConfig>? = null,
        cityList: List<Shop>? = null,
        isDeleteModel: Boolean? = null,
        showDialog: Boolean? = null
    ) {
        _uiState.update {
            it.copy(
                isLoading = isLoading ?: it.isLoading,
                isSuccess = isSuccess ?: it.isSuccess,
                errorMsg = errorMsg ?: it.errorMsg,
                isEmpty = isEmpty ?: it.isEmpty,
                selectedShop = selectedShop ?: it.selectedShop,
                shopList = shopList ?: it.shopList,
                isDeleteModel = isDeleteModel ?: it.isDeleteModel,
                showDialog = showDialog ?: it.showDialog,
                cityList = cityList ?: it.cityList
            )
        }
    }


    fun saveShopConfig(shop: ShopConfig) {
        refreshUIState(isLoading = true)
        viewModelScope.launchSafety(Dispatchers.IO) {
            repo.updateShopConfig(shop)
        }.onSuccess { unit ->
            getShopConfigList()
        }.onCatch { e -> e.printStackTrace() }
            .onComplete { unit -> refreshUIState(isLoading = false) }
    }


    fun addShopConfig(shop: ShopConfig) {
        refreshUIState(isLoading = true)

        viewModelScope.launchSafety(Dispatchers.IO) {
            repo.addShopConfig(shop.copy(id = 0))
        }.onSuccess { unit ->
            refreshUIState(showDialog = false)
            getShopConfigList()
        }.onCatch { e -> e.printStackTrace() }
            .onComplete { unit -> refreshUIState(isLoading = false) }
    }
}