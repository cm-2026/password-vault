package com.example.passwordvault.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * 加密管理器
 * 使用Android Keystore进行安全加密
 */
class EncryptionManager(private val context: Context) {
    
    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "password_vault_master_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12 // GCM推荐使用12字节IV
        private const val TAG_LENGTH = 128 // GCM认证标签长度
        
        // 备份密钥别名（用于数据备份）
        private const val BACKUP_KEY_ALIAS = "password_vault_backup_key"
    }
    
    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
    
    init {
        keyStore.load(null)
        ensureMasterKeyExists()
    }
    
    /**
     * 确保主密钥存在
     */
    private fun ensureMasterKeyExists() {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            createMasterKey()
        }
    }
    
    /**
     * 创建主密钥
     */
    private fun createMasterKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )
        
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false) // 不需要每次验证
            .setInvalidatedByBiometricEnrollment(false)
            .setIsStrongBoxBacked(false) // 如果设备支持StrongBox，可以设置为true
            .build()
        
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }
    
    /**
     * 获取主密钥
     */
    private fun getMasterKey(): SecretKey {
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }
    
    /**
     * 加密数据
     */
    fun encrypt(data: String): String {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = getMasterKey()
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            
            // 组合IV和加密数据：IV + 加密数据
            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
            
            return Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            throw EncryptionException("加密失败", e)
        }
    }
    
    /**
     * 解密数据
     */
    fun decrypt(encryptedData: String): String {
        try {
            val combined = Base64.decode(encryptedData, Base64.NO_WRAP)
            
            // 分离IV和加密数据
            val iv = ByteArray(IV_SIZE)
            val encryptedBytes = ByteArray(combined.size - IV_SIZE)
            
            System.arraycopy(combined, 0, iv, 0, IV_SIZE)
            System.arraycopy(combined, IV_SIZE, encryptedBytes, 0, encryptedBytes.size)
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = getMasterKey()
            
            val spec = GCMParameterSpec(TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            throw EncryptionException("解密失败", e)
        }
    }
    
    /**
     * 加密字节数组
     */
    fun encryptBytes(data: ByteArray): ByteArray {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = getMasterKey()
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(data)
            
            // 组合IV和加密数据
            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
            
            return combined
        } catch (e: Exception) {
            throw EncryptionException("字节数组加密失败", e)
        }
    }
    
    /**
     * 解密字节数组
     */
    fun decryptBytes(encryptedData: ByteArray): ByteArray {
        try {
            // 分离IV和加密数据
            val iv = ByteArray(IV_SIZE)
            val encryptedBytes = ByteArray(encryptedData.size - IV_SIZE)
            
            System.arraycopy(encryptedData, 0, iv, 0, IV_SIZE)
            System.arraycopy(encryptedData, IV_SIZE, encryptedBytes, 0, encryptedBytes.size)
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = getMasterKey()
            
            val spec = GCMParameterSpec(TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            return cipher.doFinal(encryptedBytes)
        } catch (e: Exception) {
            throw EncryptionException("字节数组解密失败", e)
        }
    }
    
    /**
     * 创建备份密钥
     */
    fun createBackupKey(): Boolean {
        try {
            if (keyStore.containsAlias(BACKUP_KEY_ALIAS)) {
                return true // 已存在
            }
            
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )
            
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                BACKUP_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(true) // 备份需要验证
                .setUserAuthenticationValidityDurationSeconds(300) // 5分钟内有效
                .setInvalidatedByBiometricEnrollment(true)
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
            
            return true
        } catch (e: Exception) {
            return false
        }
    }
    
    /**
     * 清除所有密钥（危险操作）
     */
    fun clearAllKeys() {
        try {
            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS)
            }
            if (keyStore.containsAlias(BACKUP_KEY_ALIAS)) {
                keyStore.deleteEntry(BACKUP_KEY_ALIAS)
            }
        } catch (e: Exception) {
            // 忽略错误
        }
    }
    
    /**
     * 检查密钥是否可用
     */
    fun isKeyAvailable(): Boolean {
        return try {
            keyStore.containsAlias(KEY_ALIAS) && getMasterKey() != null
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 加密异常类
     */
    class EncryptionException(message: String, cause: Throwable? = null) : 
        Exception(message, cause)
}