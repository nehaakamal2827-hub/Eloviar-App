package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skincare_products")
data class SkincareProduct(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val brand: String,
    val category: String, // Cleanser, Toner, Serum, Moisturizer, Sunscreen, Treatment, Other
    val isOpened: Boolean = false,
    val openedDate: Long? = null, // timestamp in ms
    val expirationMonths: Int? = 12, // e.g., 6, 12, 24
    val notes: String = "",
    val rating: Float = 0f,
    val usageFrequency: String = "Daily" // Daily, Twice Daily, Weekly, AM, PM
)

@Entity(tableName = "skincare_habits")
data class SkincareHabit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val timeOfDay: String, // Morning, Night, All Day
    val isDefault: Boolean = false
)

@Entity(tableName = "habit_logs")
data class HabitLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    val date: String, // YYYY-MM-DD
    val isCompleted: Boolean = true
)

@Entity(tableName = "progress_logs")
data class ProgressLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val notes: String,
    val skinRating: Int, // 1 to 5
    val photoUri: String?, // Can be a preset name like "preset_glow", "preset_calm" or a file path
    val hydrationLevel: String, // Dry, Normal, Hydrated
    val oilinessLevel: String, // Dry, Balanced, Oily
    val rednessLevel: String // None, Mild, Severe
)

@Entity(tableName = "skincare_settings")
data class SkincareSettings(
    @PrimaryKey val id: Int = 1, // Fixed ID for single row settings
    val morningReminderTime: String = "08:00 AM",
    val nightReminderTime: String = "09:00 PM",
    val remindersEnabled: Boolean = true,
    val skinTypeGoal: String = "Radiant & Clear",
    val primaryConcern: String = "Hydration"
)
