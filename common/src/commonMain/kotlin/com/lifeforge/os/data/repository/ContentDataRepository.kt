package com.lifeforge.os.data.repository

import com.lifeforge.os.data.database.LifeForgeDb
import com.lifeforge.os.domain.model.Book
import com.lifeforge.os.domain.model.BookStatus
import com.lifeforge.os.domain.model.Course
import com.lifeforge.os.domain.model.CourseLesson
import com.lifeforge.os.domain.model.JournalEntry
import com.lifeforge.os.domain.model.MediaItem
import com.lifeforge.os.domain.model.MediaType
import com.lifeforge.os.domain.model.Note
import com.lifeforge.os.domain.model.NoteType
import com.lifeforge.os.domain.repository.BookRepository
import com.lifeforge.os.domain.repository.CourseRepository
import com.lifeforge.os.domain.repository.JournalRepository
import com.lifeforge.os.domain.repository.MediaRepository
import com.lifeforge.os.domain.repository.NoteRepository
import com.lifeforge.os.sync.SyncStatus
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JournalRepositoryImpl(
    private val db: LifeForgeDb,
) : JournalRepository {

    override fun observeAll(): Flow<List<JournalEntry>> =
        db.q.selectAllJournal().asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override fun observeMonth(year: Int, month: Int): Flow<List<JournalEntry>> {
        val start = yearStart(year, month)
        val end = yearStart(if (month == 12) year + 1 else year, if (month == 12) 1 else month + 1) - 1
        return db.q.selectJournalBetween(start, end).asFlow().map { it.list }
            .map { rows -> rows.map { it.toDomain() } }
    }

    override fun observePrivate(): Flow<List<JournalEntry>> =
        db.q.selectPrivateJournal().asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override suspend fun getById(id: String): JournalEntry? =
        db.q.selectJournalEntryById(id).executeAsOneOrNull()?.toDomain()

    override suspend fun save(entry: JournalEntry) {
        val now = System.currentTimeMillis()
        db.q.insertJournalEntry(
            id = entry.id,
            date = entry.date,
            title = entry.title,
            content = entry.content,
            mood = entry.mood,
            energy = entry.energy?.toLong(),
            stress = entry.stress?.toLong(),
            tags = entry.tags.toCsvOrNull(),
            isPrivate = entry.isPrivate.asInt(),
            createdAt = entry.createdAt,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteJournalEntry(now, now, id)
    }

    override suspend fun search(query: String): List<JournalEntry> =
        db.q.selectJournalBySearch(query, query).executeAsList().map { it.toDomain() }

    private fun yearStart(year: Int, month: Int): Long {
        // Approximate midnight timestamp for the given month, estimated via days.
        var days = 0
        for (y in 1970 until year) days += if (y.isLeapYear()) 366 else 365
        val daysInMonth = intArrayOf(31, if (year.isLeapYear()) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        for (m in 1 until month) days += daysInMonth[m - 1]
        return days * 24L * 60 * 60 * 1000
    }

    private fun Int.isLeapYear(): Boolean = (this % 4 == 0 && this % 100 != 0) || this % 400 == 0
}

class NoteRepositoryImpl(
    private val db: LifeForgeDb,
) : NoteRepository {

    override fun observeAll(): Flow<List<Note>> =
        db.q.selectAllNotes().asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override fun observePinned(): Flow<List<Note>> =
        db.q.selectPinnedNotes().asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override fun observePrivate(): Flow<List<Note>> =
        db.q.selectPrivateNotes().asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override fun observeByType(type: NoteType): Flow<List<Note>> =
        db.q.selectNotesByType(type.name).asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override suspend fun getById(id: String): Note? =
        db.q.selectAllNotes().executeAsList().firstOrNull { it.id == id }?.toDomain()

    override suspend fun save(note: Note) {
        val now = System.currentTimeMillis()
        db.q.insertNote(
            id = note.id,
            type = note.type.name,
            title = note.title,
            content = note.content,
            plainText = note.plainText,
            isPinned = note.isPinned.asInt(),
            isPrivate = note.isPrivate.asInt(),
            tags = note.tags.toCsvOrNull(),
            linkedEntityType = note.linkedEntityType,
            linkedEntityId = note.linkedEntityId,
            createdAt = note.createdAt,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteNote(now, now, id)
    }

    override suspend fun togglePinned(id: String) {
        db.q.toggleNotePinned(System.currentTimeMillis(), id)
    }

    override suspend fun search(query: String): List<Note> =
        db.q.selectAllNotes().executeAsList()
            .filter { it.plainText?.contains(query, ignoreCase = true) == true || it.title?.contains(query, ignoreCase = true) == true }
            .map { it.toDomain() }
}

class BookRepositoryImpl(
    private val db: LifeForgeDb,
) : BookRepository {

    override fun observeAll(): Flow<List<Book>> =
        db.q.selectAllBooks().asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override fun observeByStatus(status: BookStatus): Flow<List<Book>> =
        db.q.selectBooksByStatus(status.name).asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override fun observeReading(): Flow<List<Book>> =
        db.q.selectReadingBooks().asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override suspend fun getById(id: String): Book? =
        db.q.selectAllBooks().executeAsList().firstOrNull { it.id == id }?.toDomain()

    override suspend fun save(book: Book) {
        val now = System.currentTimeMillis()
        db.q.insertBook(
            id = book.id,
            title = book.title,
            author = book.author,
            coverUrl = book.coverUrl,
            description = book.description,
            category = book.category,
            format = book.format,
            totalPages = book.totalPages?.toLong(),
            currentPage = book.currentPage.toLong(),
            status = book.status.name,
            rating = book.rating,
            isFavorite = book.isFavorite.asInt(),
            createdAt = book.createdAt,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun updateReadingProgress(id: String, page: Int): Book? {
        db.q.updateBookProgress(page.toLong(), System.currentTimeMillis(), id)
        return getById(id)
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteBook(now, now, id)
    }
}

class CourseRepositoryImpl(
    private val db: LifeForgeDb,
) : CourseRepository {

    private fun loadSections(courseId: String): List<com.lifeforge.os.domain.model.CourseSection> =
        db.q.selectCourseSections(courseId).executeAsList().map { section ->
            val lessons = db.q.selectCourseLessons(section.id).executeAsList()
                .map { it.toDomain() }
            section.toDomain(lessons)
        }

    override fun observeAll(): Flow<List<Course>> =
        db.q.selectAllCourses().asFlow().map { it.list }
            .map { rows -> rows.map { row -> row.toDomain(loadSections(row.id)) } }

    override fun observeInProgress(): Flow<List<Course>> =
        db.q.selectAllCourses().asFlow().map { it.list }
            .map { rows -> rows.filter { it.status == "InProgress" }.map { row -> row.toDomain(loadSections(row.id)) } }

    override suspend fun getById(id: String): Course? =
        db.q.selectAllCourses().executeAsList().firstOrNull { it.id == id }
            ?.let { it.toDomain(loadSections(it.id)) }

    override suspend fun save(course: Course) {
        val now = System.currentTimeMillis()
        db.q.insertCourse(
            id = course.id,
            title = course.title,
            provider = course.provider,
            instructor = course.instructor,
            thumbnailUrl = course.thumbnailUrl,
            description = course.description,
            category = course.category,
            totalLessons = course.sections.sumOf { it.lessons.size.toLong() },
            status = course.status.name,
            progress = course.progressPercent,
            isFavorite = course.isFavorite.asInt(),
            createdAt = course.createdAt,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
        course.sections.forEach { section ->
            val sectionId = section.id.ifBlank { generateId() }
            db.q.insertCourseSection(
                id = sectionId,
                courseId = course.id,
                title = section.title,
                sectionOrder = section.order.toLong(),
                createdAt = now,
                updatedAt = now,
                deviceId = "",
                syncStatus = SyncStatus.Pending.name,
            )
            section.lessons.forEach { lesson ->
                db.q.insertCourseLesson(
                    id = lesson.id.ifBlank { generateId() },
                    sectionId = sectionId,
                    title = lesson.title,
                    lessonOrder = lesson.order.toLong(),
                    videoUrl = lesson.videoUrl,
                    durationMinutes = lesson.durationMinutes?.toLong(),
                    isCompleted = lesson.isCompleted.asInt(),
                    lastPosition = lesson.lastPositionSeconds.toLong(),
                    progress = lesson.progressPercent,
                    createdAt = now,
                    updatedAt = now,
                    deviceId = "",
                    syncStatus = SyncStatus.Pending.name,
                )
            }
        }
    }

    override suspend fun completeLesson(courseId: String, lessonId: String): Course? {
        val now = System.currentTimeMillis()
        db.q.completeCourseLesson(now, now, lessonId)
        val course = getById(courseId) ?: return null
        val total = course.sections.sumOf { it.lessons.size }
        val done = course.sections.sumOf { it.lessons.count { l -> l.isCompleted } }
        val progress = if (total == 0) 0.0 else done * 100.0 / total
        db.q.updateCourseProgress(progress, progress, now, courseId)
        return getById(courseId)
    }

    override suspend fun updateLessonPosition(courseId: String, lessonId: String, positionSeconds: Int) {
        db.q.updateCourseLessonPosition(positionSeconds.toLong(), System.currentTimeMillis(), lessonId)
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteCourse(now, now, id)
    }
}

class MediaRepositoryImpl(
    private val db: LifeForgeDb,
) : MediaRepository {

    override fun observeAll(): Flow<List<MediaItem>> =
        db.q.selectAllMedia().asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override fun observeByType(type: MediaType): Flow<List<MediaItem>> =
        db.q.selectMediaByType(type.name).asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override fun observeWatching(): Flow<List<MediaItem>> =
        db.q.selectWatchingMedia().asFlow().map { it.list }.map { rows -> rows.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<MediaItem>> =
        db.q.selectAllMedia().asFlow().map { it.list }.map { rows -> rows.filter { it.isFavorite == 1L }.map { it.toDomain() } }

    override suspend fun getById(id: String): MediaItem? =
        db.q.selectAllMedia().executeAsList().firstOrNull { it.id == id }?.toDomain()

    override suspend fun save(item: MediaItem) {
        val now = System.currentTimeMillis()
        db.q.insertMediaItem(
            id = item.id,
            type = item.type.name,
            title = item.title,
            titleAr = item.titleAr,
            imageUrl = item.imageUrl,
            backdropUrl = item.backdropUrl,
            description = item.description,
            externalUrl = item.externalUrl,
            status = item.status.name,
            rating = item.rating,
            personalRating = item.personalRating,
            review = item.review,
            tags = item.tags.toCsvOrNull(),
            isFavorite = item.isFavorite.asInt(),
            progressPercent = item.progressPercent,
            createdAt = item.createdAt,
            updatedAt = now,
            deviceId = "",
            syncStatus = SyncStatus.Pending.name,
        )
    }

    override suspend fun delete(id: String) {
        val now = System.currentTimeMillis()
        db.q.deleteMediaItem(now, now, id)
    }
}