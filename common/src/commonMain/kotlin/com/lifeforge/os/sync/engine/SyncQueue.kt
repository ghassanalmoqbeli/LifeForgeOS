package com.lifeforge.os.sync.engine

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Thread-safe (suspending) priority queue for sync operations.
 * High > Normal > Low.
 */
class SyncQueueImpl : SyncQueue {
    private val mutex = Mutex()
    private val high = ArrayDeque<SyncOperation>()
    private val normal = ArrayDeque<SyncOperation>()
    private val low = ArrayDeque<SyncOperation>()

    override suspend fun size(): Int = mutex.withLock { high.size + normal.size + low.size }

    override suspend fun isNotEmpty(): Boolean = mutex.withLock {
        high.isNotEmpty() || normal.isNotEmpty() || low.isNotEmpty()
    }

    override suspend fun add(operation: SyncOperation) {
        mutex.withLock {
            when (operation.priority) {
                SyncPriority.High -> high.addLast(operation)
                SyncPriority.Normal -> normal.addLast(operation)
                SyncPriority.Low -> low.addLast(operation)
            }
        }
    }

    override suspend fun addToFront(operation: SyncOperation) {
        mutex.withLock {
            when (operation.priority) {
                SyncPriority.High -> high.addFirst(operation)
                SyncPriority.Normal -> normal.addFirst(operation)
                SyncPriority.Low -> low.addFirst(operation)
            }
        }
    }

    override suspend fun poll(): SyncOperation? = mutex.withLock {
        val h = high.removeFirstOrNull()
        if (h != null) return@withLock h
        val n = normal.removeFirstOrNull()
        if (n != null) return@withLock n
        low.removeFirstOrNull()
    }

    override suspend fun clear() {
        mutex.withLock {
            high.clear()
            normal.clear()
            low.clear()
        }
    }
}

interface SyncQueue {
    suspend fun size(): Int
    suspend fun isNotEmpty(): Boolean
    suspend fun add(operation: SyncOperation)
    suspend fun addToFront(operation: SyncOperation)
    suspend fun poll(): SyncOperation?
    suspend fun clear()
}