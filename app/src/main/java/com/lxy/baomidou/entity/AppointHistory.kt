package com.lxy.baomidou.entity

/**
 * @Author liuxy
 * @Date 2024/12/19
 * @Desc
 */
data class AppointHistory(

    val id: String,
    /**
     * 电话号码
     */
    val phone: String,

    /**
     * 门店id
     */
    val shopId: String,

    /**
     * 门店名称
     */
    val shopName: String,

    /**
     * 预约日期
     */
    val appointmentDate: String,

    /**
     * 门票id
     */
    val ticketId: String,

    /**
     * 门票名称
     */
    val ticketName: String,
    /**
     * 状态 1：待使用， 2:已取消，3:已使用
     */
    val status: Int,

    val lineType: Int,

    val isPunished: Boolean,

    val isDeleted: Int,

    /**
     * 创建时间
     */
    val createTime: String,
    /**
     * 1 默认，2 旧数据，3 新数据
     */
    val type: Int

){
    fun getStatusDesc(): String = when(status){
        1 -> "待使用"
        2 -> "已取消"
        3 -> "已使用"
        else -> ""
    }

    fun getTypeDesc(): String = when(type){
        1 -> "默认"
        2 -> "旧数据"
        3 -> "新数据"
        else -> ""
    }
}
