package com.maxmar.attendance.di

import com.maxmar.attendance.data.api.AuthApi
import com.maxmar.attendance.data.api.EmployeeApi
import com.maxmar.attendance.data.local.TokenManager
import com.maxmar.attendance.data.repository.AuthRepository
import com.maxmar.attendance.data.repository.EmployeeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for repository dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepository(authApi, tokenManager)
    }
    
    @Provides
    @Singleton
    fun provideEmployeeRepository(
        employeeApi: EmployeeApi
    ): EmployeeRepository {
        return EmployeeRepository(employeeApi)
    }
}
