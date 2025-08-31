# 🎨 Theme Migration Guide: AppColors → MaterialTheme

This guide helps you migrate from the old `AppColors` system to the new `MaterialTheme.colorScheme` system.

## 🚀 Quick Migration

Replace `AppColors.` with `MaterialTheme.colorScheme.` for most colors:

```kotlin
// ❌ Old way
color = AppColors.background
color = AppColors.primary
color = AppColors.textPrimary

// ✅ New way
color = MaterialTheme.colorScheme.background
color = MaterialTheme.colorScheme.primary
color = MaterialTheme.colorScheme.onBackground
```

## 🔄 Color Mapping

| AppColors | MaterialTheme.colorScheme | Notes |
|-----------|---------------------------|-------|
| `AppColors.background` | `MaterialTheme.colorScheme.background` | Main background |
| `AppColors.surface` | `MaterialTheme.colorScheme.surface` | Card/surface background |
| `AppColors.surfaceVariant` | `MaterialTheme.colorScheme.surfaceVariant` | Elevated surface |
| `AppColors.primary` | `MaterialTheme.colorScheme.primary` | Main purple |
| `AppColors.primaryVariant` | `MaterialTheme.colorScheme.primaryContainer` | Primary container |
| `AppColors.primaryLight` | `MaterialTheme.colorScheme.secondary` | Secondary color |
| `AppColors.secondary` | `MaterialTheme.colorScheme.secondary` | Pink accent |
| `AppColors.textPrimary` | `MaterialTheme.colorScheme.onBackground` | Primary text |
| `AppColors.textSecondary` | `MaterialTheme.colorScheme.onSurfaceVariant` | Secondary text |
| `AppColors.textTertiary` | `MaterialTheme.colorScheme.outlineVariant` | Tertiary text |
| `AppColors.selected` | `MaterialTheme.colorScheme.primary` | Selected state |
| `AppColors.unselected` | `MaterialTheme.colorScheme.outlineVariant` | Unselected state |
| `AppColors.divider` | `MaterialTheme.colorScheme.outline` | Dividers |
| `AppColors.onBackground` | `MaterialTheme.colorScheme.onBackground` | Text on background |
| `AppColors.onSurface` | `MaterialTheme.colorScheme.onSurface` | Text on surface |
| `AppColors.onPrimary` | `MaterialTheme.colorScheme.onPrimary` | Text on primary |
| `AppColors.onSecondary` | `MaterialTheme.colorScheme.onSecondary` | Text on secondary |

## 🎯 Custom Colors (Keep Using)

These colors don't exist in Material3, so keep using them:

```kotlin
// ✅ Keep using these
val accentGreen = CustomColors.AccentGreen
val accentYellow = CustomColors.AccentYellow
val trendingPositive = CustomColors.TrendingPositive
val trendingNegative = CustomColors.TrendingNegative
val buttonGradient = CustomColors.ButtonGradient
val progressGradient = CustomColors.ProgressGradient
```

## 🔧 Migration Helper

If you need to migrate gradually, use the migration helper:

```kotlin
@Composable
fun MyComponent() {
    val colors = migrateFromAppColors()
    
    // This gives you the same API as AppColors but uses MaterialTheme underneath
    color = colors.background
    color = colors.primary
    color = colors.textPrimary
}
```

## 📝 Step-by-Step Migration

### 1. Replace Standard Colors
```kotlin
// ❌ Before
Text(
    text = "Hello",
    color = AppColors.textPrimary
)

// ✅ After
Text(
    text = "Hello",
    color = MaterialTheme.colorScheme.onBackground
)
```

### 2. Replace Background Colors
```kotlin
// ❌ Before
Box(
    modifier = Modifier.background(AppColors.background)
)

// ✅ After
Box(
    modifier = Modifier.background(MaterialTheme.colorScheme.background)
)
```

### 3. Replace Card Colors
```kotlin
// ❌ Before
Card(
    colors = CardDefaults.cardColors(
        containerColor = AppColors.surface
    )
)

// ✅ After
Card(
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )
)
```

### 4. Keep Custom Colors
```kotlin
// ✅ Keep these as they are
val gradient = CustomColors.ButtonGradient
val accent = CustomColors.AccentGreen
```

## 🎨 Benefits of Migration

1. **Material Design 3 Compliance**: Follows Google's latest design standards
2. **Automatic Theme Switching**: Better integration with system themes
3. **Consistent API**: Single source of truth for colors
4. **Better Performance**: No custom color wrapper overhead
5. **Future-Proof**: Built-in support for new Material3 features

## 🚨 Common Pitfalls

### Don't Mix Systems
```kotlin
// ❌ Don't do this
color = MaterialTheme.colorScheme.primary
color = AppColors.background  // Inconsistent!

// ✅ Do this
color = MaterialTheme.colorScheme.primary
color = MaterialTheme.colorScheme.background
```

### Use Correct Text Colors
```kotlin
// ❌ Wrong - text on background
color = MaterialTheme.colorScheme.background

// ✅ Correct - text on background
color = MaterialTheme.colorScheme.onBackground
```

## 📚 Examples

### Button Component
```kotlin
@Composable
fun MyButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(text)
    }
}
```

### Card Component
```kotlin
@Composable
fun MyCard(
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        content()
    }
}
```

### Text Component
```kotlin
@Composable
fun MyText(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(
        text = text,
        style = style,
        color = MaterialTheme.colorScheme.onBackground
    )
}
```

## 🎯 Migration Checklist

- [ ] Replace `AppColors.background` → `MaterialTheme.colorScheme.background`
- [ ] Replace `AppColors.surface` → `MaterialTheme.colorScheme.surface`
- [ ] Replace `AppColors.primary` → `MaterialTheme.colorScheme.primary`
- [ ] Replace `AppColors.textPrimary` → `MaterialTheme.colorScheme.onBackground`
- [ ] Replace `AppColors.textSecondary` → `MaterialTheme.colorScheme.onSurfaceVariant`
- [ ] Keep `CustomColors.*` for custom colors
- [ ] Test both light and dark themes
- [ ] Remove unused AppColors imports
- [ ] Update component documentation

## 🆘 Need Help?

If you encounter issues during migration:

1. Use the migration helper: `migrateFromAppColors()`
2. Check the Material3 color scheme documentation
3. Refer to this mapping table
4. Test with both light and dark themes

Happy migrating! 🚀
