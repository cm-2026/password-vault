package com.example.passwordvault.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * PIN码认证管理器
 * 作为指纹认证的备用方案
 */
class PinAuthManager(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "pin_auth_prefs"
        private const val PIN_KEY = "encrypted_pin"
        private const val PIN_ATTEMPTS_KEY = "pin_attempts"
        private const val PIN_LOCKOUT_TIME_KEY = "pin_lockout_time"
        private const val MAX_ATTEMPTS = 5
        private const val LOCKOUT_DURATION = 30 * 1000L // 30秒锁定
        private const val PIN_LENGTH = 6
    }
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    /**
     * 检查是否已设置PIN码
     */
    fun hasPin(): Boolean {
        return encryptedPrefs.contains(PIN_KEY)
    }
    
    /**
     * 设置PIN码
     */
    suspend fun setPin(pin: String): Boolean = withContext(Dispatchers.IO) {
        if (pin.length != PIN_LENGTH || !pin.all { it.isDigit() }) {
            return@withContext false
        }
        
        try {
            // 加密并存储PIN码
            encryptedPrefs.edit()
                .putString(PIN_KEY, pin)
                .putInt(PIN_ATTEMPTS_KEY, 0)
                .putLong(PIN_LOCKOUT_TIME_KEY, 0)
                .apply()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 验证PIN码
     */
    suspend fun verifyPin(pin: String): AuthResult = withContext(Dispatchers.IO) {
        // 检查锁定状态
        val lockoutTime = encryptedPrefs.getLong(PIN_LOCKOUT_TIME_KEY, 0)
        val currentTime = System.currentTimeMillis()
        
        if (lockoutTime > currentTime) {
            val remainingTime = (lockoutTime - currentTime) / 1000
            return@withContext AuthResult.Locked(remainingTime.toInt())
        }
        
        // 获取存储的PIN码
        val storedPin = encryptedPrefs.getString(PIN_KEY, null)
        if (storedPin == null) {
            return@withContext AuthResult.Error("未设置PIN码")
        }
        
        // 验证PIN码
        if (storedPin == pin) {
            // 验证成功，重置尝试次数
            encryptedPrefs.edit()
                .putInt(PIN_ATTEMPTS_KEY, 0)
                .putLong(PIN_LOCKOUT_TIME_KEY, 0)
                .apply()
            
            return@withContext AuthResult.Success
        } else {
            // 验证失败，增加尝试次数
            val attempts = encryptedPrefs.getInt(PIN_ATTEMPTS_KEY, 0) + 1
            encryptedPrefs.edit().putInt(PIN_ATTEMPTS_KEY, attempts).apply()
            
            if (attempts >= MAX_ATTEMPTS) {
                // 达到最大尝试次数，锁定
                val lockoutUntil = currentTime + LOCKOUT_DURATION
                encryptedPrefs.edit()
                    .putLong(PIN_LOCKOUT_TIME_KEY, lockoutUntil)
                    .putInt(PIN_ATTEMPTS_KEY, 0)
                    .apply()
                
                val remainingTime = LOCKOUT_DURATION / 1000
                return@withContext AuthResult.Locked(remainingTime.toInt())
            }
            
            val remainingAttempts = MAX_ATTEMPTS - attempts
            return@withContext AuthResult.Failure(remainingAttempts, "PIN码错误")
        }
    }
    
    /**
     * 清除PIN码
     */
    fun clearPin() {
        encryptedPrefs.edit()
            .remove(PIN_KEY)
            .remove(PIN_ATTEMPTS_KEY)
            .remove(PIN_LOCKOUT_TIME_KEY)
            .apply()
    }
    
    /**
     * 获取剩余尝试次数
     */
    fun getRemainingAttempts(): Int {
        val attempts = encryptedPrefs.getInt(PIN_ATTEMPTS_KEY, 0)
        return MAX_ATTEMPTS - attempts
    }
    
    /**
     * 检查是否被锁定
     */
    fun isLocked(): Boolean {
        val lockoutTime = encryptedPrefs.getLong(PIN_LOCKOUT_TIME_KEY, 0)
        return lockoutTime > System.currentTimeMillis()
    }
    
    /**
     * 获取锁定剩余时间（秒）
     */
    fun getLockoutRemainingTime(): Int {
        val lockoutTime = encryptedPrefs.getLong(PIN_LOCKOUT_TIME_KEY, 0)
        val currentTime = System.currentTimeMillis()
        
        return if (lockoutTime > currentTime) {
            ((lockoutTime - currentTime) / 1000).toInt()
        } else {
            0
        }
    }
    
    /**
     * 重置锁定状态（用于测试或紧急情况）
     */
    fun resetLockout() {
        encryptedPrefs.edit()
            .putInt(PIN_ATTEMPTS_KEY, 0)
            .putLong(PIN_LOCKOUT_TIME_KEY, 0)
            .apply()
    }
    
    /**
     * 更改PIN码
     */
    suspend fun changePin(oldPin: String, newPin: String): Boolean = withContext(Dispatchers.IO) {
        val verifyResult = verifyPin(oldPin)
        if (verifyResult !is AuthResult.Success) {
            return@withContext false
        }
        
        return@withContext setPin(newPin)
    }
    
    /**
     * 认证结果密封类
     */
    sealed class AuthResult {
        object Success : AuthResult()
        data class Failure(val remainingAttempts: Int, val message: String) : AuthResult()
        data class Locked(val remainingSeconds: Int) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}