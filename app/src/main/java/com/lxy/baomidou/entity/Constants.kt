package com.lxy.baomidou.entity

import com.lxy.baomidou.entity.AppointStatus.PENDING

/**
 * @Author liuxy
 * @Date 2024/12/27
 * @Desc
 */


enum class AppointStatus(val value: Int, val desc: String) {
    PENDING(1, "待使用"),
    CANCELED(2, "已取消"),
    USED(3, "已使用");

    companion object {
        fun fromValue(value: Int): AppointStatus {
            return AppointStatus.entries.find { it.value == value } ?: PENDING
        }
        fun fromDesc(desc: String): AppointStatus {
            return AppointStatus.entries.find { it.desc == desc } ?: PENDING
        }

        fun getStatusList(): List<AppointStatus> = AppointStatus.entries
    }
}


enum class AppointType(val value: Int, val desc: String) {
    DEFAULT(1, "默认"),
    OLD(2, "旧数据"),
    NEW(3, "新数据");

    companion object {
        fun fromValue(value: Int): AppointType {
            return AppointType.entries.find { it.value == value } ?: DEFAULT
        }
        fun fromDesc(desc: String): AppointType {
            return AppointType.entries.find { it.desc == desc } ?: DEFAULT
        }

        fun getStatusList(): List<AppointType> = AppointType.entries
    }
}