package com.example.ui

import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.ProgressLog
import com.example.data.model.SkincareHabit
import com.example.data.model.SkincareProduct
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: SkincareViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(0) }

    // States from VM
    val products by viewModel.products.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val logsForDate by viewModel.habitLogsForSelectedDate.collectAsStateWithLifecycle()
    val progressLogs by viewModel.progressLogs.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val todayCompletionRate by viewModel.todayCompletionRate.collectAsStateWithLifecycle()
    val streak by viewModel.skincareStreak.collectAsStateWithLifecycle()

    // Dialog trigger states
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddDiaryDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(if (currentTab == 0) Icons.Filled.Spa else Icons.Outlined.Spa, contentDescription = "Routines") },
                    label = { Text("Routines", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_tab_routines")
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(if (currentTab == 1) Icons.Filled.Inbox else Icons.Outlined.Inbox, contentDescription = "Shelf") },
                    label = { Text("Shelf", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_tab_shelf")
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { Icon(if (currentTab == 2) Icons.Filled.PhotoLibrary else Icons.Outlined.PhotoLibrary, contentDescription = "Diary") },
                    label = { Text("Diary", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_tab_diary")
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = { Icon(if (currentTab == 3) Icons.Filled.Analytics else Icons.Outlined.Analytics, contentDescription = "Insights") },
                    label = { Text("Insights", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_tab_insights")
                )
            }
        },
        floatingActionButton = {
            when (currentTab) {
                0 -> {
                    FloatingActionButton(
                        onClick = { showAddHabitDialog = true },
                        modifier = Modifier.testTag("fab_add_habit"),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Habit")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("New Habit", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                1 -> {
                    FloatingActionButton(
                        onClick = { showAddProductDialog = true },
                        modifier = Modifier.testTag("fab_add_product"),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Product")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Product", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                2 -> {
                    FloatingActionButton(
                        onClick = { showAddDiaryDialog = true },
                        modifier = Modifier.testTag("fab_add_diary"),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Create, contentDescription = "Log Skin Today")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Today", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main Top Branding Header
            BrandingHeader(streak = streak, todayCompletionRate = todayCompletionRate)

            // Content based on selected Tab
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "tab_transitions"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> RoutinesTab(
                            viewModel = viewModel,
                            selectedDate = selectedDate,
                            habits = habits,
                            logs = logsForDate
                        )
                        1 -> ShelfTab(
                            viewModel = viewModel,
                            products = products
                        )
                        2 -> DiaryTab(
                            viewModel = viewModel,
                            progressLogs = progressLogs
                        )
                        3 -> InsightsTab(
                            viewModel = viewModel,
                            products = products,
                            streak = streak,
                            progressLogs = progressLogs,
                            settings = settings
                        )
                    }
                }
            }
        }

        // Dialogs
        if (showAddHabitDialog) {
            AddHabitDialog(
                onDismiss = { showAddHabitDialog = false },
                onSave = { name, time ->
                    viewModel.addHabit(name, time)
                    showAddHabitDialog = false
                }
            )
        }

        if (showAddProductDialog) {
            AddProductDialog(
                onDismiss = { showAddProductDialog = false },
                onSave = { name, brand, category, isOpened, openDate, expiryMonths, notes, rating, freq ->
                    viewModel.addProduct(name, brand, category, isOpened, openDate, expiryMonths, notes, rating, freq)
                    showAddProductDialog = false
                }
            )
        }

        if (showAddDiaryDialog) {
            AddDiaryDialog(
                onDismiss = { showAddDiaryDialog = false },
                onSave = { notes, rating, photoUri, hydration, oiliness, redness ->
                    viewModel.addProgressLog(notes, rating, photoUri, hydration, oiliness, redness)
                    showAddDiaryDialog = false
                }
            )
        }
    }
}

// --- Top Branding Header ---
@Composable
fun BrandingHeader(streak: Int, todayCompletionRate: Float) {
    val sdf = remember { SimpleDateFormat("EEEE, MMMM d", Locale.US) }
    val todayStr = remember { sdf.format(Date()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Minimal Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = todayStr.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "GlowJournal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                )
            }
            // Streak Circle Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { /* Streak Info */ },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text("🔥", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "$streak",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress Section (matching Tailwind class "bg-[#d2e8d1] p-6 rounded-[32px]")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val percentage = (todayCompletionRate * 100).toInt()
                    Text(
                        text = "$percentage% completed",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }

                // Clean Minimalist Progress Indicator matching SVG pattern
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { todayCompletionRate },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary, // dark forest green
                        trackColor = Color.White.copy(alpha = 0.4f),
                        strokeWidth = 5.dp
                    )
                    Text(
                        text = "${(todayCompletionRate * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

// ================= TAB 1: ROUTINES (HABITS) =================
@Composable
fun RoutinesTab(
    viewModel: SkincareViewModel,
    selectedDate: String,
    habits: List<SkincareHabit>,
    logs: List<com.example.data.model.HabitLog>
) {
    val completedIds = logs.map { it.habitId }.toSet()
    val morningHabits = habits.filter { it.timeOfDay == "Morning" || it.timeOfDay == "All Day" }
    val nightHabits = habits.filter { it.timeOfDay == "Night" || it.timeOfDay == "All Day" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date Selector Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { viewModel.selectDateOffset(-1) },
                        modifier = Modifier.testTag("btn_prev_day")
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Prev Day")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatDateDisplay(selectedDate),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isDateToday(selectedDate)) "TODAY" else "HISTORIC LOG",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDateToday(selectedDate)) MaterialTheme.colorScheme.secondary else Color.Gray,
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(
                        onClick = { viewModel.selectDateOffset(1) },
                        modifier = Modifier.testTag("btn_next_day")
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next Day")
                    }
                }
            }
        }

        // Morning Routines Card
        item {
            RoutineCardSection(
                title = "Morning Routine ☀️",
                description = "Prep, protect, and hydrate skin for the day",
                habits = morningHabits,
                completedIds = completedIds,
                onToggle = { id, done -> viewModel.toggleHabit(id, done) },
                onDelete = { habit -> viewModel.deleteHabit(habit) }
            )
        }

        // Night Routines Card
        item {
            RoutineCardSection(
                title = "Nighttime Routine 🌙",
                description = "Cleanse, repair, and regenerate overnight",
                habits = nightHabits,
                completedIds = completedIds,
                onToggle = { id, done -> viewModel.toggleHabit(id, done) },
                onDelete = { habit -> viewModel.deleteHabit(habit) }
            )
        }

        // General tips card for routine compliance
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Consistency beats high-potency. Complete morning protection (SPF) and evening restoration (Cleansing) to retain skin barrier integrity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineCardSection(
    title: String,
    description: String,
    habits: List<SkincareHabit>,
    completedIds: Set<Int>,
    onToggle: (Int, Boolean) -> Unit,
    onDelete: (SkincareHabit) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            if (habits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No habits in this routine yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    habits.forEach { habit ->
                        val isChecked = completedIds.contains(habit.id)
                        val icon = when {
                            habit.name.contains("cleanse", ignoreCase = true) || habit.name.contains("cleanser", ignoreCase = true) -> Icons.Outlined.WaterDrop
                            habit.name.contains("serum", ignoreCase = true) || habit.name.contains("vitamin", ignoreCase = true) -> Icons.Outlined.BubbleChart
                            habit.name.contains("spf", ignoreCase = true) || habit.name.contains("sun", ignoreCase = true) || habit.name.contains("sunscreen", ignoreCase = true) -> Icons.Outlined.WbSunny
                            habit.name.contains("moisturizer", ignoreCase = true) || habit.name.contains("cream", ignoreCase = true) || habit.name.contains("hydrate", ignoreCase = true) -> Icons.Outlined.Opacity
                            else -> Icons.Outlined.Spa
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (isChecked) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f) else Color.White,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onToggle(habit.id, !isChecked) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Muted background icon container
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.tertiary,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = habit.name,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column {
                                    Text(
                                        text = habit.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (isChecked) "Completed" else "Apply today",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                }
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!habit.isDefault) {
                                    IconButton(
                                        onClick = { onDelete(habit) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete Habit",
                                            tint = Color.Gray.copy(alpha = 0.4f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                
                                // Clean minimalist check circle / empty square button
                                if (isChecked) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .border(
                                                width = 2.dp,
                                                color = Color(0xFFC1C9BE),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ================= TAB 2: PRODUCT CABINET (SHELF) =================
@Composable
fun ShelfTab(
    viewModel: SkincareViewModel,
    products: List<SkincareProduct>
) {
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Cleanser", "Toner", "Serum", "Moisturizer", "Sunscreen", "Treatment", "Other")

    val filteredProducts = if (selectedCategoryFilter == "All") {
        products
    } else {
        products.filter { it.category == selectedCategoryFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Horizontal filter chips
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategoryFilter),
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            divider = {},
            indicator = {},
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            categories.forEachIndexed { index, cat ->
                val isSelected = cat == selectedCategoryFilter
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp).padding(vertical = 6.dp)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedCategoryFilter = cat }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧴", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your cabinet is empty!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Add products to monitor expiration timelines and record ratings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductCabinetCard(
                        product = product,
                        onToggleOpen = { viewModel.toggleProductOpened(product) },
                        onDelete = { viewModel.deleteProduct(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCabinetCard(
    product: SkincareProduct,
    onToggleOpen: () -> Unit,
    onDelete: () -> Unit
) {
    // Expiration math
    val expirationInfo = remember(product) {
        if (!product.isOpened || product.openedDate == null || product.expirationMonths == null) {
            "Shelf Life: ${product.expirationMonths ?: 12}M (Unopened)"
        } else {
            val openedMs = product.openedDate
            val expiryMs = openedMs + (product.expirationMonths.toLong() * 30L * 24L * 60L * 60L * 1000L)
            val diffMs = expiryMs - System.currentTimeMillis()
            val diffDays = (diffMs / (24 * 60 * 60 * 1000)).toInt()

            if (diffDays < 0) {
                "Expired! ⚠️ Replace product."
            } else {
                val monthsLeft = diffDays / 30
                if (monthsLeft >= 1) {
                    "Expires in $monthsLeft months ($diffDays days left)"
                } else {
                    "Expires in $diffDays days! ⚠️"
                }
            }
        }
    }

    val isExpired = remember(product) {
        if (!product.isOpened || product.openedDate == null || product.expirationMonths == null) false
        else {
            val expiryMs = product.openedDate + (product.expirationMonths.toLong() * 30L * 24L * 60L * 60L * 1000L)
            System.currentTimeMillis() > expiryMs
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Brand, Name & Category Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.brand.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rating Stars & Frequency
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (product.rating > 0) "${product.rating}/5.0" else "No rating",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "•  ${product.usageFrequency}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete product",
                        tint = Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (product.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = product.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(12.dp))

            // Expiration and Open status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isExpired) Color.Red
                                    else if (product.isOpened) MaterialTheme.colorScheme.secondary
                                    else Color.Gray
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (product.isOpened) "Opened" else "Unopened Cabinet",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isExpired) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = expirationInfo,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isExpired) Color.Red else Color.Gray
                    )
                }

                Button(
                    onClick = onToggleOpen,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (product.isOpened) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                        contentColor = if (product.isOpened) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = if (product.isOpened) "Close" else "Mark Opened",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


// ================= TAB 3: SKIN DIARY (PROGRESS LOGS) =================
@Composable
fun DiaryTab(
    viewModel: SkincareViewModel,
    progressLogs: List<ProgressLog>
) {
    if (progressLogs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📸", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No journal entries yet!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Log today's skin conditions, add notes, and choose a preset mood or gallery photo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 88.dp, top = 8.dp)
        ) {
            items(progressLogs, key = { it.id }) { log ->
                DiaryLogCard(
                    log = log,
                    onDelete = { viewModel.deleteProgressLog(log) }
                )
            }
        }
    }
}

@Composable
fun DiaryLogCard(
    log: ProgressLog,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("diary_card_${log.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Log Visual Top Box (Image or Preset Gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (log.photoUri != null && log.photoUri.startsWith("preset_")) {
                    // Render beautiful premium skin theme gradients
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(getPresetGradient(log.photoUri))
                    )
                } else if (!log.photoUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = log.photoUri,
                        contentDescription = "Skin Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.background
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌿 No Photo Attached", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                    }
                }

                // Overlay info badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.BottomStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = formatDateDisplay(log.date),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Delete button in top corner overlay
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(28.dp)
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete log",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Journal details
            Column(modifier = Modifier.padding(16.dp)) {
                // Skin rating stars + Skin Condition Mood Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Rating: ", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        repeat(5) { index ->
                            Text(
                                text = if (index < log.skinRating) "⭐" else "☆",
                                fontSize = 14.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                color = getRatingBgColor(log.skinRating),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = getRatingText(log.skinRating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = getRatingTextColor(log.skinRating)
                        )
                    }
                }

                if (log.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = log.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(12.dp))

                // Diagnostic parameters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DiagnosticTag(label = "Hydration", value = log.hydrationLevel, color = Color(0xFF4EA8DE))
                    DiagnosticTag(label = "Oiliness", value = log.oilinessLevel, color = Color(0xFFFFB703))
                    DiagnosticTag(label = "Redness", value = log.rednessLevel, color = Color(0xFFE63946))
                }
            }
        }
    }
}

@Composable
fun DiagnosticTag(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = value,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Helpers for Diary Preset Visuals
fun getPresetGradient(presetKey: String): Brush {
    return when (presetKey) {
        "preset_glow" -> Brush.radialGradient(
            colors = listOf(Color(0xFFFFF0F5), Color(0xFFFFD1DC), Color(0xFFFBC4AB))
        )
        "preset_hydration" -> Brush.linearGradient(
            colors = listOf(Color(0xFFE0F2FE), Color(0xFF7DD3FC), Color(0xFF38BDF8))
        )
        "preset_calm" -> Brush.linearGradient(
            colors = listOf(Color(0xFFF1F5F9), Color(0xFFCBD5E1), Color(0xFF94A3B8))
        )
        "preset_healing" -> Brush.radialGradient(
            colors = listOf(Color(0xFFFFE4E6), Color(0xFFFECDD3), Color(0xFFFDA4AF))
        )
        "preset_radiant" -> Brush.linearGradient(
            colors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A), Color(0xFFF59E0B))
        )
        else -> Brush.linearGradient(
            colors = listOf(Color(0xFFFFF5F5), Color(0xFFFED7D7))
        )
    }
}

fun getRatingBgColor(rating: Int): Color {
    return when (rating) {
        5 -> Color(0xFFD4EDDA)
        4 -> Color(0xFFE2F0D9)
        3 -> Color(0xFFFFF3CD)
        2 -> Color(0xFFFCE8E6)
        else -> Color(0xFFF8D7DA)
    }
}

fun getRatingTextColor(rating: Int): Color {
    return when (rating) {
        5 -> Color(0xFF155724)
        4 -> Color(0xFF385723)
        3 -> Color(0xFF856404)
        2 -> Color(0xFF721C24)
        else -> Color(0xFF721C24)
    }
}

fun getRatingText(rating: Int): String {
    return when (rating) {
        5 -> "Radiant Glow ✨"
        4 -> "Healthy Skin 🌿"
        3 -> "Balanced ⚖️"
        2 -> "Congested/Dry ⚠️"
        else -> "Irritated/Breakout 🚨"
    }
}


// ================= TAB 4: INSIGHTS & SETTINGS =================
@Composable
fun InsightsTab(
    viewModel: SkincareViewModel,
    products: List<SkincareProduct>,
    streak: Int,
    progressLogs: List<ProgressLog>,
    settings: com.example.data.model.SkincareSettings
) {
    val context = LocalContext.current

    // Settings editing fields
    var skinGoal by remember(settings) { mutableStateOf(settings.skinTypeGoal) }
    var primaryConcern by remember(settings) { mutableStateOf(settings.primaryConcern) }
    var morningTime by remember(settings) { mutableStateOf(settings.morningReminderTime) }
    var nightTime by remember(settings) { mutableStateOf(settings.nightReminderTime) }
    var notificationsEnabled by remember(settings) { mutableStateOf(settings.remindersEnabled) }

    // Dropdown options
    val goals = listOf("Hydration", "Anti-Aging & Firmness", "Acne & Texture Prevention", "Skin Barrier Repair", "Brightening")
    var goalExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // AI SKINCARE COACH CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Skincare Routine Advisor",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Personalized coaching advice",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val dynamicAdvice = remember(settings, products.size) {
                        getSkincareAdvice(settings.skinTypeGoal, settings.primaryConcern, products)
                    }

                    Text(
                        text = dynamicAdvice,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // STATS BOXES
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItemCard(
                    modifier = Modifier.weight(1f),
                    title = "Streak",
                    value = "$streak Days",
                    icon = "🔥",
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                StatItemCard(
                    modifier = Modifier.weight(1f),
                    title = "Cabinet",
                    value = "${products.size} Items",
                    icon = "🧴",
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }

        // SETTINGS / PREFERENCES CONTAINER
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Skincare Preferences",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Customize your reminders and targets",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Skin Type Goal Input
                    Text(
                        text = "Your Skin Goal",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = skinGoal,
                        onValueChange = {
                            skinGoal = it
                            viewModel.updateSettings(morningTime, nightTime, notificationsEnabled, it, primaryConcern)
                        },
                        placeholder = { Text("e.g. Dewy & Plump Skin") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_skin_goal"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Concern Dropdown-Style Select
                    Text(
                        text = "Primary Concern",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box {
                        OutlinedTextField(
                            value = primaryConcern,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_primary_concern")
                                .clickable { goalExpanded = true },
                            trailingIcon = {
                                IconButton(onClick = { goalExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        DropdownMenu(
                            expanded = goalExpanded,
                            onDismissRequest = { goalExpanded = false }
                        ) {
                            goals.forEach { g ->
                                DropdownMenuItem(
                                    text = { Text(g) },
                                    onClick = {
                                        primaryConcern = g
                                        viewModel.updateSettings(morningTime, nightTime, notificationsEnabled, skinGoal, g)
                                        goalExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // REMINDERS HEADER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = "Reminders", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Daily Habits Reminders",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                notificationsEnabled = it
                                viewModel.updateSettings(morningTime, nightTime, it, skinGoal, primaryConcern)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }

                    if (notificationsEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Morning time picker trigger
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .clickable {
                                    showTimePicker(context, morningTime) { selectedTime ->
                                        morningTime = selectedTime
                                        viewModel.updateSettings(selectedTime, nightTime, notificationsEnabled, skinGoal, primaryConcern)
                                    }
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Morning Reminder Time ☀️", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = morningTime,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Night time picker trigger
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .clickable {
                                    showTimePicker(context, nightTime) { selectedTime ->
                                        nightTime = selectedTime
                                        viewModel.updateSettings(morningTime, selectedTime, notificationsEnabled, skinGoal, primaryConcern)
                                    }
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Nighttime Reminder Time 🌙", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = nightTime,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatItemCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}

fun showTimePicker(context: android.content.Context, currentTime: String, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    var initialHour = 8
    var initialMinute = 0
    try {
        val format = SimpleDateFormat("hh:mm a", Locale.US)
        val date = format.parse(currentTime)
        if (date != null) {
            calendar.time = date
            initialHour = calendar.get(Calendar.HOUR_OF_DAY)
            initialMinute = calendar.get(Calendar.MINUTE)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    TimePickerDialog(
        context,
        { _, hour, minute ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val format = SimpleDateFormat("hh:mm a", Locale.US)
            onTimeSelected(format.format(cal.time))
        },
        initialHour,
        initialMinute,
        false
    ).show()
}

// Skincare Advisor Logic
fun getSkincareAdvice(goal: String, concern: String, products: List<SkincareProduct>): String {
    val activeSunscreens = products.count { it.category == "Sunscreen" && it.isOpened }
    val activeCleansers = products.count { it.category == "Cleanser" && it.isOpened }

    val base = when (concern) {
        "Acne & Texture Prevention" -> "For texture and congestion, double cleansing in the evening (first with an oil cleanser, then with a water-based gentle cleanser) is critical to clear sunscreen residue. Integrate an exfoliant like Salicylic Acid (BHA) 2-3 times per week at night. Avoid heavy oils."
        "Hydration" -> "Hydration requires binding water to skin and locking it in. Layer a lightweight Hyaluronic Acid serum onto damp skin, followed immediately by a lipid-rich moisturizer. Avoid physical scrubs that tear skin barrier."
        "Anti-Aging & Firmness" -> "Ensure a stable Retinoid is included in your nightly routine. Retinol triggers collagen production but requires 6-12 weeks of consecutive use. Always couple retinol night with deep hydration and wear sunscreen next morning!"
        "Skin Barrier Repair" -> "Strip down active serums! Focus on a simple routine: gentle cream cleanser, ceramide-rich soothing moisturizer, and broad-spectrum sunscreen. Avoid all exfoliating acids or vitamin C until irritation subsides."
        "Brightening" -> "Integrate Vitamin C (Ascorbic Acid) or Niacinamide in your morning routine under sunscreen. Vitamin C acts as an antioxidant boosting sunscreen efficiency, helping clear hyperpigmentation."
        else -> "Build a core stable routine of Cleanse, Moisturize, and SPF first. Add targeted serums (retinoids, hyaluronic acids, vitamin C) one-by-one to evaluate tolerance."
    }

    val warning = if (activeSunscreens == 0) {
        "\n\n🚨 WARNING: You have no active opened SPF on your shelf. Sunscreen is the absolute foundation of skincare. Acid peels and Retinol can cause extreme sensitivity without proper SPF protection."
    } else ""

    val cleanserCheck = if (activeCleansers == 0) {
        "\n\n🧼 Tip: Ensure you have an active gentle cleanser logged to wash off night repairs and daily pollution cleanly."
    } else ""

    return "$base\n\n🎯 Goal Target: $goal$warning$cleanserCheck"
}


// ================= DIALOGS =================

// 1. Add Custom Habit
@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var timeOfDay by remember { mutableStateOf("Morning") }
    val times = listOf("Morning", "Night", "All Day")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Skincare Habit", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit Name") },
                    placeholder = { Text("e.g. Apply Face Sheet Mask 🌸") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_habit_name_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Routine Time of Day", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    times.forEach { t ->
                        val isSel = t == timeOfDay
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSel) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { timeOfDay = t }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = t,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onSave(name, timeOfDay) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Add to Routines", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

// 2. Add Skincare Product
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Boolean, Long?, Int, String, Float, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Cleanser") }
    var isOpened by remember { mutableStateOf(true) }
    var expirationMonths by remember { mutableStateOf("12") }
    var notes by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(4.0f) }
    var usageFreq by remember { mutableStateOf("Daily") }

    val categories = listOf("Cleanser", "Toner", "Serum", "Moisturizer", "Sunscreen", "Treatment", "Other")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Add Product to Shelf",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Name") },
                        placeholder = { Text("e.g. Hydro Boost Gel Cream") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_prod_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand") },
                        placeholder = { Text("e.g. Neutrogena") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_prod_brand"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Category scrollable selection
                item {
                    Text("Category", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    ScrollableTabRow(
                        selectedTabIndex = categories.indexOf(category),
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = {}
                    ) {
                        categories.forEach { cat ->
                            val isSel = cat == category
                            Box(
                                modifier = Modifier
                                    .padding(end = 6.dp).padding(vertical = 4.dp)
                                    .background(
                                        color = if (isSel) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { category = cat }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Expiration Shelf-life input
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = expirationMonths,
                            onValueChange = { expirationMonths = it },
                            label = { Text("PAO Shelf Life (Months)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dialog_prod_expiry"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = usageFreq,
                            onValueChange = { usageFreq = it },
                            label = { Text("Frequency") },
                            placeholder = { Text("Daily / AM / PM") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dialog_prod_freq"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Is Opened checkbox
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .clickable { isOpened = !isOpened }
                            .padding(10.dp)
                    ) {
                        Checkbox(
                            checked = isOpened,
                            onCheckedChange = { isOpened = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Product is already opened", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("Will start the expiration countdown from today.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }

                // Product Rating slider
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Initial Rating", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("${rating}/5.0 ⭐", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = rating,
                            onValueChange = { rating = String.format(Locale.US, "%.1f", it).toFloat() },
                            valueRange = 0f..5f,
                            steps = 9
                        )
                    }
                }

                // Product Notes
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Product Experience / Notes") },
                        placeholder = { Text("Texture, smell, initial reaction...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_prod_notes"),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Save buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank() && brand.isNotBlank()) {
                                    val exp = expirationMonths.toIntOrNull() ?: 12
                                    onSave(name, brand, category, isOpened, if (isOpened) System.currentTimeMillis() else null, exp, notes, rating, usageFreq)
                                }
                            },
                            enabled = name.isNotBlank() && brand.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Add Product", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// 3. Add Diary Log
@Composable
fun AddDiaryDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, String?, String, String, String) -> Unit
) {
    var notes by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(4) }
    var selectedPhotoUri by remember { mutableStateOf<String?>("preset_glow") } // Default preset

    var hydration by remember { mutableStateOf("Normal") }
    var oiliness by remember { mutableStateOf("Balanced") }
    var redness by remember { mutableStateOf("None") }

    val levels = listOf("Dry", "Normal", "Hydrated")
    val oilLevels = listOf("Dry", "Balanced", "Oily")
    val redLevels = listOf("None", "Mild", "Severe")

    // Android Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri.toString()
        }
    }

    // Presets list
    val presets = listOf(
        "preset_glow" to "Sparkling Glow ✨",
        "preset_hydration" to "Calming Water 💧",
        "preset_calm" to "Soothing Slate 🌿",
        "preset_healing" to "Rose Healing ❤️",
        "preset_radiant" to "Sunny Radiance ☀️"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Log Skin Condition Today",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Star rating representation for today's comfort
                item {
                    Column {
                        Text("Daily Skin Feeling", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row {
                                repeat(5) { index ->
                                    val isFilled = index < rating
                                    Text(
                                        text = if (isFilled) "⭐" else "☆",
                                        fontSize = 28.sp,
                                        modifier = Modifier
                                            .clickable { rating = index + 1 }
                                            .padding(end = 4.dp)
                                    )
                                }
                            }
                            Text(
                                text = getRatingText(rating),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Progress photo visual source selection
                item {
                    Text("Select Progress Image", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Photo, contentDescription = "Gallery", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("From Gallery", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { selectedPhotoUri = "preset_glow" },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Text("Use Preset Mood", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset Gradient Selectors row (if preset is selected)
                    if (selectedPhotoUri != null && selectedPhotoUri!!.startsWith("preset_")) {
                        Text("Choose Skin Preset Mood:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        ScrollableTabRow(
                            selectedTabIndex = presets.map { it.first }.indexOf(selectedPhotoUri),
                            edgePadding = 0.dp,
                            containerColor = Color.Transparent,
                            divider = {},
                            indicator = {}
                        ) {
                            presets.forEach { pair ->
                                val isSel = selectedPhotoUri == pair.first
                                Box(
                                    modifier = Modifier
                                        .padding(end = 6.dp).padding(vertical = 4.dp)
                                        .background(
                                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedPhotoUri = pair.first }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = pair.second,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    } else if (selectedPhotoUri != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = selectedPhotoUri,
                                contentDescription = "Preview",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Custom image loaded", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Photo will be stored in progress timeline.", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                // Hydration selection
                item {
                    Text("Hydration Level", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        levels.forEach { lvl ->
                            val isSel = lvl == hydration
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (isSel) Color(0xFF4EA8DE) else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { hydration = lvl }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lvl,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Oiliness selection
                item {
                    Text("Oiliness Level", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        oilLevels.forEach { lvl ->
                            val isSel = lvl == oiliness
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (isSel) Color(0xFFFFB703) else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { oiliness = lvl }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lvl,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Redness selection
                item {
                    Text("Redness / Irritation Level", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        redLevels.forEach { lvl ->
                            val isSel = lvl == redness
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (isSel) Color(0xFFE63946) else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { redness = lvl }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lvl,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Notes input
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Log entry comments") },
                        placeholder = { Text("How does your skin feel? Any new breakouts, dietary choices, or sleep factors?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_diary_notes"),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Actions row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { onSave(notes, rating, selectedPhotoUri, hydration, oiliness, redness) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save Entry", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}


// --- Static Formatting Helpers ---
fun formatDateDisplay(dateStr: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.US)
        val date = parser.parse(dateStr)
        if (date != null) formatter.format(date) else dateStr
    } catch (e: Exception) {
        dateStr
    }
}

fun isDateToday(dateStr: String): Boolean {
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    return dateStr == todayStr
}
