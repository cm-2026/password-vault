package com.example.passwordvault.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordvault.security.BiometricAuthManager
import com.example.passwordvault.security.PinAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 登录界面的ViewModel
 * 管理认证状态和逻辑
 */
class LoginViewModel(
    private val biometricAuthManager: BiometricAuthManager,
    private val pinAuthManager: PinAuthManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    init {
        checkAuthMethods()
    }
    
    /**
     * 检查可用的认证方法
     */
    private fun checkAuthMethods() {
        val isFingerprintAvailable = biometricAuthManager.isFingerprintSupported()
        val hasPin = pinAuthManager.hasPin()
        
        _uiState.update {
            LoginUiState.Initial.copy(
                isFingerprintAvailable = isFingerprintAvailable,
                hasPin = hasPin
            )
        }
    }
    
    /**
     * 使用指纹认证
     */
    suspend fun authenticateWithFingerprint() {
        _uiState.update { LoginUiState.Authenticating }
        
        // 这里需要传入Activity，暂时模拟认证
        // 在实际应用中，需要从Composable获取Activity
        viewModelScope.launch {
            // 模拟认证过程
            kotlinx.coroutines.delay(1500)
            
            // 模拟认证成功
            _uiState.update { LoginUiState.Success }
        }
    }
    
    /**
     * 使用PIN码认证
     */
    suspend fun authenticateWithPin(pin: String) {
        _uiState.update { LoginUiState.Authenticating }
        
        viewModelScope.launch {
            // 实际调用PIN码认证
            val result = pinAuthManager.verifyPin(pin)
            
            when (result) {
                is PinAuthManager.AuthResult.Success -> {
                    _uiState.update { LoginUiState.Success }
                }
                is PinAuthManager.AuthResult.Failure -> {
                    _uiState.update {
                        LoginUiState.PinAuth(
                            errorMessage = result.message,
                            remainingAttempts = result.remainingAttempts
                        )
                    }
                }
                is PinAuthManager.AuthResult.Locked -> {
                    _uiState.update {
                        LoginUiState.PinAuth(
                            isLocked = true,
                            lockedSeconds = result.remainingSeconds
                        )
                    }
                }
                is PinAuthManager.AuthResult.Error -> {
                    _uiState.update {
                        LoginUiState.Error(result.message)
                    }
                }
            }
        }
    }
    
    /**
     * 设置PIN码
     */
    suspend fun setupPin(pin: String) {
        _uiState.update { LoginUiState.Authenticating }
        
        viewModelScope.launch {
            val success = pinAuthManager.setPin(pin)
            
            if (success) {
                // PIN码设置成功，自动认证
                authenticateWithPin(pin)
            } else {
                _uiState.update {
                    LoginUiState.Error("PIN码设置失败")
                }
            }
        }
    }
    
    /**
     * 切换到PIN码认证
     */
    fun switchToPinAuth() {
        if (pinAuthManager.hasPin()) {
            _uiState.update { LoginUiState.PinAuth() }
        } else {
            _uiState.update { LoginUiState.SetupPin }
        }
    }
    
    /**
     * 切换到指纹认证
     */
    fun switchToFingerprintAuth() {
        _uiState.update { LoginUiState.Initial }
    }
    
    /**
     * 取消PIN码设置
     */
    fun cancelPinSetup() {
        _uiState.update { LoginUiState.Initial }
    }
    
    /**
     * 重试认证
     */
    fun retry() {
        checkAuthMethods()
    }
    
    /**
     * 检查指纹是否可用
     */
    fun isFingerprintAvailable(): Boolean {
        return biometricAuthManager.isFingerprintSupported()
    }
}

/**
 * 登录界面状态
 */
sealed class LoginUiState {
    data object Initial : LoginUiState() {
        var isFingerprintAvailable: Boolean = true
        var hasPin: Boolean = false
    }
    
    data object Authenticating : LoginUiState()
    data object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
    
    data class PinAuth(
        val errorMessage: String? = null,
        val remainingAttempts: Int? = null,
        val isLocked: Boolean = false,
        val lockedSeconds: Int = 0
    ) : LoginUiState()
    
    data object SetupPin : LoginUiState()
}