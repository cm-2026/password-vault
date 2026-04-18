package com.example.passwordvault

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 密码保险箱应用类
 * 使用Hilt进行依赖注入
 */
@HiltAndroidApp
class PasswordVaultApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 应用初始化代码
        initializeApp()
    }
    
    /**
     * 应用初始化
     */
    private fun initializeApp() {
        // 可以在这里初始化第三方库、配置等
        // 例如：Firebase、Crashlytics、Analytics等
        
        // 设置异常处理器
        setupExceptionHandler()
        
        // 初始化安全组件
        initializeSecurity()
    }
    
    /**
     * 设置全局异常处理器
     */
    private fun setupExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // 记录异常日志（在实际应用中，可以发送到服务器或保存到本地）
            logException(throwable)
            
            // 调用默认处理器
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
    
    /**
     * 记录异常
     */
    private fun logException(throwable: Throwable) {
        // 在实际应用中，这里可以：
        // 1. 保存到本地文件
        // 2. 发送到服务器
        // 3. 使用Firebase Crashlytics等工具
        
        // 示例：打印到日志
        android.util.Log.e("PasswordVault", "Uncaught exception", throwable)
    }
    
    /**
     * 初始化安全组件
     */
    private fun initializeSecurity() {
        // 可以在这里进行一些安全相关的初始化
        // 例如：检查设备安全性、初始化加密库等
        
        // 检查设备是否root（在实际应用中需要更复杂的检查）
        if (isDeviceRooted()) {
            android.util.Log.w("PasswordVault", "Device may be rooted, security risk!")
        }
    }
    
    /**
     * 检查设备是否root（简单检查）
     */
    private fun isDeviceRooted(): Boolean {
        // 这只是简单的检查，实际应用中需要更复杂的方法
        val buildTags = android.os.Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }
    
    /**
     * 获取应用版本信息
     */
    fun getAppVersion(): String {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: Exception) {
            "Unknown"
        }
    }
    
    /**
     * 获取设备信息（用于调试）
     */
    fun getDeviceInfo(): String {
        return """
            Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}
            Android: ${android.os.Build.VERSION.RELEASE} (SDK ${android.os.Build.VERSION.SDK_INT})
            App: ${getAppVersion()}
        """.trimIndent()
    }
}