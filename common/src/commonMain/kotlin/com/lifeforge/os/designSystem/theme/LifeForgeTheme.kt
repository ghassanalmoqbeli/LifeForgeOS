package com.lifeforge.os.designSystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lifeforge.os.core.preferences.CornerRadius
import com.lifeforge.os.core.preferences.Density
import com.lifeforge.os.core.preferences.ThemeMode
import com.lifeforge.os.core.preferences.ThemePreset
import com.lifeforge.os.designSystem.TypographyTokens

// ============================================================
// LIFE FORGE THEME - Material 3 Wrapper
// ============================================================

@Immutable
data class LifeForgeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceElevated: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val onSurfaceDisabled: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val error: Color,
    val onError: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val outline: Color,
    val outlineVariant: Color,
    val divider: Color,
    val selection: Color,
    val focus: Color,
    val hover: Color,
    val pressed: Color,
    val scrim: Color,
    val shadow: Color,
)

object LightColors {
    val colors = LifeForgeColors(
        background = Color(0xFFF7F7F8),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF0F0F0),
        surfaceElevated = Color(0xFFFFFFFF),
        surfaceContainer = Color(0xFFF5F5F5),
        surfaceContainerHigh = Color(0xFFEBEBEB),
        onSurface = Color(0xFF1A1A1A),
        onSurfaceVariant = Color(0xFF525252),
        onSurfaceDisabled = Color(0xFFA8A8A8),
        primary = Color(0xFF00A693),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFCCF2EE),
        onPrimaryContainer = Color(0xFF003D37),
        secondary = Color(0xFF7C5BBF),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFE9DDFF),
        onSecondaryContainer = Color(0xFF2D1B4E),
        tertiary = Color(0xFFD48A00),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFE4B8),
        onTertiaryContainer = Color(0xFF453000),
        error = Color(0xFFDC2626),
        onError = Color(0xFFFFFFFF),
        success = Color(0xFF16A34A),
        warning = Color(0xFFD97706),
        info = Color(0xFF2563EB),
        outline = Color(0xFFE5E5E5),
        outlineVariant = Color(0xFFEDEDED),
        divider = Color(0xFFE5E5E5),
        selection = Color(0x3300A693),
        focus = Color(0xFF00A693),
        hover = Color(0x0DFFFFFF),
        pressed = Color(0x1A000000),
        scrim = Color(0x99000000),
        shadow = Color(0x33000000),
    )
}

object DarkColors {
    val colors = LifeForgeColors(
        background = Color(0xFF0A0A0A),
        surface = Color(0xFF141414),
        surfaceVariant = Color(0xFF1C1C1C),
        surfaceElevated = Color(0xFF242424),
        surfaceContainer = Color(0xFF1A1A1A),
        surfaceContainerHigh = Color(0xFF222222),
        onSurface = Color(0xFFE0E0E0),
        onSurfaceVariant = Color(0xFFB0B0B0),
        onSurfaceDisabled = Color(0xFF6E6E6E),
        primary = Color(0xFF00BFA6),
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFF003D37),
        onPrimaryContainer = Color(0xFF00BFA6),
        secondary = Color(0xFF8B5CF6),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF2D1B4E),
        onSecondaryContainer = Color(0xFF8B5CF6),
        tertiary = Color(0xFFF59E0B),
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFF453000),
        onTertiaryContainer = Color(0xFFF59E0B),
        error = Color(0xFFEF4444),
        onError = Color(0xFFFFFFFF),
        success = Color(0xFF22C55E),
        warning = Color(0xFFF59E0B),
        info = Color(0xFF3B82F6),
        outline = Color(0xFF3A3A3A),
        outlineVariant = Color(0xFF2A2A2A),
        divider = Color(0xFF2A2A2A),
        selection = Color(0x3300BFA6),
        focus = Color(0xFF00BFA6),
        hover = Color(0x1AFFFFFF),
        pressed = Color(0x33FFFFFF),
        scrim = Color(0x99000000),
        shadow = Color(0xFF000000),
    )
}

// Composition locals for custom tokens
val LocalLifeForgeColors = compositionLocalOf { DarkColors.colors }
val LocalCornerRadius = compositionLocalOf { CornerRadius.Medium }
val LocalDensity = compositionLocalOf { Density.Normal }
val LocalIsDark = compositionLocalOf { true }

