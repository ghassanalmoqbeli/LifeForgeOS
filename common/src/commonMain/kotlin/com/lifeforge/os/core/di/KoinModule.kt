package com.lifeforge.os.core.di

import com.lifeforge.os.core.logging.LifeForgeLogger
import com.lifeforge.os.core.logging.LifeForgeLoggerImpl
import com.lifeforge.os.core.preferences.KeyValueStore
import com.lifeforge.os.core.preferences.PreferencesManager
import com.lifeforge.os.core.preferences.PreferencesManagerImpl
import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.DefaultCoroutineScopeProvider
import com.lifeforge.os.core.utils.mainDispatcher
import com.lifeforge.os.data.database.LifeForgeDb
import com.lifeforge.os.data.repository.BodyTrackingRepositoryImpl
import com.lifeforge.os.data.repository.BookRepositoryImpl
import com.lifeforge.os.data.repository.CourseRepositoryImpl
import com.lifeforge.os.data.repository.ExerciseRepositoryImpl
import com.lifeforge.os.data.repository.FoodRepositoryImpl
import com.lifeforge.os.data.repository.GoalRepositoryImpl
import com.lifeforge.os.data.repository.HabitRepositoryImpl
import com.lifeforge.os.data.repository.JournalRepositoryImpl
import com.lifeforge.os.data.repository.MediaRepositoryImpl
import com.lifeforge.os.data.repository.NoteRepositoryImpl
import com.lifeforge.os.data.repository.NutritionRepositoryImpl
import com.lifeforge.os.data.repository.RecoveryRepositoryImpl
import com.lifeforge.os.data.repository.RoutineRepositoryImpl
import com.lifeforge.os.data.repository.SettingsRepositoryImpl
import com.lifeforge.os.data.repository.SyncRepositoryImpl
import com.lifeforge.os.data.repository.TaskRepositoryImpl
import com.lifeforge.os.data.repository.TimerRepositoryImpl
import com.lifeforge.os.data.repository.UserProfileRepositoryImpl
import com.lifeforge.os.data.repository.WaterRepositoryImpl
import com.lifeforge.os.data.repository.WorkoutProgramRepositoryImpl
import com.lifeforge.os.data.repository.WorkoutRepositoryImpl
import com.lifeforge.os.domain.repository.BodyTrackingRepository
import com.lifeforge.os.domain.repository.BookRepository
import com.lifeforge.os.domain.repository.CourseRepository
import com.lifeforge.os.domain.repository.ExerciseRepository
import com.lifeforge.os.domain.repository.FoodRepository
import com.lifeforge.os.domain.repository.GoalRepository
import com.lifeforge.os.domain.repository.HabitRepository
import com.lifeforge.os.domain.repository.JournalRepository
import com.lifeforge.os.domain.repository.MediaRepository
import com.lifeforge.os.domain.repository.NoteRepository
import com.lifeforge.os.domain.repository.NutritionRepository
import com.lifeforge.os.domain.repository.RecoveryRepository
import com.lifeforge.os.domain.repository.RoutineRepository
import com.lifeforge.os.domain.repository.SettingsRepository
import com.lifeforge.os.domain.repository.SyncOperationModel
import com.lifeforge.os.domain.repository.SyncRepository
import com.lifeforge.os.domain.repository.TaskRepository
import com.lifeforge.os.domain.repository.TimerRepository
import com.lifeforge.os.domain.repository.UserProfileRepository
import com.lifeforge.os.domain.repository.WaterRepository
import com.lifeforge.os.domain.repository.WorkoutProgramRepository
import com.lifeforge.os.domain.repository.WorkoutRepository
import com.lifeforge.os.presentation.books.BooksViewModel
import com.lifeforge.os.presentation.courses.CoursesViewModel
import com.lifeforge.os.presentation.entertainment.EntertainmentViewModel
import com.lifeforge.os.presentation.goals.GoalsViewModel
import com.lifeforge.os.presentation.gym.GymViewModel
import com.lifeforge.os.presentation.habits.HabitsViewModel
import com.lifeforge.os.presentation.home.HomeViewModel
import com.lifeforge.os.presentation.journal.JournalViewModel
import com.lifeforge.os.presentation.notes.NotesViewModel
import com.lifeforge.os.presentation.nutrition.NutritionViewModel
import com.lifeforge.os.presentation.onboarding.OnboardingViewModel
import com.lifeforge.os.presentation.recovery.RecoveryViewModel
import com.lifeforge.os.presentation.routines.RoutinesViewModel
import com.lifeforge.os.presentation.settings.SettingsViewModel
import com.lifeforge.os.presentation.tasks.TasksViewModel
import com.lifeforge.os.presentation.timers.TimersViewModel
import com.lifeforge.os.security.AppLockManager
import com.lifeforge.os.security.BiometricAuthenticator
import com.lifeforge.os.security.PinCodeHasher
import com.lifeforge.os.sync.SyncManager
import com.lifeforge.os.sync.SyncManagerImpl
import com.lifeforge.os.sync.cloud.CloudSyncProvider
import com.lifeforge.os.sync.cloud.NoopCloudSyncProvider
import com.lifeforge.os.sync.conflict.ConflictResolver
import com.lifeforge.os.sync.conflict.ConflictResolverImpl
import com.lifeforge.os.sync.engine.SyncEngine
import com.lifeforge.os.sync.engine.SyncEngineImpl
import com.lifeforge.os.sync.engine.SyncOperation
import com.lifeforge.os.sync.engine.SyncOperationType
import com.lifeforge.os.sync.engine.SyncQueue
import com.lifeforge.os.sync.engine.SyncQueueImpl
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

