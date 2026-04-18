package com.example.passwordvault.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordvault.data.repository.PasswordRepository
import com.example.passwordvault.security.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 设置界面的ViewModel
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository,
    private val backupManager: BackupManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
        loadStatistics()
        loadBackupInfo()
    }
    
    /**
     * 加载设置
     */
    private fun loadSettings() {
        // 这里应该从SharedPreferences或DataStore加载用户设置
        // 暂时使用默认值
        
        _uiState.update {
            it.copy(
                autoLockTimeout = 5, // 5分钟
                isFingerprintEnabled = true,
                themeMode = "dark" // 暗黑主题
            )
        }
    }
    
    /**
     * 加载统计信息
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            // 加载密码数量
            val passwordCount = passwordRepository.getPasswordCount()
            
            // 加载分类数量（简化实现）
            val categoryCount = 7 // 默认分类数量
            
            _uiState.update {
                it.copy(
                    passwordCount = passwordCount,
                    categoryCount = categoryCount
                )
            }
        }
    }
    
    /**
     * 加载备份信息
     */
    private fun loadBackupInfo() {
        viewModelScope.launch {
            val backupFiles = backupManager.getBackupFiles()
            val lastBackupTime = backupFiles.maxByOrNull { it.modified }?.modified
            
            _uiState.update {
                it.copy(
                    backupCount = backupFiles.size,
                    lastBackupTime = lastBackupTime
                )
            }
        }
    }
    
    /**
     * 更新自动锁定超时
     */
    suspend fun updateAutoLockTimeout(timeout: Int) {
        // 这里应该保存到SharedPreferences或DataStore
        _uiState.update { it.copy(autoLockTimeout = timeout) }
    }
    
    /**
     * 切换指纹认证
     */
    suspend fun toggleFingerprint(enabled: Boolean) {
        // 这里应该保存到SharedPreferences或DataStore
        _uiState.update { it.copy(isFingerprintEnabled = enabled) }
    }
    
    /**
     * 更新主题
     */
    suspend fun updateTheme(themeMode: String) {
        // 这里应该保存到SharedPreferences或DataStore
        _uiState.update { it.copy(themeMode = themeMode) }
    }
    
    /**
     * 创建备份
     */
    suspend fun createBackup() {
        val result = backupManager.createBackup()
        
        when (result) {
            is BackupManager.BackupResult.Success -> {
                // 备份成功，更新UI状态
                loadBackupInfo()
                
                // 可以显示成功消息
                _uiState.update {
                    it.copy(
                        showMessage = true,
                        message = "备份成功：${result.entryCount}个密码，${result.categoryCount}个分类"
                    )
                }
            }
            is BackupManager.BackupResult.Failure -> {
                // 备份失败
                _uiState.update {
                    it.copy(
                        showMessage = true,
                        message = result.message
                    )
                }
            }
        }
    }
    
    /**
     * 清除所有数据
     */
    suspend fun clearAllData() {
        try {
            passwordRepository.clearAllData()
            
            _uiState.update {
                it.copy(
                    showMessage = true,
                    message = "数据已清除",
                    passwordCount = 0
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    showMessage = true,
                    message = "清除数据失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 导出数据
     */
    suspend fun exportData() {
        // 这里可以导出为CSV或其他格式
        // 暂时使用备份功能
        createBackup()
    }
    
    /**
     * 清理旧备份
     */
    suspend fun cleanupOldBackups() {
        val result = backupManager.cleanupOldBackups()
        
        when (result) {
            is BackupManager.CleanupResult.Success -> {
                loadBackupInfo()
                _uiState.update {
                    it.copy(
                        showMessage = true,
                        message = "清理了${result.deletedCount}个旧备份"
                    )
                }
            }
            is BackupManager.CleanupResult.Failure -> {
                _uiState.update {
                    it.copy(
                        showMessage = true,
                        message = result.message
                    )
                }
            }
        }
    }
    
    /**
     * 清除消息
     */
    fun clearMessage() {
        _uiState.update { it.copy(showMessage = false, message = null) }
    }
    
    /**
     * 检查是否已设置PIN码
     */
    fun hasPin(): Boolean {
        // 这里应该检查是否设置了PIN码
        // 暂时返回true
        return true
    }
    
    /**
     * 获取应用版本
     */
    fun getAppVersion(): String {
        // 这里应该从BuildConfig获取版本信息
        return "1.0.0 (1)"
    }
}

/**
 * 设置界面状态
 */
data class SettingsUiState(
    // 安全设置
    val autoLockTimeout: Int = 5,
    val isFingerprintEnabled: Boolean = true,
    val hasPin: Boolean = true,
    
    // 备份信息
    val lastBackupTime: Long? = null,
    val backupCount: Int = 0,
    
    // 外观设置
    val themeMode: String = "dark", // dark, light, system
    
    // 数据统计
    val passwordCount: Int = 0,
    val categoryCount: Int = 0,
    
    // 应用信息
    val appVersion: String = "1.0.0 (1)",
    
    // UI状态
    val showMessage: Boolean = false,
    val message: String? = null
)