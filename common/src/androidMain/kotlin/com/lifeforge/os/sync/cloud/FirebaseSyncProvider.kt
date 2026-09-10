package com.lifeforge.os.sync.cloud

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.lifeforge.os.core.logging.LifeForgeLogger
import com.lifeforge.os.core.utils.Result
import com.lifeforge.os.sync.engine.SyncOperation
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class FirebaseSyncProvider(
    private val context: Context,
    private val logger: LifeForgeLogger,
) : CloudSyncProvider {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val json = Json { ignoreUnknownKeys = true }

    private fun userCollection(entityType: String) =
        firestore.collection("users/${currentUid()}/$entityType")

    private fun currentUid(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("Not authenticated")

    override suspend fun ensureAuthenticated(): Result<AuthResult> = try {
        val user = auth.currentUser ?: return Result.Failure(
            com.lifeforge.os.core.utils.AppError.Auth("Not authenticated")
        )
        val token = user.getIdToken(true).await()
        Result.Success(
            AuthResult(
                userId = user.uid,
                email = user.email ?: "",
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString(),
                accessToken = token.token,
                refreshToken = "",
                expiresAt = System.currentTimeMillis() + 3_600_000,
            )
        )
    } catch (e: Exception) {
        Result.Failure(com.lifeforge.os.core.utils.AppError.Auth(e.message ?: "Auth failed", e))
    }

    override suspend fun signIn(email: String, password: String): Result<AuthResult> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val token = result.user?.getIdToken(true)?.await()
        Result.Success(
            AuthResult(
                userId = result.user!!.uid,
                email = result.user!!.email!!,
                displayName = result.user!!.displayName,
                photoUrl = result.user!!.photoUrl?.toString(),
                accessToken = token!!.token,
                refreshToken = "",
                expiresAt = System.currentTimeMillis() + 3_600_000,
            )
        )
    } catch (e: Exception) {
        Result.Failure(com.lifeforge.os.core.utils.AppError.Auth(e.message ?: "Sign in failed", e))
    }

    override suspend fun signUp(email: String, password: String): Result<AuthResult> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val token = result.user?.getIdToken(true)?.await()
        Result.Success(
            AuthResult(
                userId = result.user!!.uid,
                email = result.user!!.email!!,
                displayName = result.user!!.displayName,
                photoUrl = result.user!!.photoUrl?.toString(),
                accessToken = token!!.token,
                refreshToken = "",
                expiresAt = System.currentTimeMillis() + 3_600_000,
            )
        )
    } catch (e: Exception) {
        Result.Failure(com.lifeforge.os.core.utils.AppError.Auth(e.message ?: "Sign up failed", e))
    }

    override suspend fun signInWithGoogle(): Result<AuthResult> =
        Result.Failure(com.lifeforge.os.core.utils.AppError.Auth("Google Sign-In requires Activity context"))

    override suspend fun signOut(): Result<Unit> = try {
        auth.signOut()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Failure(com.lifeforge.os.core.utils.AppError.Auth(e.message ?: "Sign out failed", e))
    }

    override suspend fun getCurrentUser(): User? {
        val user = auth.currentUser ?: return null
        return User(user.uid, user.email ?: "", user.displayName, user.photoUrl?.toString(), user.isAnonymous)
    }

    override suspend fun upsert(operation: SyncOperation): SyncResult = try {
        val docRef = userCollection(operation.entityType).document(operation.entityId)

        val docJson = json.parseToJsonElement(operation.data).obj
            .plus("version" to kotlinx.serialization.json.JsonPrimitive(operation.version))
            .plus("deviceId" to kotlinx.serialization.json.JsonPrimitive(operation.deviceId))
            .plus("updatedAt" to kotlinx.serialization.json.JsonPrimitive(operation.version))

        // Firestore expects a Map<String, Any> for nested objects.
        val writable = docJson.mapValues { (_, v) -> jsonValueToFirestore(v) }
        docRef.set(writable, SetOptions.merge()).await()

        SyncResult(isSuccess = true, remoteVersion = operation.version)
    } catch (e: Exception) {
        val remote = try {
            val doc = userCollection(operation.entityType).document(operation.entityId).get().await()
            if (doc.exists()) {
                RemoteRecord(
                    entityType = operation.entityType,
                    entityId = operation.entityId,
                    version = doc.getLong("version") ?: 0,
                    deviceId = doc.getString("deviceId") ?: "",
                    updatedAt = doc.getLong("updatedAt") ?: 0,
                    data = doc.data?.toString() ?: "",
                )
            } else null
        } catch (ignore: Exception) { null }

        SyncResult(isSuccess = false, conflict = remote, error = e.message)
    }

    override suspend fun delete(operation: SyncOperation): SyncResult = try {
        userCollection(operation.entityType).document(operation.entityId).delete().await()
        SyncResult(isSuccess = true)
    } catch (e: Exception) {
        SyncResult(isSuccess = false, error = e.message)
    }

    override suspend fun query(
        entityType: String,
        filters: Map<String, Any>,
    ): Result<List<RemoteRecord>> = try {
        var query = userCollection(entityType)
        filters.forEach { (k, v) -> query = query.whereEqualTo(k, v) }
        val snapshot = query.get().await()
        val records = snapshot.documents.map { doc ->
            RemoteRecord(
                entityType = entityType,
                entityId = doc.id,
                version = doc.getLong("version") ?: 0,
                deviceId = doc.getString("deviceId") ?: "",
                updatedAt = doc.getLong("updatedAt") ?: 0,
                data = doc.data?.toString() ?: "",
            )
        }
        Result.Success(records)
    } catch (e: Exception) {
        Result.Failure(com.lifeforge.os.core.utils.AppError.Network(e.message ?: "Query failed", e))
    }

    override suspend fun uploadFile(path: String, data: ByteArray, mimeType: String): Result<String> = try {
        val ref = storage.reference.child("users/${currentUid()}/$path")
        ref.putBytes(data).await()
        val url = ref.downloadUrl.await().toString()
        Result.Success(url)
    } catch (e: Exception) {
        Result.Failure(com.lifeforge.os.core.utils.AppError.Network(e.message ?: "Upload failed", e))
    }

    override suspend fun downloadFile(url: String): Result<ByteArray> = try {
        val ref = storage.getReferenceFromUrl(url)
        Result.Success(ref.getBytes(Long.MAX_VALUE).await())
    } catch (e: Exception) {
        Result.Failure(com.lifeforge.os.core.utils.AppError.Network(e.message ?: "Download failed", e))
    }

    override suspend fun deleteFile(url: String): Result<Unit> = try {
        storage.getReferenceFromUrl(url).delete().await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Failure(com.lifeforge.os.core.utils.AppError.Network(e.message ?: "Delete failed", e))
    }

    private fun jsonValueToFirestore(v: kotlinx.serialization.json.JsonElement): Any? = when (v) {
        is JsonObject -> v.mapValues { jsonValueToFirestore(it.value) }
        is kotlinx.serialization.json.JsonPrimitive ->
            when {
                v.isString -> v.content
                v.content == "true" -> true
                v.content == "false" -> false
                else -> v.content.toLongOrNull()
                    ?: v.content.toDoubleOrNull()
                    ?: v.content
            }
        is kotlinx.serialization.json.JsonArray -> v.map { jsonValueToFirestore(it) }
        else -> null
    }
}