private fun SyncOperationModel.toSyncOperation(): SyncOperation = SyncOperation(
    type = if (type == "Delete") SyncOperationType.Delete else SyncOperationType.Upsert,
    entityType = entityType,
    entityId = entityId,
    data = data,
    version = version,
    deviceId = deviceId,
)

val koinModules: Module = module {
    // Core
    single<CoroutineScopeProvider> { DefaultCoroutineScopeProvider(mainDispatcher()) }
    single<LifeForgeLogger> { LifeForgeLoggerImpl() }
    single<KeyValueStore> { get<PlatformBindings>().keyValueStore }
    single<PreferencesManager> { PreferencesManagerImpl(get()) }

    // Database
    single<LifeForgeDb> { get<PlatformBindings>().db }

    // Repositories
    single<UserProfileRepository> { UserProfileRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<ExerciseRepository> { ExerciseRepositoryImpl(get()) }
    single<WorkoutProgramRepository> { WorkoutProgramRepositoryImpl(get()) }
    single<WorkoutRepository> { WorkoutRepositoryImpl(get()) }
    single<BodyTrackingRepository> { BodyTrackingRepositoryImpl(get()) }
    single<FoodRepository> { FoodRepositoryImpl(get()) }
    single<NutritionRepository> { NutritionRepositoryImpl(get()) }
    single<WaterRepository> { WaterRepositoryImpl(get()) }
    single<HabitRepository> { HabitRepositoryImpl(get()) }
    single<RoutineRepository> { RoutineRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<GoalRepository> { GoalRepositoryImpl(get()) }
    single<JournalRepository> { JournalRepositoryImpl(get()) }
    single<NoteRepository> { NoteRepositoryImpl(get()) }
    single<BookRepository> { BookRepositoryImpl(get()) }
    single<CourseRepository> { CourseRepositoryImpl(get()) }
    single<MediaRepository> { MediaRepositoryImpl(get()) }
    single<RecoveryRepository> { RecoveryRepositoryImpl(get()) }
    single<TimerRepository> { TimerRepositoryImpl(get()) }

    // Presentation layer view models
    single<HomeViewModel> { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    single<OnboardingViewModel> { OnboardingViewModel(get(), get(), get(), get()) }
    single<SettingsViewModel> { SettingsViewModel(get(), get()) }
    single<HabitsViewModel> { HabitsViewModel(get(), get()) }
    single<TasksViewModel> { TasksViewModel(get(), get()) }
    single<NutritionViewModel> { NutritionViewModel(get(), get(), get(), get(), get()) }
    single<GymViewModel> { GymViewModel(get(), get(), get()) }
    single<JournalViewModel> { JournalViewModel(get(), get()) }
    single<RecoveryViewModel> { RecoveryViewModel(get(), get()) }
    single<TimersViewModel> { TimersViewModel(get(), get()) }
    single<GoalsViewModel> { GoalsViewModel(get(), get()) }
    single<RoutinesViewModel> { RoutinesViewModel(get(), get()) }
    single<NotesViewModel> { NotesViewModel(get(), get()) }
    single<BooksViewModel> { BooksViewModel(get(), get()) }
    single<CoursesViewModel> { CoursesViewModel(get(), get()) }
    single<EntertainmentViewModel> { EntertainmentViewModel(get(), get()) }

    // Security
    single<PinCodeHasher> { get<PlatformBindings>().pinCodeHasher }
    single<BiometricAuthenticator> { get<PlatformBindings>().biometricAuthenticator }
    single<AppLockManager> { get<PlatformBindings>().appLockManager }

    // Cloud sync
    single<CloudSyncProvider> { get<PlatformBindings>().cloudSyncProvider ?: NoopCloudSyncProvider() }
    single<SyncRepository> { SyncRepositoryImpl(get()) }
    single<SyncQueue> { SyncQueueImpl() }
    single<ConflictResolver> {
        ConflictResolverImpl(
            scopeProvider = get(),
            onResolve = { conflict, _, winningVersion ->
                get<SyncRepository>().resolveConflict(conflict.entityType, conflict.entityId, winningVersion)
            },
            logger = get(),
        )
    }
    single<SyncEngine> {
        SyncEngineImpl(
            scopeProvider = get(),
            syncQueue = get(),
            cloudSyncProvider = get(),
            conflictResolver = get(),
            onStatusUpdate = { type, id, status, version ->
                get<SyncRepository>().updateSyncStatus(type, id, status, version)
            },
            getPendingChanges = { get<SyncRepository>().getPendingChanges().map { it.toSyncOperation() } },
            logger = get(),
        )
    }
    single<SyncManager> {
        SyncManagerImpl(get(), get(), get(), get(), get(), get(), get(), get())
    }
}

fun startKoin(context: Any?) {
    if (org.koin.core.context.GlobalContext.getOrNull() == null) {
        startKoin {
            modules(
                koinModules,
                module {
                    single<PlatformBindings> { createPlatformBindings(context) }
                },
            )
        }
    }
}