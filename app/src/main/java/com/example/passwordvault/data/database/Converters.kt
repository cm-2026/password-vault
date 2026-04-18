package com.example.passwordvault.data.database

import androidx.room.TypeConverter
import com.example.passwordvault.data.model.PasswordEntry

/**
 * Room数据库类型转换器
 */
class Converters {
    
    /**
     * SecurityLevel枚举转换
     */
    @TypeConverter
    fun fromSecurityLevel(level: PasswordEntry.SecurityLevel): String {
        return level.name
    }
    
    @TypeConverter
    fun toSecurityLevel(value: String): PasswordEntry.SecurityLevel {
        return PasswordEntry.SecurityLevel.valueOf(value)
    }
    
    /**
     * 可空字符串转换
     */
    @TypeConverter
    fun fromString(value: String?): String? {
        return value
    }
    
    @TypeConverter
    fun toString(value: String?): String? {
        return value
    }
    
    /**
     * 可空Long转换
     */
    @TypeConverter
    fun fromLong(value: Long?): Long? {
        return value
    }
    
    @TypeConverter
    fun toLong(value: Long?): Long? {
        return value
    }
    
    /**
     * Boolean转换（用于兼容性）
     */
    @TypeConverter
    fun fromBoolean(value: Boolean): Int {
        return if (value) 1 else 0
    }
    
    @TypeConverter
    fun toBoolean(value: Int): Boolean {
        return value == 1
    }
}