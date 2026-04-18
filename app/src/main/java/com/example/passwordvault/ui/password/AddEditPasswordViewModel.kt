package com.example.passwordvault.ui.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordvault.data.model.Category
import com.example.passwordvault.data.model.PasswordEntry
import com.example.passwordvault.data.repository.PasswordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * 添加/编辑密码的ViewModel
 */
@HiltViewModel
class AddEditPasswordViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddEditPasswordUiState())
    val uiState: StateFlow<AddEditPasswordUiState> = _uiState.asStateFlow()
    
    private var originalPassword: PasswordEntry? = null
    
    init {
        loadCategories()
    }
    
    /**
     * 加载分类
     */
    private fun loadCategories() {
        viewModelScope.launch {
            passwordRepository.getVisibleCategories()
                .collect { categories ->
                    _uiState.update { it.copy(categories = categories) }
                    
                    // 如果没有选择分类，选择第一个
                    if (it.categoryId == null && categories.isNotEmpty()) {
                        _uiState.update { it.copy(categoryId = categories.first().id) }
                    }
                }
        }
    }
    
    /**
     * 加载密码条目
     */
    fun loadPassword(passwordId: Long) {
        viewModelScope.launch {
            val password = passwordRepository.getPasswordById(passwordId)
            originalPassword = password
            
            password?.let {
                _uiState.update { state ->
                    state.copy(
                        title = it.title,
                        username = it.username,
                        password = it.password,
                        categoryId = it.categoryId,
                        url = it.url ?: "",
                        notes = it.notes ?: "",
                        securityLevel = it.securityLevel,
                        isEditing = true
                    )
                }
            }
        }
    }
    
    /**
     * 更新标题
     */
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }
    
    /**
     * 更新账号
     */
    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null) }
    }
    
    /**
     * 更新密码
     */
    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }
    
    /**
     * 更新分类
     */
    fun updateCategory(categoryId: Long) {
        _uiState.update { it.copy(categoryId = categoryId) }
    }
    
    /**
     * 更新网址
     */
    fun updateUrl(url: String) {
        _uiState.update { it.copy(url = url) }
    }
    
    /**
     * 更新备注
     */
    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }
    
    /**
     * 更新安全级别
     */
    fun updateSecurityLevel(level: PasswordEntry.SecurityLevel) {
        _uiState.update { it.copy(securityLevel = level) }
    }
    
    /**
     * 生成随机密码
     */
    fun generatePassword() {
        val password = generateSecurePassword()
        _uiState.update { it.copy(password = password, passwordError = null) }
    }
    
    /**
     * 保存密码
     */
    fun savePassword() {
        viewModelScope.launch {
            // 验证输入
            val validationResult = validateInput()
            if (!validationResult.isValid) {
                _uiState.update { it.copy(
                    titleError = validationResult.titleError,
                    usernameError = validationResult.usernameError,
                    passwordError = validationResult.passwordError,
                    errorMessage = validationResult.errorMessage
                ) }
                return@launch
            }
            
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            
            try {
                val state = _uiState.value
                val passwordEntry = PasswordEntry(
                    id = originalPassword?.id ?: 0,
                    title = state.title.trim(),
                    username = state.username.trim(),
                    password = state.password,
                    categoryId = state.categoryId ?: 1, // 默认分类
                    url = state.url.takeIf { it.isNotBlank() },
                    notes = state.notes.takeIf { it.isNotBlank() },
                    securityLevel = state.securityLevel
                )
                
                if (state.isEditing) {
                    passwordRepository.updatePassword(passwordEntry)
                } else {
                    passwordRepository.addPassword(passwordEntry)
                }
                
                _uiState.update { it.copy(isSaving = false, isSaveComplete = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isSaving = false,
                    errorMessage = "保存失败: ${e.message}"
                ) }
            }
        }
    }
    
    /**
     * 验证输入
     */
    private fun validateInput(): ValidationResult {
        val state = _uiState.value
        var isValid = true
        val errors = mutableListOf<String>()
        
        // 验证标题
        if (state.title.isBlank()) {
            isValid = false
            errors.add("标题不能为空")
        }
        
        // 验证账号
        if (state.username.isBlank()) {
            isValid = false
            errors.add("账号不能为空")
        }
        
        // 验证密码
        if (state.password.isBlank()) {
            isValid = false
            errors.add("密码不能为空")
        }
        
        // 验证分类
        if (state.categoryId == null) {
            isValid = false
            errors.add("请选择分类")
        }
        
        return ValidationResult(
            isValid = isValid,
            titleError = if (state.title.isBlank()) "标题不能为空" else null,
            usernameError = if (state.username.isBlank()) "账号不能为空" else null,
            passwordError = if (state.password.isBlank()) "密码不能为空" else null,
            errorMessage = if (errors.isNotEmpty()) errors.joinToString("\n") else null
        )
    }
    
    /**
     * 生成安全密码
     */
    private fun generateSecurePassword(): String {
        val length = 16
        val chars = mutableListOf<Char>()
        
        // 添加各种字符类型
        chars.addAll(('a'..'z').toList()) // 小写字母
        chars.addAll(('A'..'Z').toList()) // 大写字母
        chars.addAll(('0'..'9').toList()) // 数字
        chars.addAll("!@#$%^&*()_+-=[]{}|;:,.<>?".toList()) // 特殊字符
        
        val random = Random()
        return (1..length)
            .map { chars[random.nextInt(chars.size)] }
            .joinToString("")
    }
    
    /**
     * 验证结果
     */
    private data class ValidationResult(
        val isValid: Boolean,
        val titleError: String? = null,
        val usernameError: String? = null,
        val passwordError: String? = null,
        val errorMessage: String? = null
    )
}

/**
 * 添加/编辑密码界面状态
 */
data class AddEditPasswordUiState(
    val title: String = "",
    val username: String = "",
    val password: String = "",
    val categoryId: Long? = null,
    val url: String = "",
    val notes: String = "",
    val securityLevel: PasswordEntry.SecurityLevel = PasswordEntry.SecurityLevel.NORMAL,
    
    val categories: List<Category> = emptyList(),
    
    val titleError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,
    
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isSaveComplete: Boolean = false
)