package com.example.passwordvault.data.database

import androidx.room.*
import com.example.passwordvault.data.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * 分类数据访问对象
 */
@Dao
interface CategoryDao {
    
    // 插入操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)
    
    // 更新操作
    @Update
    suspend fun update(category: Category)
    
    @Update
    suspend fun updateAll(categories: List<Category>)
    
    // 删除操作
    @Delete
    suspend fun delete(category: Category)
    
    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM categories WHERE isDefault = 0") // 只删除非默认分类
    suspend fun deleteCustomCategories()
    
    // 查询操作
    @Query("SELECT * FROM categories ORDER BY position ASC")
    fun getAllCategories(): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): Category?
    
    @Query("SELECT * FROM categories WHERE uuid = :uuid")
    suspend fun getCategoryByUuid(uuid: String): Category?
    
    @Query("SELECT * FROM categories WHERE name = :name")
    suspend fun getCategoryByName(name: String): Category?
    
    // 可见分类查询
    @Query("SELECT * FROM categories WHERE isHidden = 0 ORDER BY position ASC")
    fun getVisibleCategories(): Flow<List<Category>>
    
    // 默认分类查询
    @Query("SELECT * FROM categories WHERE isDefault = 1 ORDER BY position ASC")
    fun getDefaultCategories(): Flow<List<Category>>
    
    // 自定义分类查询
    @Query("SELECT * FROM categories WHERE isDefault = 0 ORDER BY position ASC")
    fun getCustomCategories(): Flow<List<Category>>
    
    // 统计查询
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
    
    @Query("SELECT COUNT(*) FROM categories WHERE isDefault = 1")
    suspend fun getDefaultCategoryCount(): Int
    
    // 位置管理
    @Query("UPDATE categories SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: Long, position: Int)
    
    @Query("SELECT MAX(position) FROM categories")
    suspend fun getMaxPosition(): Int?
    
    // 条目数量更新
    @Query("UPDATE categories SET itemCount = :count WHERE id = :id")
    suspend fun updateItemCount(id: Long, count: Int)
    
    // 批量操作
    @Transaction
    suspend fun updateCategoryWithCount(category: Category, count: Int): Category {
        val updated = category.updateItemCount(count)
        update(updated)
        return updated
    }
    
    @Transaction
    suspend fun reorderCategories(categories: List<Category>) {
        categories.forEachIndexed { index, category ->
            update(category.copy(position = index))
        }
    }
    
    // 备份相关
    @Query("SELECT * FROM categories ORDER BY position ASC")
    suspend fun getAllCategoriesForBackup(): List<Category>
    
    // 导入相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun importCategories(categories: List<Category>)
    
    // 检查分类是否被使用
    @Query("SELECT COUNT(*) FROM password_entries WHERE categoryId = :categoryId")
    suspend fun isCategoryUsed(categoryId: Long): Int
}