package com.lifeforge.os.sync.cloud

import com.lifeforge.os.core.utils.AppError
import com.lifeforge.os.core.utils.Result
import com.lifeforge.os.sync.engine.SyncOperation

/**
 * Platform with no cloud backend (e.g. desktop) gets this provider so
 * the rest of the app can depend on [CloudSyncProvider] unconditionally.
 */
class NoopCloudSyncProvider : CloudSyncProvider {
    private val unavailable = Result.Failure(AppError.Auth("Cloud sync is not available on this platform"))

    override suspend fun ensureAuthenticated(): Result<AuthResult> = unavailable
    override suspend fun signIn(email: String, password: String): Result<AuthResult> = unavailable
    override suspend fun signUp(email: String, password: String): Result<AuthResult> = unavailable
    override suspend fun signInWithGoogle(): Result<AuthResult> = unavailable
    override suspend fun signOut(): Result<Unit> = unavailable
    override suspend fun getCurrentUser(): User? = null
    override suspend fun upsert(operation: SyncOperation): SyncResult =
        SyncResult(isSuccess = false, error = "Cloud sync is not available on this platform")
    override suspend fun delete(operation: SyncOperation): SyncResult =
        SyncResult(isSuccess = false, error = "Cloud sync is not available on this platform")
    override suspend fun query(entityType: String, filters: Map<String, Any>): Result<List<RemoteRecord>> = unavailable
    override suspend fun uploadFile(path: String, data: ByteArray, mimeType: String): Result<String> = unavailable
    override suspend fun downloadFile(url: String): Result<ByteArray> = unavailable
    override suspend fun deleteFile(url: String): Result<Unit> = unavailable
}