@Immutable
data class DensityScale(
    val spacingScale: Float = 1f,
    val elementHeightScale: Float = 1f,
    val iconScale: Float = 1f,
) {
    companion object {
        val Compact = DensityScale(0.85f, 0.9f, 0.9f)
        val Normal = DensityScale(1f, 1f, 1f)
        val Comfortable = DensityScale(1.15f, 1.1f, 1.1f)
    }
}

val LocalDensityScale = compositionLocalOf { DensityScale.Normal }

/**
 * Apply theme preset colors on top of base colors.
 * Adjusts primary/secondary/tertiary hues to match selected preset.
 */
fun applyPresetToColors(base: LifeForgeColors, preset: ThemePreset): LifeForgeColors {
    return when (preset) {
        ThemePreset.Midnight -> base
        ThemePreset.Graphite -> base.copy(
            primary = Color(0xFF6366F1),
            secondary = Color(0xFF14B8A6),
            tertiary = Color(0xFFF97316)
        )
        ThemePreset.Ocean -> base.copy(
            primary = Color(0xFF0EA5E9),
            secondary = Color(0xFF22D3EE),
            tertiary = Color(0xFFF43F5E)
        )
        ThemePreset.Forest -> base.copy(
            primary = Color(0xFF22C55E),
            secondary = Color(0xFF84CC16),
            tertiary = Color(0xFFEAB308)
        )
        ThemePreset.Minimal -> base.copy(
            primary = Color(0xFF9C9CA3),
            secondary = Color(0xFF71717A),
            tertiary = Color(0xFFA1A1AA)
        )
        ThemePreset.Custom -> base
    }
}

