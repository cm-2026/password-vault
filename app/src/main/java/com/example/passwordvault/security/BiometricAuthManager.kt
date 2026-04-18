package com.example.passwordvault.security

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.KeyStore
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 生物识别认证管理器
 * 支持指纹、面部识别等生物识别方式
 * 特别适配小米13的屏下指纹
 */
class BiometricAuthManager(private val context: Context) {
    
    companion object {
        private const val KEY_NAME = "password_vault_biometric_key"
        private const val KEYSTORE_NAME = "AndroidKeyStore"
    }
    
    private val executor: Executor = ContextCompat.getMainExecutor(context)
    private val biometricManager = BiometricManager.from(context)
    
    /**
     * 检查设备是否支持生物识别
     */
    fun canAuthenticate(): BiometricStatus {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HARDWARE_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricStatus.SECURITY_UPDATE_REQUIRED
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> BiometricStatus.UNSUPPORTED
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> BiometricStatus.UNKNOWN
            else -> BiometricStatus.UNKNOWN
        }
    }
    
    /**
     * 检查是否支持指纹识别（特别适配小米13）
     */
    fun isFingerprintSupported(): Boolean {
        return if (Build.MANUFACTURER.equals("xiaomi", ignoreCase = true)) {
            // 小米设备特殊检查
            canAuthenticate() == BiometricStatus.AVAILABLE &&
            biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
        } else {
            canAuthenticate() == BiometricStatus.AVAILABLE
        }
    }
    
    /**
     * 显示指纹认证对话框
     */
    suspend fun authenticate(
        activity: FragmentActivity,
        title: String = "指纹验证",
        subtitle: String = "请验证指纹以继续",
        negativeButtonText: String = "取消"
    ): AuthResult = suspendCoroutine { continuation ->
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
        
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    val error = when (errorCode) {
                        BiometricPrompt.ERROR_CANCELED -> AuthError.CANCELED
                        BiometricPrompt.ERROR_USER_CANCELED -> AuthError.USER_CANCELED
                        BiometricPrompt.ERROR_TIMEOUT -> AuthError.TIMEOUT
                        BiometricPrompt.ERROR_LOCKOUT -> AuthError.LOCKOUT
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> AuthError.LOCKOUT_PERMANENT
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> AuthError.NO_BIOMETRICS
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> AuthError.HW_UNAVAILABLE
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> AuthError.NEGATIVE_BUTTON
                        else -> AuthError.UNKNOWN
                    }
                    continuation.resume(AuthResult.Failure(error, errString.toString()))
                }
                
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    continuation.resume(AuthResult.Success)
                }
                
                override fun onAuthenticationFailed() {
                    continuation.resume(AuthResult.Failure(AuthError.FAILED, "认证失败"))
                }
            }
        )
        
        biometricPrompt.authenticate(promptInfo)
    }
    
    /**
     * 使用加密的指纹认证（更安全）
     */
    suspend fun authenticateWithCrypto(
        activity: FragmentActivity,
        title: String = "指纹验证",
        subtitle: String = "请验证指纹以解密数据"
    ): CryptoAuthResult = suspendCoroutine { continuation ->
        try {
            val cipher = getCipher()
            val secretKey = getSecretKey()
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText("取消")
                .setConfirmationRequired(false) // 小米13屏下指纹可能需要设置为false
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build()
            
            val biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        val error = when (errorCode) {
                            BiometricPrompt.ERROR_CANCELED -> AuthError.CANCELED
                            BiometricPrompt.ERROR_USER_CANCELED -> AuthError.USER_CANCELED
                            else -> AuthError.UNKNOWN
                        }
                        continuation.resume(CryptoAuthResult.Failure(error, errString.toString()))
                    }
                    
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        val cryptoObject = result.cryptoObject
                        if (cryptoObject?.cipher != null) {
                            continuation.resume(CryptoAuthResult.Success(cryptoObject.cipher!!))
                        } else {
                            continuation.resume(CryptoAuthResult.Failure(AuthError.CRYPTO_FAILED, "加密对象为空"))
                        }
                    }
                    
                    override fun onAuthenticationFailed() {
                        continuation.resume(CryptoAuthResult.Failure(AuthError.FAILED, "认证失败"))
                    }
                }
            )
            
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        } catch (e: Exception) {
            continuation.resume(CryptoAuthResult.Failure(AuthError.INIT_FAILED, e.message ?: "初始化失败"))
        }
    }
    
    /**
     * 获取加密密钥
     */
    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_NAME)
        keyStore.load(null)
        
        return if (keyStore.containsAlias(KEY_NAME)) {
            (keyStore.getEntry(KEY_NAME, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            createSecretKey()
        }
    }
    
    /**
     * 创建加密密钥
     */
    private fun createSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_NAME
        )
        
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_NAME,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)
            .build()
        
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }
    
    /**
     * 获取加密器
     */
    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            "${KeyProperties.KEY_ALGORITHM_AES}/" +
            "${KeyProperties.BLOCK_MODE_CBC}/" +
            KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }
    
    /**
     * 清除所有生物识别数据
     */
    fun clearBiometricData() {
        try {
            val keyStore = KeyStore.getInstance(KEYSTORE_NAME)
            keyStore.load(null)
            if (keyStore.containsAlias(KEY_NAME)) {
                keyStore.deleteEntry(KEY_NAME)
            }
        } catch (e: Exception) {
            // 忽略错误
        }
    }
    
    /**
     * 生物识别状态枚举
     */
    enum class BiometricStatus {
        AVAILABLE,              // 可用
        NOT_ENROLLED,           // 未注册
        HARDWARE_UNAVAILABLE,   // 硬件不可用
        NO_HARDWARE,            // 无硬件
        SECURITY_UPDATE_REQUIRED, // 需要安全更新
        UNSUPPORTED,            // 不支持
        UNKNOWN                 // 未知
    }
    
    /**
     * 认证错误枚举
     */
    enum class AuthError {
        CANCELED,               // 取消
        USER_CANCELED,          // 用户取消
        TIMEOUT,                // 超时
        LOCKOUT,                // 锁定
        LOCKOUT_PERMANENT,      // 永久锁定
        NO_BIOMETRICS,          // 无生物识别
        HW_UNAVAILABLE,         // 硬件不可用
        NEGATIVE_BUTTON,        // 负面按钮
        FAILED,                 // 失败
        CRYPTO_FAILED,          // 加密失败
        INIT_FAILED,            // 初始化失败
        UNKNOWN                 // 未知
    }
    
    /**
     * 认证结果密封类
     */
    sealed class AuthResult {
        object Success : AuthResult()
        data class Failure(val error: AuthError, val message: String) : AuthResult()
    }
    
    /**
     * 加密认证结果密封类
     */
    sealed class CryptoAuthResult {
        data class Success(val cipher: Cipher) : CryptoAuthResult()
        data class Failure(val error: AuthError, val message: String) : CryptoAuthResult()
    }
}