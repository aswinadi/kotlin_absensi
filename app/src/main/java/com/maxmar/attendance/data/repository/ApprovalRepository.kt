package com.maxmar.attendance.data.repository

import com.maxmar.attendance.data.api.ApprovalApi
import com.maxmar.attendance.data.model.Approval
import com.maxmar.attendance.data.model.ApprovalListData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for approval operations.
 */
@Singleton
class ApprovalRepository @Inject constructor(
    private val approvalApi: ApprovalApi
) {
    
    /**
     * Fetch all approvals (pending and processed).
     */
    suspend fun fetchApprovals(): AuthResult<ApprovalListData> {
        return try {
            val response = approvalApi.getApprovals()
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error("Data tidak ditemukan")
            }
        } catch (e: retrofit2.HttpException) {
            AuthResult.Error("Error: ${e.code()}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Acknowledge a leave request.
     */
    suspend fun acknowledge(id: Int): AuthResult<Approval> {
        return try {
            val response = approvalApi.acknowledge(id)
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Gagal mengakui")
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = parseErrorMessage(errorBody) ?: "Error: ${e.code()}"
            AuthResult.Error(message)
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Approve a leave request.
     */
    suspend fun approve(id: Int): AuthResult<Approval> {
        return try {
            val response = approvalApi.approve(id)
            if (response.success && response.data != null) {
                AuthResult.Success(response.data)
            } else {
                AuthResult.Error(response.message ?: "Gagal menyetujui")
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = parseErrorMessage(errorBody) ?: "Error: ${e.code()}"
            AuthResult.Error(message)
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    /**
     * Reject a leave request.
     */
    suspend fun reject(id: Int, reason: String? = null): AuthResult<Unit> {
        return try {
            val response = approvalApi.reject(id, reason)
            if (response.success) {
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(response.message ?: "Gagal menolak")
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = parseErrorMessage(errorBody) ?: "Error: ${e.code()}"
            AuthResult.Error(message)
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }
    
    private fun parseErrorMessage(errorBody: String?): String? {
        return try {
            val json = com.google.gson.Gson().fromJson(errorBody, Map::class.java)
            json["message"]?.toString()
        } catch (_: Exception) {
            null
        }
    }
}