@Composable
fun LifeForgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeMode: ThemeMode = ThemeMode.System,
    themePreset: ThemePreset = ThemePreset.Midnight,
    accentColor: Color = Color(0xFF00BFA6),
    cornerRadius: CornerRadius = CornerRadius.Medium,
    density: Density = Density.Normal,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> darkTheme
    }

    val baseColors = if (isDark) DarkColors.colors else LightColors.colors
    val presetColors = applyPresetToColors(baseColors, themePreset)
    val finalColors = presetColors.copy(
        primary = accentColor,
        primaryContainer = if (isDark) Color(0xFF003D37) else Color(0xFFCCF2EE),
        onPrimaryContainer = accentColor
    )

    val densityScale = when (density) {
        Density.Compact -> DensityScale.Compact
        Density.Normal -> DensityScale.Normal
        Density.Comfortable -> DensityScale.Comfortable
    }

    val shapes = when (cornerRadius) {
        CornerRadius.None -> Shapes(extraSmall = RoundedCornerShape(0.dp), small = RoundedCornerShape(0.dp), medium = RoundedCornerShape(0.dp), large = RoundedCornerShape(0.dp), extraLarge = RoundedCornerShape(0.dp))
        CornerRadius.Small -> Shapes(extraSmall = RoundedCornerShape(4.dp), small = RoundedCornerShape(4.dp), medium = RoundedCornerShape(6.dp), large = RoundedCornerShape(8.dp), extraLarge = RoundedCornerShape(12.dp))
        CornerRadius.Medium -> Shapes(extraSmall = RoundedCornerShape(8.dp), small = RoundedCornerShape(10.dp), medium = RoundedCornerShape(12.dp), large = RoundedCornerShape(16.dp), extraLarge = RoundedCornerShape(20.dp))
        CornerRadius.Large -> Shapes(extraSmall = RoundedCornerShape(12.dp), small = RoundedCornerShape(16.dp), medium = RoundedCornerShape(20.dp), large = RoundedCornerShape(24.dp), extraLarge = RoundedCornerShape(28.dp))
        CornerRadius.ExtraLarge -> Shapes(extraSmall = RoundedCornerShape(16.dp), small = RoundedCornerShape(20.dp), medium = RoundedCornerShape(28.dp), large = RoundedCornerShape(32.dp), extraLarge = RoundedCornerShape(40.dp))
    }

    val m3Colors = if (isDark) {
        material3DarkColorScheme(finalColors)
    } else {
        material3LightColorScheme(finalColors)
    }

    val typography = Typography(
        displayLarge = TypographyTokens.DisplayLarge,
        displayMedium = TypographyTokens.DisplayMedium,
        displaySmall = TypographyTokens.DisplaySmall,
        headlineLarge = TypographyTokens.HeadlineLarge,
        headlineMedium = TypographyTokens.HeadlineMedium,
        headlineSmall = TypographyTokens.HeadlineSmall,
        titleLarge = TypographyTokens.TitleLarge,
        titleMedium = TypographyTokens.TitleMedium,
        titleSmall = TypographyTokens.TitleSmall,
        bodyLarge = TypographyTokens.BodyLarge,
        bodyMedium = TypographyTokens.BodyMedium,
        bodySmall = TypographyTokens.BodySmall,
        labelLarge = TypographyTokens.LabelLarge,
        labelMedium = TypographyTokens.LabelMedium,
        labelSmall = TypographyTokens.LabelSmall,
    )

    CompositionLocalProvider(
        LocalLifeForgeColors provides finalColors,
        LocalCornerRadius provides cornerRadius,
        LocalDensity provides density,
        LocalDensityScale provides densityScale,
        LocalIsDark provides isDark,
    ) {
        MaterialTheme(
            colorScheme = m3Colors,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

// Convert LifeForgeColors to Material3 ColorScheme
private fun material3DarkColorScheme(colors: LifeForgeColors): ColorScheme = darkColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    primaryContainer = colors.primaryContainer,
    onPrimaryContainer = colors.onPrimaryContainer,
    secondary = colors.secondary,
    onSecondary = colors.onSecondary,
    secondaryContainer = colors.secondaryContainer,
    onSecondaryContainer = colors.onSecondaryContainer,
    tertiary = colors.tertiary,
    onTertiary = colors.onTertiary,
    tertiaryContainer = colors.tertiaryContainer,
    onTertiaryContainer = colors.onTertiaryContainer,
    error = colors.error,
    onError = colors.onError,
    background = colors.background,
    onBackground = colors.onSurface,
    surface = colors.surface,
    onSurface = colors.onSurface,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.onSurfaceVariant,
    outline = colors.outline,
    outlineVariant = colors.outlineVariant,
    scrim = colors.scrim,
    inverseSurface = Color(0xFFF5F5F5),
    inverseOnSurface = Color(0xFF1A1A1A),
    inversePrimary = Color(0xFF009688),
    surfaceContainer = colors.surfaceContainer,
    surfaceContainerHigh = colors.surfaceContainerHigh,
    surfaceContainerHighest = colors.surfaceElevated,
)

private fun material3LightColorScheme(colors: LifeForgeColors): ColorScheme = lightColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    primaryContainer = colors.primaryContainer,
    onPrimaryContainer = colors.onPrimaryContainer,
    secondary = colors.secondary,
    onSecondary = colors.onSecondary,
    secondaryContainer = colors.secondaryContainer,
    onSecondaryContainer = colors.onSecondaryContainer,
    tertiary = colors.tertiary,
    onTertiary = colors.onTertiary,
    tertiaryContainer = colors.tertiaryContainer,
    onTertiaryContainer = colors.onTertiaryContainer,
    error = colors.error,
    onError = colors.onError,
    background = colors.background,
    onBackground = colors.onSurface,
    surface = colors.surface,
    onSurface = colors.onSurface,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.onSurfaceVariant,
    outline = colors.outline,
    outlineVariant = colors.outlineVariant,
    scrim = colors.scrim,
    inverseSurface = Color(0xFF1A1A1A),
    inverseOnSurface = Color(0xFFF5F5F5),
    inversePrimary = Color(0xFF00A693),
    surfaceContainer = colors.surfaceContainer,
    surfaceContainerHigh = colors.surfaceContainerHigh,
    surfaceContainerHighest = colors.surfaceElevated,
)

// Convenience accessors
object LifeForgeThemeExt {
    val colors: LifeForgeColors
        @Composable get() = LocalLifeForgeColors.current

    val isDark: Boolean
        @Composable get() = LocalIsDark.current

    val cornerRadius: CornerRadius
        @Composable get() = LocalCornerRadius.current

    val density: Density
        @Composable get() = LocalDensity.current

    val densityScale: DensityScale
        @Composable get() = LocalDensityScale.current
}