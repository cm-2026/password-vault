package com.example.passwordvault.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.passwordvault.data.model.Category

/**
 * 数据库回调，用于初始化和迁移
 */
class DatabaseCallback(
    private val context: Context
) : RoomDatabase.Callback() {
    
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        
        // 在后台线程执行初始化
        CoroutineScope(Dispatchers.IO).launch {
            // 初始化默认分类
            initializeDefaultCategories()
        }
    }
    
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        // 数据库打开时的操作
        // 可以在这里执行一些检查或优化
    }
    
    /**
     * 初始化默认分类
     */
    private suspend fun initializeDefaultCategories() {
        val database = PasswordDatabase.getDatabase(context)
        val categoryDao = database.categoryDao()
        
        // 检查是否已有分类
        val existingCategories = categoryDao.getAllCategories()
        
        if (existingCategories.isEmpty()) {
            // 插入默认分类
            val defaultCategories = Category.getDefaultCategories()
            categoryDao.insertAll(defaultCategories)
        }
    }
    
    /**
     * 数据库迁移逻辑（未来版本使用）
     */
    companion object {
        // 迁移规则可以在这里定义
        // 例如：从版本1迁移到版本2
    }
}