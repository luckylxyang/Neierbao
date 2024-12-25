package com.lxy.baomidou.data

import android.util.Log
import com.lxy.baomidou.entity.AppointHistory
import com.lxy.baomidou.entity.SearchEntity
import com.lxy.baomidou.entity.ShopConfig
import com.lxy.baomidou.net.NetworkUtils
import java.util.Collections

/**
 * @Author liuxy
 * @Date 2024/12/21
 * @Desc
 */
class AppRepo {



    suspend fun getShopConfigList(): List<ShopConfig> {
        return NetworkUtils.getAllShopConfig() ?: return Collections.emptyList()
    }

    suspend fun getAppointHistoryList(): List<AppointHistory> {
        val searchEntity = SearchEntity(null,null,null,null);
        return NetworkUtils.getAllApt(searchEntity) ?: return Collections.emptyList()
    }

    fun saveShopConfig(config: ShopConfig) {
        Log.d("TAG", "saveShopConfig: $config")
        //NetworkUtils.addShopConfig(config)
    }

    fun deleteShopConfig(config: ShopConfig){

    }

}