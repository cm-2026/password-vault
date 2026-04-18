package com.example.passwordvault.data.database

import androidx.room.*
import com.example.passwordvault.data.model.PasswordEntry
import kotlinx.coroutines.flow.Flow

/**
 * 密码条目数据访问对象
 */
@Dao
interface PasswordDao {
    
    // 插入操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(passwordEntry: PasswordEntry): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(passwordEntries: List<PasswordEntry>)
    
    // 更新操作
    @Update
    suspend fun update(passwordEntry: PasswordEntry)
    
    @Update
    suspend fun updateAll(passwordEntries: List<PasswordEntry>)
    
    // 删除操作
    @Delete
    suspend fun delete(passwordEntry: PasswordEntry)
    
    @Query("DELETE FROM password_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM password_entries")
    suspend fun deleteAll()
    
    // 查询操作
    @Query("SELECT * FROM password_entries ORDER BY updatedAt DESC")
    fun getAllPasswords(): Flow<List<PasswordEntry>>
    
    @Query("SELECT * FROM password_entries WHERE id = :id")
    suspend fun getPasswordById(id: Long): PasswordEntry?
    
    @Query("SELECT * FROM password_entries WHERE uuid = :uuid")
    suspend fun getPasswordByUuid(uuid: String): PasswordEntry?
    
    // 分类查询
    @Query("SELECT * FROM password_entries WHERE categoryId = :categoryId ORDER BY updatedAt DESC")
    fun getPasswordsByCategory(categoryId: Long): Flow<List<PasswordEntry>>
    
    @Query("SELECT * FROM password_entries WHERE categoryId = :categoryId AND isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoritePasswordsByCategory(categoryId: Long): Flow<List<PasswordEntry>>
    
    // 收藏查询
    @Query("SELECT * FROM password_entries WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoritePasswords(): Flow<List<PasswordEntry>>
    
    // 搜索查询
    @Query("SELECT * FROM password_entries WHERE title LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchPasswords(query: String): Flow<List<PasswordEntry>>
    
    // 最近使用查询
    @Query("SELECT * FROM password_entries WHERE lastUsed IS NOT NULL ORDER BY lastUsed DESC LIMIT :limit")
    fun getRecentlyUsedPasswords(limit: Int = 10): Flow<List<PasswordEntry>>
    
    // 统计查询
    @Query("SELECT COUNT(*) FROM password_entries")
    suspend fun getPasswordCount(): Int
    
    @Query("SELECT COUNT(*) FROM password_entries WHERE categoryId = :categoryId")
    suspend fun getPasswordCountByCategory(categoryId: Long): Int
    
    @Query("SELECT COUNT(*) FROM password_entries WHERE isFavorite = 1")
    suspend fun getFavoriteCount(): Int
    
    // 安全级别查询
    @Query("SELECT * FROM password_entries WHERE securityLevel = :securityLevel ORDER BY updatedAt DESC")
    fun getPasswordsBySecurityLevel(securityLevel: PasswordEntry.SecurityLevel): Flow<List<PasswordEntry>>
    
    // 批量操作
    @Transaction
    suspend fun updatePasswordUsage(id: Long) {
        val password = getPasswordById(id)
        password?.let {
            update(it.markAsUsed())
        }
    }
    
    @Transaction
    suspend fun toggleFavorite(id: Long): Boolean {
        val password = getPasswordById(id) ?: return false
        val updated = password.copy(isFavorite = !password.isFavorite, updatedAt = System.currentTimeMillis())
        update(updated)
        return updated.isFavorite
    }
    
    // 备份相关
    @Query("SELECT * FROM password_entries ORDER BY createdAt ASC")
    suspend fun getAllPasswordsForBackup(): List<PasswordEntry>
    
    // 导入相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun importPasswords(passwordEntries: List<PasswordEntry>)
}