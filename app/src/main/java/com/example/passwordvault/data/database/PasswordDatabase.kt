package com.example.passwordvault.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.passwordvault.data.model.Category
import com.example.passwordvault.data.model.PasswordEntry

/**
 * 密码数据库
 * 使用Room数据库存储密码条目和分类
 */
@Database(
    entities = [PasswordEntry::class, Category::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PasswordDatabase : RoomDatabase() {
    
    abstract fun passwordDao(): PasswordDao
    abstract fun categoryDao(): CategoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: PasswordDatabase? = null
        
        /**
         * 获取数据库实例（单例模式）
         */
        fun getDatabase(context: Context): PasswordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PasswordDatabase::class.java,
                    "password_vault.db"
                )
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * 清空数据库实例（用于测试）
         */
        fun clearInstance() {
            INSTANCE = null
        }
    }
}