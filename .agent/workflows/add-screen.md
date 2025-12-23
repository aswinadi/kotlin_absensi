---
description: How to add new screens with theme-aware colors
---

# Adding New Screens

When creating new screens in the kotlin_absensi app, follow these guidelines to ensure theme-aware colors work properly.

## 1. Import LocalAppColors

Instead of using `DarkColors` directly, import and use `LocalAppColors`:

```kotlin
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors
```

## 2. Access appColors in Composables

At the start of each composable function that uses colors, add:

```kotlin
@Composable
fun MyScreen() {
    val appColors = LocalAppColors.current
    
    // Now use appColors instead of DarkColors
}
```

## 3. Color Mapping Reference

| Old (DarkColors)          | New (appColors)              |
|---------------------------|------------------------------|
| DarkColors.BackgroundGradientStart | appColors.backgroundGradientStart |
| DarkColors.BackgroundGradientEnd   | appColors.backgroundGradientEnd   |
| DarkColors.Surface        | appColors.surface            |
| DarkColors.SurfaceVariant | appColors.surfaceVariant     |
| DarkColors.TextPrimary    | appColors.textPrimary        |
| DarkColors.TextSecondary  | appColors.textSecondary      |
| DarkColors.TextTertiary   | appColors.textTertiary       |

## 4. Nested Composables

Each nested private composable that uses colors MUST declare its own `appColors`:

```kotlin
@Composable
private fun MyCard() {
    val appColors = LocalAppColors.current  // Required in each composable!
    
    Card(
        colors = CardDefaults.cardColors(containerColor = appColors.surface)
    ) {
        // ...
    }
}
```

## 5. Brand Colors

`MaxmarColors` can still be used directly for brand colors (Primary, Error, Success, etc.) as these don't change with the theme.

## 6. Background Gradient Pattern

```kotlin
Box(
    modifier = Modifier
        .fillMaxSize()
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    appColors.backgroundGradientStart,
                    appColors.backgroundGradientEnd
                )
            )
        )
)
```

## 7. TopAppBar Pattern

```kotlin
TopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = appColors.textPrimary,
        navigationIconContentColor = appColors.textPrimary
    )
)
```

## 8. Card Pattern

```kotlin
Card(
    colors = CardDefaults.cardColors(containerColor = appColors.surface)
)
```

## 9. Text Pattern

```kotlin
Text(
    text = "Title",
    color = appColors.textPrimary
)

Text(
    text = "Subtitle",
    color = appColors.textSecondary
)
```

## Example: Complete Screen Template

```kotlin
package com.maxmar.attendance.ui.screens.myfeature

// ... other imports
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFeatureScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: MyFeatureViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val appColors = LocalAppColors.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Feature", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = appColors.textPrimary,
                    navigationIconContentColor = appColors.textPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            appColors.backgroundGradientStart,
                            appColors.backgroundGradientEnd
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            // Screen content here
        }
    }
}
```
