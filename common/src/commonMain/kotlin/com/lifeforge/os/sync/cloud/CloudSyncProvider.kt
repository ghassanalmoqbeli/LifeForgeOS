package com.lifeforge.os.sync.cloud

import com.lifeforge.os.core.utils.Result
import com.lifeforge.os.sync.engine.SyncOperation

interface CloudSyncProvider {
    suspend fun ensureAuthenticated(): Result<AuthResult>
    suspend fun signIn(email: String, password: String): Result<AuthResult>
    suspend fun signUp(email: String, password: String): Result<AuthResult>
    suspend fun signInWithGoogle(): Result<AuthResult>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun upsert(operation: SyncOperation): SyncResult
    suspend fun delete(operation: SyncOperation): SyncResult
    suspend fun query(entityType: String, filters: Map<String, Any> = emptyMap()): Result<List<RemoteRecord>>
    suspend fun uploadFile(path: String, data: ByteArray, mimeType: String): Result<String>
    suspend fun downloadFile(url: String): Result<ByteArray>
    suspend fun deleteFile(url: String): Result<Unit>
}

data class AuthResult(
    val userId: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
)

data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean,
)

/**
 * Result of a single upsert/delete sync operation.
 */
data class SyncResult(
    val isSuccess: Boolean,
    val remoteVersion: Long? = null,
    val conflict: RemoteRecord? = null,
    val error: String? = null,
)

/**
 * A remote record (Firestore doc etc.) for conflict comparison.
 */
data class RemoteRecord(
    val entityType: String,
    val entityId: String,
    val version: Long,
    val deviceId: String,
    val updatedAt: Long,
    val data: String,
)