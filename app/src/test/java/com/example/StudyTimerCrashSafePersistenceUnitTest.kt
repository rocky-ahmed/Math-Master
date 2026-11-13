package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MathDatabase
import com.example.data.model.StudyTimerFlushOperation
import com.example.data.repository.MathRepository.StudyFlushFaultPoint
import com.example.math.MonotonicClock
import com.example.ui.MathViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Simulated failure-injection & Room SQLite persistence suite for BUG-86 (Study Timer Crash-Safe Flush Journal).
 * Note: Process termination is simulated in Robolectric JVM by clearing ViewModelStore without invoking
 * flush/pause callbacks, verifying SharedPreferences + Room SQLite durability and idempotency across restarts.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StudyTimerCrashSafePersistenceUnitTest {

    private lateinit var app: Application
    private lateinit var db: MathDatabase

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        MathViewModel.globalStudyFlushFaultInjectorForTesting = null
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        db = MathDatabase.getInstance(app)
        runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            db.clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @After
    fun tearDown() {
        MathViewModel.globalStudyFlushFaultInjectorForTesting = null
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            db.clearAllTables()
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    private fun createManagedViewModel(
        store: ViewModelStore,
        dateProvider: () -> String,
        clock: MonotonicClock = object : MonotonicClock {
            override fun elapsedRealtime(): Long = 100_000L
        }
    ): MathViewModel {
        val factory = ViewModelProvider.AndroidViewModelFactory(app)
        val vm = ViewModelProvider(store, factory)[MathViewModel::class.java]
        vm.currentDateKeyProvider = dateProvider
        vm.clock = clock
        return vm
    }

    private suspend fun awaitCondition(maxAttempts: Int = 60, predicate: suspend () -> Boolean) {
        repeat(maxAttempts) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            if (predicate()) return
            Thread.sleep(15)
        }
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    private fun drainMainLooperForFaultInjection(passes: Int = 5) {
        repeat(passes) {
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
            Thread.sleep(10)
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        }
    }

    @Test
    fun `TEST A crash before pending event enters outbox promotes unrecorded prefs atomically on restart`() = runBlocking {
        val day = "2097-05-01"
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        val dao = db.mathDao()

        val store1 = ViewModelStore()
        val vm1 = createManagedViewModel(store1, dateProvider = { day })
        vm1.setStudyTimerDateKeyForTesting(day)
        vm1.startStudySession()
        vm1.tickStudySessionTimer(19) // < 30s threshold: committed in prefs, outbox still empty

        assertEquals(19, vm1.studySessionTimer.value.unrecordedDbSeconds)
        assertEquals(19, prefs.getInt("study_timer_unrecorded_db_sec", -1))
        assertEquals(day, prefs.getString("study_timer_active_date_key", null))
        assertTrue(vm1.readPendingStudyFlushOperations().isEmpty())
        assertNull(dao.getDailyStudyActivityByDate(day))

        // Simulate abrupt process kill while session is running (cancels viewModelScope without flushing)
        store1.clear()
        assertEquals(19, prefs.getInt("study_timer_unrecorded_db_sec", -1))

        // Cold restart on a new day still attributes recovered seconds to the original activeDateKey
        val store2 = ViewModelStore()
        val vm2 = createManagedViewModel(store2, dateProvider = { "2097-05-02" })
        assertFalse(vm2.studySessionTimer.value.isVisibleOnHome)
        assertFalse(vm2.studySessionTimer.value.isRunning)
        assertEquals(0, vm2.studySessionTimer.value.unrecordedDbSeconds)
        assertEquals(0, prefs.getInt("study_timer_unrecorded_db_sec", -1))

        awaitCondition {
            dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds == 19 &&
                vm2.readPendingStudyFlushOperations().isEmpty()
        }
        assertEquals(19, dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds)
        assertEquals(1, dao.getAllStudyTimerFlushReceipts().size)
        assertNull(dao.getDailyStudyActivityByDate("2097-05-02"))

        // Extra replay pass stays idempotent
        assertTrue(vm2.replayPendingStudyFlushJournal())
        assertEquals(19, dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds)
        assertEquals(1, dao.getAllStudyTimerFlushReceipts().size)
        store2.clear()
    }

    @Test
    fun `TEST B crash after outbox persist before Room transaction replays and commits once on restart`() = runBlocking {
        val day = "2097-05-02"
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        val dao = db.mathDao()

        MathViewModel.globalStudyFlushFaultInjectorForTesting = { StudyFlushFaultPoint.FAIL_BEFORE_TRANSACTION }
        val store1 = ViewModelStore()
        val vm1 = createManagedViewModel(store1, dateProvider = { day })
        vm1.setStudyTimerDateKeyForTesting(day)
        vm1.startStudySession()
        vm1.tickStudySessionTimer(23)
        vm1.pauseStudySession()

        drainMainLooperForFaultInjection()
        assertEquals(0, vm1.studySessionTimer.value.unrecordedDbSeconds)
        assertEquals(0, prefs.getInt("study_timer_unrecorded_db_sec", -1))

        val queued = vm1.readPendingStudyFlushOperations()
        assertEquals("Unacked op must remain durably in outbox", 1, queued.size)
        assertEquals(day, queued[0].studyDateKey)
        assertEquals(23, queued[0].pendingSeconds)
        assertNull("Room must remain untouched after pre-transaction failure", dao.getDailyStudyActivityByDate(day))
        assertTrue(dao.getAllStudyTimerFlushReceipts().isEmpty())

        // Simulate crash and restart with fault cleared
        store1.clear()
        MathViewModel.globalStudyFlushFaultInjectorForTesting = null

        val store2 = ViewModelStore()
        val vm2 = createManagedViewModel(store2, dateProvider = { day })
        awaitCondition {
            dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds == 23 &&
                vm2.readPendingStudyFlushOperations().isEmpty()
        }
        assertEquals(23, dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds)
        assertEquals(1, dao.getAllStudyTimerFlushReceipts().size)
        assertEquals(queued[0].operationId, dao.getAllStudyTimerFlushReceipts()[0].operationId)
        store2.clear()
    }

    @Test
    fun `TEST C crash after Room commit before journal ack does not double-count on restart`() = runBlocking {
        val day = "2097-05-03"
        val dao = db.mathDao()

        MathViewModel.globalStudyFlushFaultInjectorForTesting = { StudyFlushFaultPoint.FAIL_AFTER_COMMIT_BEFORE_ACK }
        val store1 = ViewModelStore()
        val vm1 = createManagedViewModel(store1, dateProvider = { day })
        vm1.setStudyTimerDateKeyForTesting(day)
        vm1.startStudySession()
        vm1.tickStudySessionTimer(35) // Crosses 30s threshold -> writes to Room, throws before ack
        vm1.pauseStudySession()

        awaitCondition {
            dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds == 35 &&
                dao.getAllStudyTimerFlushReceipts().size == 1
        }
        assertEquals(35, dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds)
        assertEquals(1, dao.getAllStudyTimerFlushReceipts().size)
        assertEquals(
            "Outbox op must stay unacked when crash happens after SQLite commit and before ack",
            1,
            vm1.readPendingStudyFlushOperations().size
        )

        // Simulate crash and restart without fault
        store1.clear()
        MathViewModel.globalStudyFlushFaultInjectorForTesting = null

        val store2 = ViewModelStore()
        val vm2 = createManagedViewModel(store2, dateProvider = { day })
        awaitCondition { vm2.readPendingStudyFlushOperations().isEmpty() }

        assertTrue(vm2.readPendingStudyFlushOperations().isEmpty())
        assertEquals(
            "Replaying an already-committed operationId must not double-count seconds",
            35,
            dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds
        )
        assertEquals(1, dao.getAllStudyTimerFlushReceipts().size)
        store2.clear()
    }

    @Test
    fun `TEST D crash during date rollover with pending previous-day seconds preserves previousDateKey`() = runBlocking {
        val prevDay = "2097-05-04"
        val nextDay = "2097-05-05"
        var currentDay = prevDay
        val dao = db.mathDao()

        MathViewModel.globalStudyFlushFaultInjectorForTesting = { StudyFlushFaultPoint.FAIL_BEFORE_TRANSACTION }
        val store1 = ViewModelStore()
        val vm1 = createManagedViewModel(store1, dateProvider = { currentDay })
        vm1.setStudyTimerDateKeyForTesting(prevDay)
        vm1.startStudySession()
        vm1.tickStudySessionTimer(22)
        vm1.pauseStudySession() // Pause would flush prevDay, so instead test rollover directly:
        store1.clear()

        // Reset and test active rollover across midnight
        app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        currentDay = prevDay
        val storeRoll = ViewModelStore()
        val vmRoll = createManagedViewModel(storeRoll, dateProvider = { currentDay })
        vmRoll.setStudyTimerDateKeyForTesting(prevDay)
        vmRoll.startStudySession()
        vmRoll.tickStudySessionTimer(22)

        // Midnight rollover occurs while flush to Room fails
        currentDay = nextDay
        vmRoll.ensureStudyTimerDateCurrent(nextDay)
        vmRoll.pauseStudySession()
        drainMainLooperForFaultInjection()

        val queued = vmRoll.readPendingStudyFlushOperations()
        assertEquals(1, queued.size)
        assertEquals(prevDay, queued[0].studyDateKey)
        assertEquals(22, queued[0].pendingSeconds)
        assertNull(dao.getDailyStudyActivityByDate(prevDay))
        assertNull(dao.getDailyStudyActivityByDate(nextDay))

        // Crash and restart on nextDay with Room available
        storeRoll.clear()
        MathViewModel.globalStudyFlushFaultInjectorForTesting = null

        val store2 = ViewModelStore()
        val vm2 = createManagedViewModel(store2, dateProvider = { nextDay })
        awaitCondition {
            dao.getDailyStudyActivityByDate(prevDay)?.studyTimeSeconds == 22 &&
                vm2.readPendingStudyFlushOperations().isEmpty()
        }
        assertEquals(22, dao.getDailyStudyActivityByDate(prevDay)?.studyTimeSeconds)
        assertNull("No previous-day seconds may leak into nextDay", dao.getDailyStudyActivityByDate(nextDay))
        store2.clear()
    }

    @Test
    fun `TEST E repeated flush calls for same operationId and simultaneous VM flushes remain idempotent`() = runBlocking {
        val day = "2097-05-05"
        val dao = db.mathDao()
        val directRepo = com.example.data.repository.MathRepository(dao)

        val op = StudyTimerFlushOperation(
            operationId = "fixed-op-test-e",
            studyDateKey = day,
            pendingSeconds = 27,
            sessionId = "session-e",
            completedSessionsDelta = 1,
            createdAtEpochMs = 1_700_000_000_000L
        )
        assertTrue("First application of opId must apply", directRepo.applyStudyTimerFlushOperation(op))
        assertFalse("Duplicate application of identical opId must be ignored", directRepo.applyStudyTimerFlushOperation(op))

        assertEquals(27, dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds)
        assertEquals(1, dao.getDailyStudyActivityByDate(day)?.practiceSessionsCompleted)
        assertEquals(1, dao.getAllStudyTimerFlushReceipts().count { it.operationId == "fixed-op-test-e" })

        // Also test rapid back-to-back flushUnrecordedStudyTime calls on ViewModel
        val store = ViewModelStore()
        val vm = createManagedViewModel(store, dateProvider = { day })
        vm.setStudyTimerDateKeyForTesting(day)
        vm.startStudySession()
        vm.tickStudySessionTimer(14)
        vm.flushUnrecordedStudyTime()
        vm.flushUnrecordedStudyTime()
        vm.pauseStudySession()

        awaitCondition {
            dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds == 41 &&
                vm.readPendingStudyFlushOperations().isEmpty()
        }
        assertEquals(41, dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds)
        assertEquals(1, dao.getDailyStudyActivityByDate(day)?.practiceSessionsCompleted)
        store.clear()
    }

    @Test
    fun `TEST F two independent flush operations on same day accumulate cleanly`() = runBlocking {
        val day = "2097-05-06"
        val dao = db.mathDao()

        val store = ViewModelStore()
        val vm = createManagedViewModel(store, dateProvider = { day })
        vm.setStudyTimerDateKeyForTesting(day)
        vm.startStudySession()
        vm.tickStudySessionTimer(32) // op1: 32s auto-flush
        vm.tickStudySessionTimer(18) // remaining 18s
        vm.finishAndSaveStudySession() // op2: 18s + 1 completed session

        awaitCondition {
            val row = dao.getDailyStudyActivityByDate(day)
            row?.studyTimeSeconds == 50 &&
                row.practiceSessionsCompleted == 1 &&
                vm.readPendingStudyFlushOperations().isEmpty()
        }
        val row = dao.getDailyStudyActivityByDate(day)
        assertNotNull(row)
        assertEquals(50, row!!.studyTimeSeconds)
        assertEquals(1, row.practiceSessionsCompleted)
        assertEquals(2, dao.getAllStudyTimerFlushReceipts().size)
        store.clear()
    }

    @Test
    fun `TEST G process recreation with multiple pending operations replays all in order`() = runBlocking {
        val day1 = "2097-05-07"
        val day2 = "2097-05-08"
        val prefs = app.getSharedPreferences("math_master_prefs", Context.MODE_PRIVATE)
        val dao = db.mathDao()

        val seededJournal = JSONArray().apply {
            put(JSONObject().apply {
                put("operationId", "g-op-1")
                put("studyDateKey", day1)
                put("pendingSeconds", 15)
                put("sessionId", "s-1")
                put("completedSessionsDelta", 0)
                put("createdAtEpochMs", 1_700_000_001_000L)
            })
            put(JSONObject().apply {
                put("operationId", "g-op-2")
                put("studyDateKey", day1)
                put("pendingSeconds", 25)
                put("sessionId", "s-1")
                put("completedSessionsDelta", 1)
                put("createdAtEpochMs", 1_700_000_002_000L)
            })
            put(JSONObject().apply {
                put("operationId", "g-op-3")
                put("studyDateKey", day2)
                put("pendingSeconds", 12)
                put("sessionId", "s-2")
                put("completedSessionsDelta", 0)
                put("createdAtEpochMs", 1_700_000_003_000L)
            })
        }
        prefs.edit().putString(MathViewModel.KEY_STUDY_FLUSH_JOURNAL, seededJournal.toString()).commit()

        val store = ViewModelStore()
        val vm = createManagedViewModel(store, dateProvider = { day2 })
        awaitCondition { vm.readPendingStudyFlushOperations().isEmpty() }

        assertEquals(40, dao.getDailyStudyActivityByDate(day1)?.studyTimeSeconds)
        assertEquals(1, dao.getDailyStudyActivityByDate(day1)?.practiceSessionsCompleted)
        assertEquals(12, dao.getDailyStudyActivityByDate(day2)?.studyTimeSeconds)
        assertEquals(0, dao.getDailyStudyActivityByDate(day2)?.practiceSessionsCompleted)
        assertEquals(3, dao.getAllStudyTimerFlushReceipts().size)
        store.clear()
    }

    @Test
    fun `TEST H mid-transaction SQLite failure rolls back daily_study_activity and receipt then recovers on retry`() = runBlocking {
        val day = "2097-05-09"
        val dao = db.mathDao()
        val directRepo = com.example.data.repository.MathRepository(dao)

        // Commit baseline 10s first via standalone repository
        assertTrue(
            directRepo.applyStudyTimerFlushOperation(
                StudyTimerFlushOperation("h-base-op", day, 10, "s-h0", 0, 1_700_000_010_000L)
            )
        )
        assertEquals(10, dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds)

        // Now inject SQLite failure inside transaction right after daily_study_activity upsert
        MathViewModel.globalStudyFlushFaultInjectorForTesting = { StudyFlushFaultPoint.FAIL_INSIDE_TRANSACTION_AFTER_UPSERT }
        val store1 = ViewModelStore()
        val vm1 = createManagedViewModel(store1, dateProvider = { day })
        vm1.setStudyTimerDateKeyForTesting(day)
        vm1.startStudySession()
        vm1.tickStudySessionTimer(29)
        vm1.pauseStudySession()

        drainMainLooperForFaultInjection()

        // SQLite transaction rollback must preserve baseline 10s and still keep op in outbox
        assertEquals(
            "Failed Room @Transaction must roll back daily_study_activity to baseline 10s",
            10,
            dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds
        )
        assertEquals(1, dao.getAllStudyTimerFlushReceipts().size)
        assertEquals(1, vm1.readPendingStudyFlushOperations().size)
        assertEquals(29, vm1.readPendingStudyFlushOperations()[0].pendingSeconds)
        store1.clear()

        // Clear fault and restart ViewModel -> replays journal and commits cleanly to 39s
        MathViewModel.globalStudyFlushFaultInjectorForTesting = null
        val store2 = ViewModelStore()
        val vm2 = createManagedViewModel(store2, dateProvider = { day })
        awaitCondition {
            dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds == 39 &&
                vm2.readPendingStudyFlushOperations().isEmpty()
        }
        assertTrue(vm2.readPendingStudyFlushOperations().isEmpty())
        assertEquals(39, dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds)
        assertEquals(2, dao.getAllStudyTimerFlushReceipts().size)
        store2.clear()
    }

    @Test
    fun `TEST I midnight rollover while previous-day event awaits ack preserves previous and new day counts`() = runBlocking {
        val day1 = "2097-05-10"
        val day2 = "2097-05-11"
        var currentDay = day1
        val dao = db.mathDao()

        // Block day1 operations initially so op1 remains unacked in journal across midnight
        MathViewModel.globalStudyFlushFaultInjectorForTesting = { op ->
            if (op.studyDateKey == day1) StudyFlushFaultPoint.FAIL_BEFORE_TRANSACTION else StudyFlushFaultPoint.NONE
        }

        val store1 = ViewModelStore()
        val vm1 = createManagedViewModel(store1, dateProvider = { currentDay })
        vm1.setStudyTimerDateKeyForTesting(day1)
        vm1.startStudySession()
        vm1.tickStudySessionTimer(34) // op1 (day1, 34s) queued & blocked
        vm1.tickStudySessionTimer(12) // 12s unrecorded on day1
        drainMainLooperForFaultInjection()
        assertEquals(1, vm1.readPendingStudyFlushOperations().size)

        // Midnight passes -> 16s ticked on day2 promotes day1's 12s into op2(day1)
        currentDay = day2
        vm1.tickStudySessionTimer(16)
        vm1.finishAndSaveStudySession() // op3(day2, 16s, completedSessionsDelta=1)
        drainMainLooperForFaultInjection()

        val pending = vm1.readPendingStudyFlushOperations()
        assertEquals("op1(day1), op2(day1), op3(day2) must all be preserved in FIFO order", 3, pending.size)
        assertEquals(listOf(day1, day1, day2), pending.map { it.studyDateKey })
        assertEquals(listOf(34, 12, 16), pending.map { it.pendingSeconds })

        // Crash and restart on day2 with fault cleared
        store1.clear()
        MathViewModel.globalStudyFlushFaultInjectorForTesting = null

        val store2 = ViewModelStore()
        val vm2 = createManagedViewModel(store2, dateProvider = { day2 })
        awaitCondition { vm2.readPendingStudyFlushOperations().isEmpty() }

        assertEquals(46, dao.getDailyStudyActivityByDate(day1)?.studyTimeSeconds)
        assertEquals(0, dao.getDailyStudyActivityByDate(day1)?.practiceSessionsCompleted)
        assertEquals(16, dao.getDailyStudyActivityByDate(day2)?.studyTimeSeconds)
        assertEquals(1, dao.getDailyStudyActivityByDate(day2)?.practiceSessionsCompleted)
        assertEquals(3, dao.getAllStudyTimerFlushReceipts().size)
        store2.clear()
    }

    @Test
    fun `TEST J previously acknowledged events never replay after restart`() = runBlocking {
        val day = "2097-05-12"
        val dao = db.mathDao()
        val directRepo = com.example.data.repository.MathRepository(dao)

        val store1 = ViewModelStore()
        val vm1 = createManagedViewModel(store1, dateProvider = { day })
        vm1.setStudyTimerDateKeyForTesting(day)
        vm1.startStudySession()
        vm1.tickStudySessionTimer(21)
        vm1.finishAndSaveStudySession()

        awaitCondition {
            dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds == 21 &&
                vm1.readPendingStudyFlushOperations().isEmpty()
        }
        val receipts = dao.getAllStudyTimerFlushReceipts()
        assertEquals(1, receipts.size)
        val committedOp = receipts[0]
        assertTrue(vm1.readPendingStudyFlushOperations().isEmpty())
        store1.clear()

        // Cold restart must keep journal empty and never re-apply
        val store2 = ViewModelStore()
        val vm2 = createManagedViewModel(store2, dateProvider = { day })
        drainMainLooperForFaultInjection()
        assertTrue(vm2.readPendingStudyFlushOperations().isEmpty())
        assertEquals(21, dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds)
        assertEquals(1, dao.getDailyStudyActivityByDate(day)?.practiceSessionsCompleted)

        // Even if the exact operation is forcefully submitted again, Room receipt rejects duplicate application
        val reapplied = directRepo.applyStudyTimerFlushOperation(
            StudyTimerFlushOperation(
                operationId = committedOp.operationId,
                studyDateKey = committedOp.studyDateKey,
                pendingSeconds = committedOp.pendingSeconds,
                sessionId = committedOp.sessionId,
                completedSessionsDelta = committedOp.completedSessionsDelta,
                createdAtEpochMs = committedOp.createdAtEpochMs
            )
        )
        assertFalse(reapplied)
        assertEquals(21, dao.getDailyStudyActivityByDate(day)?.studyTimeSeconds)
        assertEquals(1, dao.getDailyStudyActivityByDate(day)?.practiceSessionsCompleted)
        store2.clear()
    }

    @Test
    fun `MIGRATION populated Room v9 to v10 preserves all rows, rolls back on mid-migration failure, and survives restart`() = runBlocking<Unit> {
        val dbName = "bug86_migration_v9_v10_test.db"
        app.deleteDatabase(dbName)

        // 1. Create authentic v9 schema using v1 baseline + MIGRATION_1_2..MIGRATION_8_9 and seed populated data
        val v1Callback = object : SupportSQLiteOpenHelper.Callback(1) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `bookmarks` (`id` TEXT NOT NULL, `category` TEXT NOT NULL, `titleBn` TEXT NOT NULL, `subtitleBn` TEXT NOT NULL, `detailBn` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `quiz_scores` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `topic` TEXT NOT NULL, `topicTitleBn` TEXT NOT NULL, `score` INTEGER NOT NULL, `totalQuestions` INTEGER NOT NULL, `percentage` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `user_progress` (`itemId` TEXT NOT NULL, `isLearned` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`itemId`))")
                db.execSQL("INSERT INTO `bookmarks` VALUES ('bm_v9', 'FORMULA', 'সূত্র v9', 'উপশিরোনাম', 'বিস্তারিত', 1700000100)")
                db.execSQL("INSERT INTO `user_progress` VALUES ('item_v9', 1, 1700000200)")
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(app)
                .name(dbName)
                .callback(v1Callback)
                .build()
        )
        val rawDb = helper.writableDatabase
        MathDatabase.MIGRATION_1_2.migrate(rawDb)
        MathDatabase.MIGRATION_2_3.migrate(rawDb)
        MathDatabase.MIGRATION_3_4.migrate(rawDb)
        MathDatabase.MIGRATION_4_5.migrate(rawDb)
        MathDatabase.MIGRATION_5_6.migrate(rawDb)
        MathDatabase.MIGRATION_6_7.migrate(rawDb)
        MathDatabase.MIGRATION_7_8.migrate(rawDb)
        MathDatabase.MIGRATION_8_9.migrate(rawDb)
        rawDb.execSQL("INSERT INTO `daily_study_activity` VALUES ('2026-03-10', 25, 21, 4, 425, 2, 3, 20, 30, 1700000900)")
        rawDb.version = 9

        // 2. Simulate an aborted/failed MIGRATION_9_10 inside a SQLite transaction -> must roll back cleanly
        rawDb.beginTransaction()
        try {
            MathDatabase.MIGRATION_9_10.migrate(rawDb)
            // Intentionally omit setTransactionSuccessful() to simulate crash/failure mid-migration
        } finally {
            rawDb.endTransaction()
        }
        assertEquals(9, rawDb.version)
        rawDb.query("SELECT name FROM sqlite_master WHERE type='table' AND name='study_timer_flush_receipts'").use { c ->
            assertEquals("Failed MIGRATION_9_10 transaction must roll back table creation", 0, c.count)
        }
        rawDb.query("SELECT studyTimeSeconds, practiceSessionsCompleted FROM daily_study_activity WHERE dateKey='2026-03-10'").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals(425, c.getInt(0))
            assertEquals(3, c.getInt(1))
        }
        helper.close()

        // 3. Reopen with Room v10 -> MIGRATION_9_10 runs cleanly, preserves existing data, supports receipts
        val room1 = Room.databaseBuilder(app, MathDatabase::class.java, dbName)
            .addMigrations(
                MathDatabase.MIGRATION_1_2,
                MathDatabase.MIGRATION_2_3,
                MathDatabase.MIGRATION_3_4,
                MathDatabase.MIGRATION_4_5,
                MathDatabase.MIGRATION_5_6,
                MathDatabase.MIGRATION_6_7,
                MathDatabase.MIGRATION_7_8,
                MathDatabase.MIGRATION_8_9,
                MathDatabase.MIGRATION_9_10
            )
            .allowMainThreadQueries()
            .build()

        val sqlite1 = room1.openHelper.writableDatabase
        assertEquals(10, sqlite1.version)
        sqlite1.query("PRAGMA integrity_check").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("ok", c.getString(0).lowercase())
        }
        val dao1 = room1.mathDao()
        assertEquals(1, dao1.getAllBookmarks().first().size)
        assertEquals(1, dao1.getAllProgress().first().size)
        val migratedDay = dao1.getDailyStudyActivityByDate("2026-03-10")
        assertNotNull(migratedDay)
        assertEquals(425, migratedDay!!.studyTimeSeconds)
        assertEquals(3, migratedDay.practiceSessionsCompleted)

        // Write a new BUG-86 flush on top of migrated data and verify restart durability
        val repo1 = com.example.data.repository.MathRepository(dao1)
        assertTrue(
            repo1.applyStudyTimerFlushOperation(
                StudyTimerFlushOperation("mig-op-1", "2026-03-10", 30, "s-mig", 1, 1700001000L)
            )
        )
        room1.close()

        val room2 = Room.databaseBuilder(app, MathDatabase::class.java, dbName)
            .addMigrations(
                MathDatabase.MIGRATION_1_2,
                MathDatabase.MIGRATION_2_3,
                MathDatabase.MIGRATION_3_4,
                MathDatabase.MIGRATION_4_5,
                MathDatabase.MIGRATION_5_6,
                MathDatabase.MIGRATION_6_7,
                MathDatabase.MIGRATION_7_8,
                MathDatabase.MIGRATION_8_9,
                MathDatabase.MIGRATION_9_10
            )
            .allowMainThreadQueries()
            .build()

        val dao2 = room2.mathDao()
        val postRestartDay = dao2.getDailyStudyActivityByDate("2026-03-10")
        assertNotNull(postRestartDay)
        assertEquals(455, postRestartDay!!.studyTimeSeconds)
        assertEquals(4, postRestartDay.practiceSessionsCompleted)
        assertEquals(1, dao2.getAllStudyTimerFlushReceipts().size)
        assertFalse(
            "Idempotency receipt must survive Room close/reopen",
            com.example.data.repository.MathRepository(dao2).applyStudyTimerFlushOperation(
                StudyTimerFlushOperation("mig-op-1", "2026-03-10", 30, "s-mig", 1, 1700001000L)
            )
        )
        room2.close()
        app.deleteDatabase(dbName)
    }
}
