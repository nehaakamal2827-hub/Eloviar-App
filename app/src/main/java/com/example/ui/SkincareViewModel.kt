package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.SkincareDatabase
import com.example.data.model.*
import com.example.data.repository.SkincareRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SkincareViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SkincareDatabase.getDatabase(application, viewModelScope)
    private val repository = SkincareRepository(database.skincareDao())

    // Date formatting
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // UI Selected Date state
    private val _selectedDate = MutableStateFlow(dateFormat.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Products Flow
    val products: StateFlow<List<SkincareProduct>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Habits Flow
    val habits: StateFlow<List<SkincareHabit>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Logs Flow (useful for calculating streaks)
    val allLogs: StateFlow<List<HabitLog>> = repository.allHabitLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active logs for the selected date
    val habitLogsForSelectedDate: StateFlow<List<HabitLog>> = _selectedDate
        .flatMapLatest { date -> repository.getLogsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Progress photo logs Flow
    val progressLogs: StateFlow<List<ProgressLog>> = repository.allProgressLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Settings Flow (with backup defaults)
    val settings: StateFlow<SkincareSettings> = repository.settings
        .map { it ?: SkincareSettings() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SkincareSettings()
        )

    // --- Derived Statistics ---

    // Calculate Today's Habits Completion Rate
    val todayCompletionRate: StateFlow<Float> = combine(habits, habitLogsForSelectedDate) { listHabits, activeLogs ->
        if (listHabits.isEmpty()) 0f
        else {
            val activeIds = activeLogs.map { it.habitId }.toSet()
            val totalForToday = listHabits.size
            val completed = listHabits.count { it.id in activeIds }
            completed.toFloat() / totalForToday
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    // Calculate Current Skincare Streak (Consecutive days with at least one habit logged)
    val skincareStreak: StateFlow<Int> = allLogs.map { logs ->
        if (logs.isEmpty()) return@map 0
        val uniqueLogDates = logs.map { it.date }.toSet()
            .map { dateFormat.parse(it) }
            .filterNotNull()
            .sortedDescending()

        if (uniqueLogDates.isEmpty()) return@map 0

        // Check if today or yesterday is completed to start the streak
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.time

        // Format dates to ignore time components
        val todayStr = dateFormat.format(today)
        val yesterdayStr = dateFormat.format(yesterday)

        val completedDatesStr = logs.map { it.date }.toSet()
        if (!completedDatesStr.contains(todayStr) && !completedDatesStr.contains(yesterdayStr)) {
            return@map 0
        }

        var streak = 0
        val checkCalendar = Calendar.getInstance()
        // If today is completed, start check from today. Otherwise start from yesterday.
        if (!completedDatesStr.contains(todayStr)) {
            checkCalendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            val dateToCheck = dateFormat.format(checkCalendar.time)
            if (completedDatesStr.contains(dateToCheck)) {
                streak++
                checkCalendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        streak
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Actions ---

    fun changeSelectedDate(dateStr: String) {
        _selectedDate.value = dateStr
    }

    fun selectDateOffset(daysOffset: Int) {
        val calendar = Calendar.getInstance()
        try {
            val currentDate = dateFormat.parse(_selectedDate.value) ?: Date()
            calendar.time = currentDate
            calendar.add(Calendar.DAY_OF_YEAR, daysOffset)
            _selectedDate.value = dateFormat.format(calendar.time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Toggle Habit Status
    fun toggleHabit(habitId: Int, isChecked: Boolean) {
        viewModelScope.launch {
            repository.checkHabit(habitId, _selectedDate.value, isChecked)
        }
    }

    // Add custom habit
    fun addHabit(name: String, timeOfDay: String) {
        viewModelScope.launch {
            repository.insertHabit(SkincareHabit(name = name, timeOfDay = timeOfDay, isDefault = false))
        }
    }

    // Delete custom habit
    fun deleteHabit(habit: SkincareHabit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // Add Product to shelf
    fun addProduct(
        name: String,
        brand: String,
        category: String,
        isOpened: Boolean,
        openedDate: Long?,
        expirationMonths: Int,
        notes: String,
        rating: Float,
        usageFrequency: String
    ) {
        viewModelScope.launch {
            val newProd = SkincareProduct(
                name = name,
                brand = brand,
                category = category,
                isOpened = isOpened,
                openedDate = if (isOpened) (openedDate ?: System.currentTimeMillis()) else null,
                expirationMonths = expirationMonths,
                notes = notes,
                rating = rating,
                usageFrequency = usageFrequency
            )
            repository.insertProduct(newProd)
        }
    }

    // Toggle product opened status
    fun toggleProductOpened(product: SkincareProduct) {
        viewModelScope.launch {
            val updated = product.copy(
                isOpened = !product.isOpened,
                openedDate = if (!product.isOpened) System.currentTimeMillis() else null
            )
            repository.updateProduct(updated)
        }
    }

    // Delete a product from cabinet
    fun deleteProduct(product: SkincareProduct) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // Add Progress Diary Log
    fun addProgressLog(
        notes: String,
        skinRating: Int,
        photoUri: String?,
        hydrationLevel: String,
        oilinessLevel: String,
        rednessLevel: String
    ) {
        viewModelScope.launch {
            val todayStr = dateFormat.format(Date())
            val log = ProgressLog(
                date = todayStr,
                notes = notes,
                skinRating = skinRating,
                photoUri = photoUri,
                hydrationLevel = hydrationLevel,
                oilinessLevel = oilinessLevel,
                rednessLevel = rednessLevel
            )
            repository.insertProgressLog(log)
        }
    }

    // Delete a progress log
    fun deleteProgressLog(log: ProgressLog) {
        viewModelScope.launch {
            repository.deleteProgressLog(log)
        }
    }

    // Update global skincare settings
    fun updateSettings(
        morningReminderTime: String,
        nightReminderTime: String,
        remindersEnabled: Boolean,
        skinTypeGoal: String,
        primaryConcern: String
    ) {
        viewModelScope.launch {
            repository.updateSettings(
                SkincareSettings(
                    id = 1,
                    morningReminderTime = morningReminderTime,
                    nightReminderTime = nightReminderTime,
                    remindersEnabled = remindersEnabled,
                    skinTypeGoal = skinTypeGoal,
                    primaryConcern = primaryConcern
                )
            )
        }
    }
}
