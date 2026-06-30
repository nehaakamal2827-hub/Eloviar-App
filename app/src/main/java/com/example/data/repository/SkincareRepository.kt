package com.example.data.repository

import com.example.data.dao.SkincareDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class SkincareRepository(private val skincareDao: SkincareDao) {

    // Products
    val allProducts: Flow<List<SkincareProduct>> = skincareDao.getAllProducts()

    suspend fun insertProduct(product: SkincareProduct) {
        skincareDao.insertProduct(product)
    }

    suspend fun updateProduct(product: SkincareProduct) {
        skincareDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: SkincareProduct) {
        skincareDao.deleteProduct(product)
    }

    // Habits
    val allHabits: Flow<List<SkincareHabit>> = skincareDao.getAllHabits()

    suspend fun insertHabit(habit: SkincareHabit) {
        skincareDao.insertHabit(habit)
    }

    suspend fun deleteHabit(habit: SkincareHabit) {
        skincareDao.deleteHabit(habit)
    }

    // Habit Logs
    val allHabitLogs: Flow<List<HabitLog>> = skincareDao.getAllHabitLogs()

    fun getLogsForDate(date: String): Flow<List<HabitLog>> {
        return skincareDao.getLogsForDate(date)
    }

    suspend fun checkHabit(habitId: Int, date: String, completed: Boolean) {
        if (completed) {
            skincareDao.insertHabitLog(HabitLog(habitId = habitId, date = date))
        } else {
            skincareDao.deleteHabitLog(habitId, date)
        }
    }

    // Progress Logs
    val allProgressLogs: Flow<List<ProgressLog>> = skincareDao.getAllProgressLogs()

    suspend fun insertProgressLog(log: ProgressLog) {
        skincareDao.insertProgressLog(log)
    }

    suspend fun deleteProgressLog(log: ProgressLog) {
        skincareDao.deleteProgressLog(log)
    }

    // Settings
    val settings: Flow<SkincareSettings?> = skincareDao.getSettings()

    suspend fun updateSettings(newSettings: SkincareSettings) {
        skincareDao.insertSettings(newSettings)
    }
}
