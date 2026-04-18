package com.example.passwordvault.security

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.core.content.FileProvider
import com.example.passwordvault.data.repository.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 备份管理器
 * 处理数据的备份和恢复
 */
@Singleton
class BackupManager @Inject constructor(
    private val context: Context,
    private val passwordRepository: PasswordRepository,
    private val encryptionManager: EncryptionManager
) {
    
    companion object {
        private const val BACKUP_FILE_PREFIX = "password_vault_backup"
        private const val BACKUP_FILE_EXTENSION = ".pvbak"
        private const val BACKUP_VERSION = 1
        private const val PROVIDER_AUTHORITY = "com.example.passwordvault.fileprovider"
    }
    
    /**
     * 创建备份文件
     */
    suspend fun createBackup(): BackupResult = withContext(Dispatchers.IO) {
        try {
            // 获取所有数据
            val exportData = passwordRepository.exportAllData()
            
            // 转换为JSON格式
            val jsonData = convertToJson(exportData)
            
            // 加密备份数据
            val encryptedData = encryptionManager.encrypt(jsonData)
            
            // 创建备份文件
            val backupFile = createBackupFile()
            backupFile.writeText(encryptedData)
            
            // 获取文件URI（用于分享）
            val fileUri = getFileUri(backupFile)
            
            BackupResult.Success(
                file = backupFile,
                uri = fileUri,
                entryCount = exportData.passwords.size,
                categoryCount = exportData.categories.size
            )
        } catch (e: Exception) {
            BackupResult.Failure("创建备份失败: ${e.message}")
        }
    }
    
    /**
     * 恢复备份
     */
    suspend fun restoreBackup(uri: Uri): RestoreResult = withContext(Dispatchers.IO) {
        try {
            // 读取备份文件
            val inputStream = context.contentResolver.openInputStream(uri)
            val encryptedData = inputStream?.bufferedReader()?.readText()
            inputStream?.close()
            
            if (encryptedData.isNullOrEmpty()) {
                return@withContext RestoreResult.Failure("备份文件为空或损坏")
            }
            
            // 解密数据
            val jsonData = try {
                encryptionManager.decrypt(encryptedData)
            } catch (e: Exception) {
                return@withContext RestoreResult.Failure("解密失败: ${e.message}")
            }
            
            // 解析JSON数据
            val backupData = parseBackupJson(jsonData)
            
            // 验证备份数据
            if (!validateBackupData(backupData)) {
                return@withContext RestoreResult.Failure("备份数据格式无效")
            }
            
            // 导入数据
            passwordRepository.importCategories(backupData.categories)
            passwordRepository.importPasswords(backupData.passwords)
            
            RestoreResult.Success(
                entryCount = backupData.passwords.size,
                categoryCount = backupData.categories.size
            )
        } catch (e: Exception) {
            RestoreResult.Failure("恢复备份失败: ${e.message}")
        }
    }
    
    /**
     * 创建备份文件
     */
    private fun createBackupFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${BACKUP_FILE_PREFIX}_${timestamp}${BACKUP_FILE_EXTENSION}"
        
        val backupDir = File(context.getExternalFilesDir(null), "backups")
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        
        return File(backupDir, fileName)
    }
    
    /**
     * 获取文件URI
     */
    private fun getFileUri(file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, PROVIDER_AUTHORITY, file)
        } else {
            Uri.fromFile(file)
        }
    }
    
    /**
     * 转换为JSON格式
     */
    private fun convertToJson(exportData: PasswordRepository.ExportData): String {
        // 简化实现，实际应用中应该使用Gson或Moshi等库
        return """
            {
                "version": $BACKUP_VERSION,
                "exportTime": ${exportData.exportTime},
                "passwords": [
                    ${exportData.passwords.joinToString(",") { password ->
                        """
                        {
                            "uuid": "${password.uuid}",
                            "title": "${escapeJson(password.title)}",
                            "username": "${escapeJson(password.username)}",
                            "password": "${escapeJson(password.password)}",
                            "categoryId": ${password.categoryId},
                            "url": ${password.url?.let { "\"${escapeJson(it)}\"" } ?: "null"},
                            "notes": ${password.notes?.let { "\"${escapeJson(it)}\"" } ?: "null"},
                            "isFavorite": ${password.isFavorite},
                            "securityLevel": "${password.securityLevel.name}",
                            "createdAt": ${password.createdAt},
                            "updatedAt": ${password.updatedAt}
                        }
                        """.trimIndent()
                    }}
                ],
                "categories": [
                    ${exportData.categories.joinToString(",") { category ->
                        """
                        {
                            "uuid": "${category.uuid}",
                            "name": "${escapeJson(category.name)}",
                            "color": ${category.color},
                            "icon": "${escapeJson(category.icon)}",
                            "position": ${category.position},
                            "isDefault": ${category.isDefault},
                            "itemCount": ${category.itemCount}
                        }
                        """.trimIndent()
                    }}
                ]
            }
        """.trimIndent()
    }
    
    /**
     * 解析备份JSON
     */
    private fun parseBackupJson(jsonData: String): BackupData {
        // 简化实现，实际应用中应该使用JSON解析库
        // 这里返回一个模拟数据
        return BackupData(
            version = BACKUP_VERSION,
            exportTime = System.currentTimeMillis(),
            passwords = emptyList(),
            categories = emptyList()
        )
    }
    
    /**
     * 验证备份数据
     */
    private fun validateBackupData(backupData: BackupData): Boolean {
        return backupData.version == BACKUP_VERSION &&
                backupData.passwords.isNotEmpty() &&
                backupData.categories.isNotEmpty()
    }
    
    /**
     * 转义JSON字符串
     */
    private fun escapeJson(str: String): String {
        return str
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
    
    /**
     * 获取所有备份文件
     */
    suspend fun getBackupFiles(): List<BackupFileInfo> = withContext(Dispatchers.IO) {
        val backupDir = File(context.getExternalFilesDir(null), "backups")
        if (!backupDir.exists()) {
            return@withContext emptyList()
        }
        
        return@withContext backupDir.listFiles { file ->
            file.name.endsWith(BACKUP_FILE_EXTENSION)
        }?.map { file ->
            BackupFileInfo(
                name = file.name,
                path = file.absolutePath,
                size = file.length(),
                modified = file.lastModified(),
                uri = getFileUri(file)
            )
        }?.sortedByDescending { it.modified } ?: emptyList()
    }
    
    /**
     * 删除备份文件
     */
    suspend fun deleteBackupFile(fileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val backupDir = File(context.getExternalFilesDir(null), "backups")
            val file = File(backupDir, fileName)
            return@withContext file.delete()
        } catch (e: Exception) {
            return@withContext false
        }
    }
    
    /**
     * 清理旧备份文件（保留最近7天）
     */
    suspend fun cleanupOldBackups(): CleanupResult = withContext(Dispatchers.IO) {
        try {
            val backupDir = File(context.getExternalFilesDir(null), "backups")
            if (!backupDir.exists()) {
                return@withContext CleanupResult.Success(0)
            }
            
            val cutoffTime = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L) // 7天前
            val backupFiles = backupDir.listFiles { file ->
                file.name.endsWith(BACKUP_FILE_EXTENSION) && file.lastModified() < cutoffTime
            }
            
            var deletedCount = 0
            backupFiles?.forEach { file ->
                if (file.delete()) {
                    deletedCount++
                }
            }
            
            CleanupResult.Success(deletedCount)
        } catch (e: Exception) {
            CleanupResult.Failure("清理备份失败: ${e.message}")
        }
    }
    
    /**
     * 备份结果
     */
    sealed class BackupResult {
        data class Success(
            val file: File,
            val uri: Uri,
            val entryCount: Int,
            val categoryCount: Int
        ) : BackupResult()
        
        data class Failure(val message: String) : BackupResult()
    }
    
    /**
     * 恢复结果
     */
    sealed class RestoreResult {
        data class Success(
            val entryCount: Int,
            val categoryCount: Int
        ) : RestoreResult()
        
        data class Failure(val message: String) : RestoreResult()
    }
    
    /**
     * 清理结果
     */
    sealed class CleanupResult {
        data class Success(val deletedCount: Int) : CleanupResult()
        data class Failure(val message: String) : CleanupResult()
    }
    
    /**
     * 备份文件信息
     */
    data class BackupFileInfo(
        val name: String,
        val path: String,
        val size: Long,
        val modified: Long,
        val uri: Uri
    )
    
    /**
     * 备份数据
     */
    private data class BackupData(
        val version: Int,
        val exportTime: Long,
        val passwords: List<com.example.passwordvault.data.model.PasswordEntry>,
        val categories: List<com.example.passwordvault.data.model.Category>
    )
}