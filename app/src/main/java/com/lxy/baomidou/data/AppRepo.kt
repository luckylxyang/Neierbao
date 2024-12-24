package com.lxy.baomidou.data

import android.util.Log
import com.lxy.baomidou.entity.AppointHistory
import com.lxy.baomidou.entity.ShopConfig

/**
 * @Author liuxy
 * @Date 2024/12/21
 * @Desc
 */
class AppRepo {

    fun getShopConfigList(): List<ShopConfig> {
        val list = listOf(
            ShopConfig(
                id = 53,
                shopName = "光环购物公园店",
                areaName = "成都市武侯区光环购物公园 3F",
                maxCountPerDay = 3,
                remark = "免预约单要更新一张，任意门"
            ),
            ShopConfig(
                id = 47,
                shopName = "时代天街店",
                areaName = "重庆市渝中区时代天街 2F",
                maxCountPerDay = 2,
                remark = "12.26,12.27 闭店：铺富妈咪要一张"
            ),
            ShopConfig(
                id = 46,
                shopName = "光环购物公园店",
                areaName = "重庆市渝北区光环购物公园 2F",
                maxCountPerDay = 2,
                remark = "12.26,12.27 闭店：铺富妈咪要一张"
            )
        )
        return list
    }

    fun getAppointHistoryList(): List<AppointHistory> {
        return listOf(
            AppointHistory(
                id = "1",
                phone = "186****5138",
                shopId = "53",
                shopName = "成都环球中心店",
                appointmentDate = "2024-12-19",
                ticketId = "1",
                ticketName = "默认票",
                status = 1,
                lineType = 1,
                isPunished = false,
                isDeleted = 0,
                createTime = "2024-12-19 10:00:00",
                type = 1
            ),
            AppointHistory(
                id = "2",
                phone = "186****0007",
                shopId = "47",
                shopName = "成都环球中心店",
                appointmentDate = "2024-12-20",
                ticketId = "1",
                ticketName = "默认票",
                status = 1,
                lineType = 1,
                isPunished = false,
                isDeleted = 0,
                createTime = "2024-12-19 11:00:00",
                type = 1
            ),
            AppointHistory(
                id = "3",
                phone = "186****2262",
                shopId = "46",
                shopName = "成都环球中心店",
                appointmentDate = "2024-12-21",
                ticketId = "1",
                ticketName = "默认票",
                status = 1,
                lineType = 1,
                isPunished = false,
                isDeleted = 0,
                createTime = "2024-12-19 12:00:00",
                type = 1
            )
        )
    }

    fun saveShopConfig(config: ShopConfig) {
        Log.d("TAG", "saveShopConfig: $config")
    }

    fun deleteShopConfig(config: ShopConfig){

    }

}