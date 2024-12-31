package com.lxy.baomidou.net

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/**
 * @Author liuxy
 * @Date 2024/12/26
 * @Desc
 */
class NonNullStringAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): String {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Unit>()
            return "" // 返回一个默认值
        }
        return reader.nextString()
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: String?) {
        writer.value(value)
    }
}
