package com.lxy.baomidou.net

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.getValue

/**
 * Retrofit 网络请求封装类
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.example.com/" // 替换为实际的 BASE_URL
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
    /*
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): RetrofitClient.ApiResponse<LoginResponse>

    @GET("api/user/info")
    suspend fun getUserInfo(@Query("userId") String): RetrofitClient.ApiResponse<UserInfo>
    */
}

/**
 * 网络请求工具类
 */
object NetworkUtils {
    private val apiService: ApiService by lazy {
        RetrofitClient.createService(ApiService::class.java)
    }

    // 在这里封装具体的网络请求方法
} 