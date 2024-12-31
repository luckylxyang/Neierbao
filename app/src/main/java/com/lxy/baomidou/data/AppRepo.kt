package com.lxy.baomidou.data

import android.util.Log
import com.lxy.baomidou.entity.AppointHistory
import com.lxy.baomidou.entity.SearchEntity
import com.lxy.baomidou.entity.Shop
import com.lxy.baomidou.entity.ShopConfig
import com.lxy.baomidou.net.ApiResponse
import com.lxy.baomidou.net.NetworkUtils
import java.util.Collections

/**
 * @Author liuxy
 * @Date 2024/12/21
 * @Desc
 */
class AppRepo {


    suspend fun getAppointHistoryList(search: SearchEntity): List<AppointHistory> {
        return NetworkUtils.getAllApt(search) ?: return Collections.emptyList()
    }


    suspend fun updatePhone(history: AppointHistory){
        NetworkUtils.editPhone(history.id, history.phone)
    }

    suspend fun cancelApt(history: AppointHistory){
        NetworkUtils.cancelApt(history.id)
    }

    suspend fun getShopConfigList(): List<ShopConfig> {
        return NetworkUtils.getAllShopConfig()
    }


    suspend fun addShopConfig(config: ShopConfig) {
        Log.d("TAG", "saveShopConfig: $config")
        NetworkUtils.addShopConfig(config)
    }

    suspend fun updateShopConfig(config: ShopConfig) {
        Log.d("TAG", "updateShopConfig: $config")
        NetworkUtils.updateShopConfig(config)
    }

    suspend fun getShopList(): ApiResponse<List<Shop>> {
        return NetworkUtils.areaShop()
    }

}