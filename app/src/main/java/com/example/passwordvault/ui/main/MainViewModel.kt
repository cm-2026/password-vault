package com.example.passwordvault.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordvault.security.BiometricAuthManager
import com.example.passwordvault.security.PinAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主界面的ViewModel
 * 管理应用的整体状态和认证
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val biometricAuthManager: BiometricAuthManager,
    private val pinAuthManager: PinAuthManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Unauthenticated)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    private var lastActivityTime: Long = System.currentTimeMillis()
    private var autoLockTimeout: Long = 5 * 60 * 1000 // 5分钟默认超时
    
    init {
        // 检查之前的认证状态
        checkPreviousAuthState()
        
        // 启动活动监控
        startActivityMonitoring()
    }
    
    /**
     * 检查之前的认证状态
     */
    private fun checkPreviousAuthState() {
        // 在实际应用中，这里可以检查本地存储的认证状态
        // 例如：检查是否已经登录、是否被锁定等
        
        // 暂时设置为未认证状态
        _uiState.update { MainUiState.Unauthenticated }
    }
    
    /**
     * 启动活动监控（用于自动锁定）
     */
    private fun startActivityMonitoring() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000) // 每秒检查一次
                
                if (_uiState.value is MainUiState.Authenticated) {
                    val currentTime = System.currentTimeMillis()
                    val inactiveTime = currentTime - lastActivityTime
                    
                    if (inactiveTime >= autoLockTimeout) {
                        // 自动锁定
                        lockApp()
                    }
                }
            }
        }
    }
    
    /**
     * 登录成功
     */
    fun onLoginSuccess() {
        _uiState.update { MainUiState.Authenticated }
        updateActivityTime()
    }
    
    /**
     * 锁定应用
     */
    fun lockApp() {
        _uiState.update { MainUiState.Locked(System.currentTimeMillis()) }
    }
    
    /**
     * 解锁应用
     */
    fun unlock() {
        // 切换到未认证状态，让用户重新登录
        _uiState.update { MainUiState.Unauthenticated }
    }
    
    /**
     * 更新活动时间
     */
    fun updateActivityTime() {
        lastActivityTime = System.currentTimeMillis()
    }
    
    /**
     * 设置自动锁定超时时间
     */
    fun setAutoLockTimeout(minutes: Int) {
        autoLockTimeout = minutes * 60 * 1000L
    }
    
    /**
     * 获取自动锁定超时时间（分钟）
     */
    fun getAutoLockTimeout(): Int {
        return (autoLockTimeout / (60 * 1000)).toInt()
    }
    
    /**
     * 检查指纹是否可用
     */
    fun isFingerprintAvailable(): Boolean {
        return biometricAuthManager.isFingerprintSupported()
    }
    
    /**
     * 检查是否已设置PIN码
     */
    fun hasPin(): Boolean {
        return pinAuthManager.hasPin()
    }
    
    /**
     * 登出
     */
    fun logout() {
        // 清除认证状态
        _uiState.update { MainUiState.Unauthenticated }
        
        // 可以在这里清除其他用户数据
        // 例如：清除缓存、重置状态等
    }
    
    /**
     * 获取应用状态信息（用于调试）
     */
    fun getAppStatus(): String {
        return """
            认证状态: ${_uiState.value::class.simpleName}
            指纹可用: ${isFingerprintAvailable()}
            已设置PIN: ${hasPin()}
            自动锁定: ${getAutoLockTimeout()}分钟
            最后活动: ${System.currentTimeMillis() - lastActivityTime}ms前
        """.trimIndent()
    }
}