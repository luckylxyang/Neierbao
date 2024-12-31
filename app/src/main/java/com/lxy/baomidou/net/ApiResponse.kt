package com.lxy.baomidou.net

/**
 * @Author liuxy
 * @Date 2024/12/27
 * @Desc
 */
data class ApiResponse<T>(
    val code: Int = 0,
    val msg: String = "",
    val data: T,
    val success: Boolean = false
) {
    fun isSuccess() = code == 200
}
