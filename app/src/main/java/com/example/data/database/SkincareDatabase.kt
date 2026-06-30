package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.SkincareDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SkincareProduct::class,
        SkincareHabit::class,
        HabitLog::class,
        ProgressLog::class,
        SkincareSettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SkincareDatabase : RoomDatabase() {

    abstract fun skincareDao(): SkincareDao

    companion object {
        @Volatile
        private var INSTANCE: SkincareDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SkincareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkincareDatabase::class.java,
                    "skincare_database"
                )
                .addCallback(SkincareDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SkincareDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.skincareDao())
                }
            }
        }

        suspend fun populateDatabase(dao: SkincareDao) {
            // Default morning habits
            val morningHabits = listOf(
                SkincareHabit(name = "Gentle Cleanser 🧼", timeOfDay = "Morning", isDefault = true),
                SkincareHabit(name = "Hydrating Toner 💧", timeOfDay = "Morning", isDefault = true),
                SkincareHabit(name = "Vitamin C Serum 🍊", timeOfDay = "Morning", isDefault = true),
                SkincareHabit(name = "Moisturizer 🧴", timeOfDay = "Morning", isDefault = true),
                SkincareHabit(name = "Sunscreen SPF 50+ ☀️", timeOfDay = "Morning", isDefault = true),
                SkincareHabit(name = "Drink 500ml Water 🥛", timeOfDay = "Morning", isDefault = true)
            )

            // Default night habits
            val nightHabits = listOf(
                SkincareHabit(name = "Double Cleanse (Oil + Water)", timeOfDay = "Night", isDefault = true),
                SkincareHabit(name = "Exfoliator / Retinol 🌙", timeOfDay = "Night", isDefault = true),
                SkincareHabit(name = "Nourishing Night Cream 🧴", timeOfDay = "Night", isDefault = true),
                SkincareHabit(name = "Apply Eye Cream 👁️", timeOfDay = "Night", isDefault = true),
                SkincareHabit(name = "Sleep 8 Hours 💤", timeOfDay = "Night", isDefault = true)
            )

            dao.insertHabits(morningHabits + nightHabits)

            // Default settings
            dao.insertSettings(
                SkincareSettings(
                    id = 1,
                    morningReminderTime = "08:00 AM",
                    nightReminderTime = "09:30 PM",
                    remindersEnabled = true,
                    skinTypeGoal = "Hydrated & Glowing",
                    primaryConcern = "Acne & Texture Prevention"
                )
            )

            // Add some default initial skincare products in the cabinet
            val defaultProducts = listOf(
                SkincareProduct(
                    name = "Gentle Hydrating Cleanser",
                    brand = "CeraVe",
                    category = "Cleanser",
                    isOpened = true,
                    openedDate = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000), // 30 days ago
                    expirationMonths = 12,
                    notes = "Very gentle, doesn't strip skin barrier. Perfect for double cleanse.",
                    rating = 4.5f,
                    usageFrequency = "Daily"
                ),
                SkincareProduct(
                    name = "Daily Facial Moisturizing Lotion SPF 30",
                    brand = "CeraVe",
                    category = "Sunscreen",
                    isOpened = true,
                    openedDate = System.currentTimeMillis() - (10L * 24 * 60 * 60 * 1000), // 10 days ago
                    expirationMonths = 12,
                    notes = "Good everyday SPF, non-greasy.",
                    rating = 4.0f,
                    usageFrequency = "AM only"
                ),
                SkincareProduct(
                    name = "Hyaluronic Acid 2% + B5",
                    brand = "The Ordinary",
                    category = "Serum",
                    isOpened = false,
                    openedDate = null,
                    expirationMonths = 6,
                    notes = "Backup serum for intensive hydration.",
                    rating = 4.8f,
                    usageFrequency = "Daily"
                )
            )

            for (prod in defaultProducts) {
                dao.insertProduct(prod)
            }

            // Let's add some default progress photo log entries for a beautiful starter graph/gallery
            val defaultLogs = listOf(
                ProgressLog(
                    date = getDaysAgo(5),
                    notes = "Skin felt a bit dry this morning. Added extra hydrating toner. No new break outs.",
                    skinRating = 3,
                    photoUri = "preset_neutral",
                    hydrationLevel = "Dry",
                    oilinessLevel = "Balanced",
                    rednessLevel = "Mild"
                ),
                ProgressLog(
                    date = getDaysAgo(3),
                    notes = "Hydration level is much better today. Glowing after using Cerave lotion.",
                    skinRating = 4,
                    photoUri = "preset_glowing",
                    hydrationLevel = "Hydrated",
                    oilinessLevel = "Balanced",
                    rednessLevel = "None"
                ),
                ProgressLog(
                    date = getDaysAgo(1),
                    notes = "Skin looks amazing! Retinol night went well, very minimal irritation.",
                    skinRating = 5,
                    photoUri = "preset_radiant",
                    hydrationLevel = "Hydrated",
                    oilinessLevel = "Balanced",
                    rednessLevel = "None"
                )
            )

            for (log in defaultLogs) {
                dao.insertProgressLog(log)
            }
        }

        private fun getDaysAgo(days: Int): String {
            val msAgo = System.currentTimeMillis() - (days.toLong() * 24 * 60 * 60 * 1000)
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            return sdf.format(java.util.Date(msAgo))
        }
    }
}
