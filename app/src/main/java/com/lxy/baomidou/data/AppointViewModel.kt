package com.lxy.baomidou.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lxy.baomidou.entity.AppointHistory
import com.lxy.baomidou.entity.ShopConfig
import com.lxy.baomidou.entity.SearchEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    data class InitOrEmpty(
        val isEmpty: Boolean,
        override val errorMsg: String,
        override val isSuccess: Boolean,
        override val isLoading: Boolean,
        override val search: SearchEntity,
    ) : AppointUIState

    data class Success(
        override val isLoading: Boolean,
        override val isSuccess: Boolean,
        override val errorMsg: String,
        val appointList: List<AppointHistory>,
        override val search: SearchEntity,
        val configList: List<ShopConfig>
    ) : AppointUIState
}

private data class AppointVMUIState(
    val isLoading: Boolean = true,
    val isSuccess: Boolean = false,
    val errorMsg: String = "",
    val isEmpty: Boolean = true,
    val search: SearchEntity = SearchEntity(null,null,null,null),
    val appointList: List<AppointHistory> = emptyList(),
    val configList: List<ShopConfig> = emptyList()
) {
    fun toUIState(): AppointUIState = if (appointList.isEmpty()) {
        AppointUIState.InitOrEmpty(
            isEmpty = true,
            isLoading = isLoading,
            isSuccess = isSuccess,
            search = search,
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
        // 获取预约列表
        viewModelScope.launch(Dispatchers.IO){
            val list = repo.getAppointHistoryList()
            _uiState.update{ it.copy(appointList = list)}
        }
    }

    fun refreshUiState(
        phone: String? = null,
        isLoading: Boolean? = null,
        isSuccess: Boolean? = null,
        errorMsg: String? = null,
        isEmpty: Boolean? = null,
        appointList: List<AppointHistory>? = null,
        configList: List<ShopConfig>? = null,
    ) {
        _uiState.update {
            it.copy(
                search = if (phone != null) {
                    it.search.copy(phone = phone)
                } else it.search,
                isLoading = isLoading ?: it.isLoading,
                isSuccess = isSuccess ?: it.isSuccess,
                errorMsg = errorMsg ?: it.errorMsg,
                isEmpty = isEmpty ?: it.isEmpty,
                appointList = appointList ?: it.appointList,
                configList = configList ?: it.configList,
            )
        }
    }
}