package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SkincareDao {

    // --- Products ---
    @Query("SELECT * FROM skincare_products ORDER BY brand ASC, name ASC")
    fun getAllProducts(): Flow<List<SkincareProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: SkincareProduct)

    @Update
    suspend fun updateProduct(product: SkincareProduct)

    @Delete
    suspend fun deleteProduct(product: SkincareProduct)


    // --- Habits ---
    @Query("SELECT * FROM skincare_habits")
    fun getAllHabits(): Flow<List<SkincareHabit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: SkincareHabit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<SkincareHabit>)

    @Delete
    suspend fun deleteHabit(habit: SkincareHabit)


    // --- Habit Logs ---
    @Query("SELECT * FROM habit_logs")
    fun getAllHabitLogs(): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs WHERE date = :date")
    fun getLogsForDate(date: String): Flow<List<HabitLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLog)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND date = :date")
    suspend fun deleteHabitLog(habitId: Int, date: String)


    // --- Progress Logs ---
    @Query("SELECT * FROM progress_logs ORDER BY date DESC")
    fun getAllProgressLogs(): Flow<List<ProgressLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressLog(log: ProgressLog)

    @Delete
    suspend fun deleteProgressLog(log: ProgressLog)


    // --- Settings ---
    @Query("SELECT * FROM skincare_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<SkincareSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SkincareSettings)
}
