package com.lxy.baomidou.entity

/**
 * @Author liuxy
 * @Date 2024/12/19
 * @Desc
 */
data class ShopConfig(
    val id: Int = -1,

    /**
     * 门店id
     */
    val shopId: String = "",
    /**
     * 区域名称
     */
    val areaName: String = "",
    /**
     * 门店名称
     */
    val shopName: String = "",
    /**
     * 手机号码
     */
    val phone: String? = "",
    /**
     * 备注
     */
    val remark: String? = "",
    /**
     * 推送的列表
     */
    val spts: String? = "",
    /**
     * 每天最多预约数量
     */
    val maxCountPerDay: Int = 2,
    /**
     * 预约规则
     */
    val type: Int? = 0,
    /**
     * 预约规则详情
     */
    val data: String? = "",
    /**
     * 是否已完成
     */
    val success: Boolean = false,
    /**
     * 删除标志
     */
    val deleted: Boolean = false,

    var isChoose: Boolean = false,

)
