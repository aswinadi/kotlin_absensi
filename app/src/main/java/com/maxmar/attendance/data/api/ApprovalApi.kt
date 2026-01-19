package com.maxmar.attendance.data.api

import com.maxmar.attendance.data.model.ApprovalActionResponse
import com.maxmar.attendance.data.model.ApprovalListResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API interface for approval endpoints.
 */
interface ApprovalApi {
    
    @GET(ApiEndpoints.APPROVALS)
    suspend fun getApprovals(): ApprovalListResponse
    
    @POST("att/approvals/{id}/acknowledge")
    suspend fun acknowledge(@Path("id") id: Int): ApprovalActionResponse
    
    @POST("att/approvals/{id}/approve")
    suspend fun approve(@Path("id") id: Int): ApprovalActionResponse
    
    @FormUrlEncoded
    @POST("att/approvals/{id}/reject")
    suspend fun reject(
        @Path("id") id: Int,
        @Field("reason") reason: String?
    ): ApprovalActionResponse
}
