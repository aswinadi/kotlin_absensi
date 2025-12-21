package com.maxmar.attendance.di

import com.maxmar.attendance.BuildConfig
import com.maxmar.attendance.data.api.AbsentApi
import com.maxmar.attendance.data.api.ApprovalApi
import com.maxmar.attendance.data.api.AttendanceApi
import com.maxmar.attendance.data.api.AuthApi
import com.maxmar.attendance.data.api.AuthInterceptor
import com.maxmar.attendance.data.api.BusinessTripApi
import com.maxmar.attendance.data.api.EmployeeApi
import com.maxmar.attendance.data.api.NotificationApi
import com.maxmar.attendance.data.local.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for network dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideEmployeeApi(retrofit: Retrofit): EmployeeApi {
        return retrofit.create(EmployeeApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAttendanceApi(retrofit: Retrofit): AttendanceApi {
        return retrofit.create(AttendanceApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAbsentApi(retrofit: Retrofit): AbsentApi {
        return retrofit.create(AbsentApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideBusinessTripApi(retrofit: Retrofit): BusinessTripApi {
        return retrofit.create(BusinessTripApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideApprovalApi(retrofit: Retrofit): ApprovalApi {
        return retrofit.create(ApprovalApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }
}
