package com.lxy.baomidou.net

import com.lxy.baomidou.entity.AppointHistory
import com.lxy.baomidou.entity.SearchEntity
import com.lxy.baomidou.entity.ShopConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit
import kotlin.getValue

/**
 * Retrofit 网络请求封装类
 */
object RetrofitClient {
    private const val BASE_URL = "http://1.95.9.139:8080" // 替换为实际的 BASE_URL
    private const val TIME_OUT = 10L

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Content-Type", "application/json")
                    // 在这里添加其他需要的 header
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * 创建 API 服务接口实例
     */
    fun <T> createService(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    /**
     * 网络请求响应基类
     */
    data class ApiResponse<T>(
        val code: Int = 0,
        val message: String = "",
        val data: T? = null
    ) {
        fun isSuccess() = code == 200
    }

    /**
     * 网络请求结果密封类
     */
    sealed class Result<out T> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val exception: Exception) : Result<Nothing>()
        object Loading : Result<Nothing>()
    }

    /**
     * 安全的网络请求方法
     */
    suspend fun <T> safeApiCall(call: suspend () -> ApiResponse<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccess()) {
                response.data?.let {
                    Result.Success(it)
                } ?: Result.Error(Exception("Response data is null"))
            } else {
                Result.Error(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * API 接口定义
 */
interface ApiService {
    // 在这里定义 API 接口方法
    // 使用 Moshi 注解

    /**
     * 获取所有配置
     */
    @GET("/api/shopConfig/all")
    suspend fun getAllShopConfig(): RetrofitClient.ApiResponse<List<ShopConfig>>
    /**
     * 添加配置
     */
    @POST("/api/shopConfig/add")
    suspend fun addShopConfig(@Body request: ShopConfig): RetrofitClient.ApiResponse<String>
    /**
     * 编辑配置
     */
    @POST("/api/shopConfig/update")
    suspend fun updateShopConfig(@Body request: ShopConfig): RetrofitClient.ApiResponse<String>
    /**
     * 删除配置
     */
    @DELETE("/api/shopConfig/{id}")
    suspend fun delShopConfig(@Path("id") id: Int): RetrofitClient.ApiResponse<String>

    /**
     * 获取预约记录
     */
    @POST("/api/apt/search")
    suspend fun getAllApt(@Body request: SearchEntity): RetrofitClient.ApiResponse<List<AppointHistory>>
    /**
     * 切换手机号
     */
    @POST("/api/apt/{id}/phone/{phone}")
    suspend fun editPhone(@Path("id") id: String, @Path("phone") phone:String): RetrofitClient.ApiResponse<String>
    /**
     * 取消
     */
    @POST("/api/apt/cancel/{id}")
    suspend fun cancelApt(@Path("id") id: String): RetrofitClient.ApiResponse<String>
}

/**
 * 网络请求工具类
 */
object NetworkUtils {
    private val apiService: ApiService by lazy {
        RetrofitClient.createService(ApiService::class.java)
    }

    /**
     * 获取门店配置
     */
    suspend fun getAllShopConfig(): List<ShopConfig>?{
        val result = apiService.getAllShopConfig()
        return result.data
    }
    /**
     * 添加配置
     */
    suspend fun addShopConfig(request: ShopConfig){
        val result = apiService.addShopConfig(request)
    }
    /**
     * 修改配置
     */
    suspend fun updateShopConfig(request: ShopConfig){
        val result = apiService.updateShopConfig(request)
    }
    /**
     * 删除配置
     */
    suspend fun delShopConfig(id: Int){
        val result = apiService.delShopConfig(id)
    }

    /**
     * 获取预约记录
     */
    suspend fun getAllApt(request: SearchEntity): List<AppointHistory>?{
        val result = apiService.getAllApt(request)
        return result.data
    }
    /**
     * 切换手机号
     */
    suspend fun editPhone(id: String, phone:String){
        val result = apiService.editPhone(id, phone)
    }
    /**
     * 取消
     */
    suspend fun cancelApt(id: String){
        val result = apiService.cancelApt(id)
    }
    // 在这里封装具体的网络请求方法
} 