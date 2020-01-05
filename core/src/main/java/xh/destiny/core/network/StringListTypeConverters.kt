package xh.destiny.core.network

import androidx.room.TypeConverter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 类型转换器
 * String列表类型转Json
 */

class StringListTypeConverters {
    @TypeConverter
    fun stringToList(data: String?): List<String> {
        if (data == null) {
            return emptyList()
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(configs: List<String>?): String {
        if (configs == null) {
            return ""
        } else {
            return Gson().toJson(configs)
        }
    }
}
