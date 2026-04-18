package com.example.passwordvault.data.repository

import com.example.passwordvault.data.database.PasswordDao
import com.example.passwordvault.data.database.CategoryDao
import com.example.passwordvault.data.model.PasswordEntry
import com.example.passwordvault.data.model.Category
import com.example.passwordvault.security.EncryptionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 密码数据仓库
 * 处理密码条目的所有业务逻辑，包括加密解密
 */
@Singleton
class PasswordRepository @Inject constructor(
    private val passwordDao: PasswordDao,
    private val categoryDao: CategoryDao,
    private val encryptionManager: EncryptionManager
) {
    
    /**
     * 获取所有密码条目（已解密）
     */
    fun getAllPasswords(): Flow<List<PasswordEntry>> {
        return passwordDao.getAllPasswords().map { encryptedList ->
            encryptedList.map { decryptPassword(it) }
        }
    }
    
    /**
     * 根据分类获取密码条目
     */
    fun getPasswordsByCategory(categoryId: Long): Flow<List<PasswordEntry>> {
        return passwordDao.getPasswordsByCategory(categoryId).map { encryptedList ->
            encryptedList.map { decryptPassword(it) }
        }
    }
    
    /**
     * 获取收藏的密码条目
     */
    fun getFavoritePasswords(): Flow<List<PasswordEntry>> {
        return passwordDao.getFavoritePasswords().map { encryptedList ->
            encryptedList.map { decryptPassword(it) }
        }
    }
    
    /**
     * 搜索密码条目
     */
    fun searchPasswords(query: String): Flow<List<PasswordEntry>> {
        return passwordDao.searchPasswords(query).map { encryptedList ->
            encryptedList.map { decryptPassword(it) }
        }
    }
    
    /**
     * 获取最近使用的密码条目
     */
    fun getRecentlyUsedPasswords(limit: Int = 10): Flow<List<PasswordEntry>> {
        return passwordDao.getRecentlyUsedPasswords(limit).map { encryptedList ->
            encryptedList.map { decryptPassword(it) }
        }
    }
    
    /**
     * 根据ID获取密码条目
     */
    suspend fun getPasswordById(id: Long): PasswordEntry? {
        val encrypted = passwordDao.getPasswordById(id)
        return encrypted?.let { decryptPassword(it) }
    }
    
    /**
     * 添加密码条目
     */
    suspend fun addPassword(password: PasswordEntry): Long {
        val encrypted = encryptPassword(password)
        return passwordDao.insert(encrypted)
    }
    
    /**
     * 更新密码条目
     */
    suspend fun updatePassword(password: PasswordEntry) {
        val encrypted = encryptPassword(password)
        passwordDao.update(encrypted)
    }
    
    /**
     * 删除密码条目
     */
    suspend fun deletePassword(password: PasswordEntry) {
        passwordDao.delete(encryptPassword(password))
    }
    
    /**
     * 根据ID删除密码条目
     */
    suspend fun deletePasswordById(id: Long) {
        passwordDao.deleteById(id)
    }
    
    /**
     * 标记密码为已使用
     */
    suspend fun markPasswordAsUsed(id: Long) {
        passwordDao.updatePasswordUsage(id)
    }
    
    /**
     * 切换收藏状态
     */
    suspend fun toggleFavorite(id: Long): Boolean {
        return passwordDao.toggleFavorite(id)
    }
    
    /**
     * 获取密码数量
     */
    suspend fun getPasswordCount(): Int {
        return passwordDao.getPasswordCount()
    }
    
    /**
     * 获取收藏数量
     */
    suspend fun getFavoriteCount(): Int {
        return passwordDao.getFavoriteCount()
    }
    
    /**
     * 获取所有分类
     */
    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }
    
    /**
     * 获取可见分类
     */
    fun getVisibleCategories(): Flow<List<Category>> {
        return categoryDao.getVisibleCategories()
    }
    
    /**
     * 根据ID获取分类
     */
    suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)
    }
    
    /**
     * 添加分类
     */
    suspend fun addCategory(category: Category): Long {
        return categoryDao.insert(category)
    }
    
    /**
     * 更新分类
     */
    suspend fun updateCategory(category: Category) {
        categoryDao.update(category)
    }
    
    /**
     * 删除分类
     */
    suspend fun deleteCategory(category: Category) {
        // 检查分类是否被使用
        val usageCount = categoryDao.isCategoryUsed(category.id)
        if (usageCount > 0) {
            throw CategoryInUseException("分类正在被使用，无法删除")
        }
        categoryDao.delete(category)
    }
    
    /**
     * 重新排序分类
     */
    suspend fun reorderCategories(categories: List<Category>) {
        categoryDao.reorderCategories(categories)
    }
    
    /**
     * 获取分类及其密码数量
     */
    fun getCategoriesWithCount(): Flow<List<Pair<Category, Int>>> {
        return combine(
            categoryDao.getVisibleCategories(),
            passwordDao.getAllPasswords()
        ) { categories, passwords ->
            categories.map { category ->
                val count = passwords.count { it.categoryId == category.id }
                category to count
            }
        }
    }
    
    /**
     * 加密密码条目
     */
    private fun encryptPassword(password: PasswordEntry): PasswordEntry {
        return password.copy(
            password = encryptionManager.encrypt(password.password),
            notes = password.notes?.let { encryptionManager.encrypt(it) }
        )
    }
    
    /**
     * 解密密码条目
     */
    private fun decryptPassword(encrypted: PasswordEntry): PasswordEntry {
        return encrypted.copy(
            password = encryptionManager.decrypt(encrypted.password),
            notes = encrypted.notes?.let { encryptionManager.decrypt(it) }
        )
    }
    
    /**
     * 批量导入密码条目
     */
    suspend fun importPasswords(passwords: List<PasswordEntry>) {
        val encrypted = passwords.map { encryptPassword(it) }
        passwordDao.importPasswords(encrypted)
    }
    
    /**
     * 批量导入分类
     */
    suspend fun importCategories(categories: List<Category>) {
        categoryDao.importCategories(categories)
    }
    
    /**
     * 导出所有数据（用于备份）
     */
    suspend fun exportAllData(): ExportData {
        val passwords = passwordDao.getAllPasswordsForBackup()
        val categories = categoryDao.getAllCategoriesForBackup()
        
        return ExportData(
            passwords = passwords,
            categories = categories,
            exportTime = System.currentTimeMillis()
        )
    }
    
    /**
     * 清除所有数据
     */
    suspend fun clearAllData() {
        passwordDao.deleteAll()
        categoryDao.deleteCustomCategories()
    }
    
    /**
     * 导出数据类
     */
    data class ExportData(
        val passwords: List<PasswordEntry>,
        val categories: List<Category>,
        val exportTime: Long
    )
    
    /**
     * 分类正在使用异常
     */
    class CategoryInUseException(message: String) : Exception(message)
}