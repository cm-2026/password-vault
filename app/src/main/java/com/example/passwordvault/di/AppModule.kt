package com.example.passwordvault.di

import android.content.Context
import com.example.passwordvault.data.database.PasswordDatabase
import com.example.passwordvault.data.repository.PasswordRepository
import com.example.passwordvault.security.BiometricAuthManager
import com.example.passwordvault.security.EncryptionManager
import com.example.passwordvault.security.PinAuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 应用依赖注入模块
 * 使用Hilt进行依赖注入
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * 提供应用上下文
     */
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }
    
    /**
     * 提供数据库实例
     */
    @Provides
    @Singleton
    fun providePasswordDatabase(@ApplicationContext context: Context): PasswordDatabase {
        return PasswordDatabase.getDatabase(context)
    }
    
    /**
     * 提供密码DAO
     */
    @Provides
    @Singleton
    fun providePasswordDao(database: PasswordDatabase) = database.passwordDao()
    
    /**
     * 提供分类DAO
     */
    @Provides
    @Singleton
    fun provideCategoryDao(database: PasswordDatabase) = database.categoryDao()
    
    /**
     * 提供加密管理器
     */
    @Provides
    @Singleton
    fun provideEncryptionManager(@ApplicationContext context: Context): EncryptionManager {
        return EncryptionManager(context)
    }
    
    /**
     * 提供生物识别认证管理器
     */
    @Provides
    @Singleton
    fun provideBiometricAuthManager(@ApplicationContext context: Context): BiometricAuthManager {
        return BiometricAuthManager(context)
    }
    
    /**
     * 提供PIN码认证管理器
     */
    @Provides
    @Singleton
    fun providePinAuthManager(@ApplicationContext context: Context): PinAuthManager {
        return PinAuthManager(context)
    }
    
    /**
     * 提供密码仓库
     */
    @Provides
    @Singleton
    fun providePasswordRepository(
        passwordDao: com.example.passwordvault.data.database.PasswordDao,
        categoryDao: com.example.passwordvault.data.database.CategoryDao,
        encryptionManager: EncryptionManager
    ): PasswordRepository {
        return PasswordRepository(passwordDao, categoryDao, encryptionManager)
    }
}