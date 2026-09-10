package com.lifeforge.os.designSystem

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================
// COLOR TOKENS
// ============================================================

object ColorTokens {
    // Base Semantic Colors
    val Background = Color(0xFF0A0A0A)
    val Surface = Color(0xFF141414)
    val SurfaceVariant = Color(0xFF1C1C1C)
    val SurfaceElevated = Color(0xFF242424)
    val SurfaceContainer = Color(0xFF1A1A1A)
    val SurfaceContainerHigh = Color(0xFF222222)

    val OnBackground = Color(0xFFE8E8E8)
    val OnSurface = Color(0xFFE0E0E0)
    val OnSurfaceVariant = Color(0xFFB0B0B0)
    val OnSurfaceDisabled = Color(0xFF6E6E6E)

    val Primary = Color(0xFF00BFA6) // Teal accent
    val OnPrimary = Color(0xFF000000)
    val PrimaryContainer = Color(0xFF003D37)
    val OnPrimaryContainer = Color(0xFF00BFA6)

    val Secondary = Color(0xFF8B5CF6) // Purple
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFF2D1B4E)
    val OnSecondaryContainer = Color(0xFF8B5CF6)

    val Tertiary = Color(0xFFF59E0B) // Amber
    val OnTertiary = Color(0xFF000000)
    val TertiaryContainer = Color(0xFF453000)
    val OnTertiaryContainer = Color(0xFFF59E0B)

    val Error = Color(0xFFEF4444)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFF450A0A)
    val OnErrorContainer = Color(0xFFEF4444)

    val Success = Color(0xFF22C55E)
    val OnSuccess = Color(0xFF000000)
    val SuccessContainer = Color(0xFF052E16)
    val OnSuccessContainer = Color(0xFF22C55E)

    val Warning = Color(0xFFF59E0B)
    val OnWarning = Color(0xFF000000)
    val WarningContainer = Color(0xFF453000)
    val OnWarningContainer = Color(0xFFF59E0B)

    val Info = Color(0xFF3B82F6)
    val OnInfo = Color(0xFFFFFFFF)
    val InfoContainer = Color(0xFF1E3A5F)
    val OnInfoContainer = Color(0xFF3B82F6)

    // Outline / Border
    val Outline = Color(0xFF3A3A3A)
    val OutlineVariant = Color(0xFF2A2A2A)

    // Inverse (for light surfaces on dark)
    val InverseSurface = Color(0xFFF5F5F5)
    val InverseOnSurface = Color(0xFF1A1A1A)
    val InversePrimary = Color(0xFF009688)

    // Scrim
    val Scrim = Color(0xFF000000)

    // Shadow
    val Shadow = Color(0xFF000000)

    // Surface Tint (Material 3)
    val SurfaceTint = Color(0xFF00BFA6)

    // Divider
    val Divider = Color(0xFF2A2A2A)

    // Selection
    val Selection = Color(0x3300BFA6)

    // Focus
    val Focus = Color(0xFF00BFA6)

    // Hover
    val Hover = Color(0x1AFFFFFF)

    // Pressed
    val Pressed = Color(0x33FFFFFF)

    // Dragged
    val Dragged = Color(0x4DFFFFFF)
}

// Theme Preset Colors
object ThemePresets {
    data class PresetColors(
        val name: String,
        val primary: Color,
        val secondary: Color,
        val tertiary: Color,
        val surface: Color,
        val surfaceVariant: Color,
    )

    val Midnight = PresetColors(
        name = "Midnight",
        primary = Color(0xFF00BFA6), // Teal
        secondary = Color(0xFF8B5CF6), // Purple
        tertiary = Color(0xFFF59E0B), // Amber
        surface = Color(0xFF141414),
        surfaceVariant = Color(0xFF1C1C1C)
    )

    val Graphite = PresetColors(
        name = "Graphite",
        primary = Color(0xFF6366F1), // Indigo
        secondary = Color(0xFF14B8A6), // Teal
        tertiary = Color(0xFFF97316), // Orange
        surface = Color(0xFF18181B),
        surfaceVariant = Color(0xFF27272A)
    )

    val Ocean = PresetColors(
        name = "Ocean",
        primary = Color(0xFF0EA5E9), // Sky
        secondary = Color(0xFF22D3EE), // Cyan
        tertiary = Color(0xFFF43F5E), // Rose
        surface = Color(0xFF0C1A2E),
        surfaceVariant = Color(0xFF142840)
    )

    val Forest = PresetColors(
        name = "Forest",
        primary = Color(0xFF22C55E), // Green
        secondary = Color(0xFF84CC16), // Lime
        tertiary = Color(0xFFEAB308), // Yellow
        surface = Color(0xFF0F1F0F),
        surfaceVariant = Color(0xFF1A2E1A)
    )

    val Minimal = PresetColors(
        name = "Minimal",
        primary = Color(0xFF52525B), // Neutral
        secondary = Color(0xFF71717A),
        tertiary = Color(0xFFA1A1AA),
        surface = Color(0xFF09090B),
        surfaceVariant = Color(0xFF18181B)
    )

    val all = listOf(Midnight, Graphite, Ocean, Forest, Minimal)
}

// ============================================================
// TYPOGRAPHY TOKENS
// ============================================================

object TypographyTokens {
    // Font Families
    val fontFamily = FontFamily.Default
    val fontFamilyMono = FontFamily.Monospace
    val fontFamilyArabic = FontFamily.Default // Will be overridden with Arabic font

    // Display Styles
    val DisplayLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W700,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = -0.25.sp
    )

    val DisplayMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    )

    val DisplaySmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    )

    // Headline Styles
    val HeadlineLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    )

    val HeadlineMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )

    val HeadlineSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )

    // Title Styles
    val TitleLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )

    val TitleMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )

    val TitleSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

    // Body Styles
    val BodyLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

    val BodyMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )

    val BodySmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )

    // Label Styles
    val LabelLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

    val LabelMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

    val LabelSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

    // Arabic Adjustments
    val ArabicDisplayLarge = DisplayLarge.copy(
        fontFamily = fontFamilyArabic,
        fontSize = 52.sp,
        lineHeight = 60.sp
    )

    val ArabicHeadlineLarge = HeadlineLarge.copy(
        fontFamily = fontFamilyArabic,
        fontSize = 28.sp,
        lineHeight = 36.sp
    )

    val ArabicBodyLarge = BodyLarge.copy(
        fontFamily = fontFamilyArabic,
        fontSize = 15.sp,
        lineHeight = 24.sp
    )

    val ArabicBodyMedium = BodyMedium.copy(
        fontFamily = fontFamilyArabic,
        fontSize = 13.sp,
        lineHeight = 20.sp
    )
}

// ============================================================
// SPACING TOKENS
// ============================================================

object SpacingTokens {
    val None = 0.dp
    val Xxxs = 2.dp
    val Xxs = 4.dp
    val Xs = 8.dp
    val Sm = 12.dp
    val Md = 16.dp
    val Lg = 20.dp
    val Xl = 24.dp
    val Xxl = 32.dp
    val Xxxl = 40.dp
    val Xxxxl = 48.dp
    val Xxxxxl = 64.dp

    // Semantic spacing
    val InlineXs = Xxs
    val InlineSm = Xs
    val InlineMd = Sm
    val InlineLg = Md

    val StackXs = Xxs
    val StackSm = Xs
    val StackMd = Sm
    val StackLg = Md
    val StackXl = Lg

    val InsetXs = Xxs
    val InsetSm = Xs
    val InsetMd = Sm
    val InsetLg = Md
    val InsetXl = Lg

    val ComponentXs = Xxs
    val ComponentSm = Xs
    val ComponentMd = Sm
    val ComponentLg = Md
    val ComponentXl = Lg

    val LayoutXs = Xs
    val LayoutSm = Sm
    val LayoutMd = Md
    val LayoutLg = Lg
    val LayoutXl = Xl
    val LayoutXxl = Xxl
}

// ============================================================
// SHAPE TOKENS
// ============================================================

object ShapeTokens {

    val None = RoundedCornerShape(0.dp)
    val Xs = RoundedCornerShape(4.dp)
    val Sm = RoundedCornerShape(8.dp)
    val Md = RoundedCornerShape(12.dp)
    val Lg = RoundedCornerShape(16.dp)
    val Xl = RoundedCornerShape(24.dp)
    val Xxl = RoundedCornerShape(32.dp)
    val Full = RoundedCornerShape(9999.dp)

    // Semantic shapes
    val Button = Md
    val Chip = Full
    val Card = Lg
    val Dialog = Xl
    val BottomSheet = Xl
    val TextField = Md
    val DropdownMenu = Md
    val Tooltip = Sm
    val Avatar = Full
    val FAB = Full
    val NavigationBar = Xxl
    val NavigationRail = Xxl
}

// ============================================================
// ELEVATION TOKENS
// ============================================================

object ElevationTokens {
    val Level0 = 0.dp
    val Level1 = 1.dp
    val Level2 = 3.dp
    val Level3 = 6.dp
    val Level4 = 8.dp
    val Level5 = 12.dp

    // Semantic elevations
    val Surface = Level0
    val Card = Level1
    val ElevatedCard = Level2
    val Dialog = Level4
    val BottomSheet = Level3
    val DropdownMenu = Level3
    val Tooltip = Level2
    val FAB = Level3
    val Snackbar = Level3
    val NavigationBar = Level2
    val NavigationRail = Level1
    val AppBar = Level1
}

// ============================================================
// MOTION TOKENS
// ============================================================

object MotionTokens {

    // Durations
    val DurationInstant = 0
    val DurationFast = 100
    val DurationMedium = 200
    val DurationSlow = 300
    val DurationSlowest = 500

    // Easing
    val EaseInOut = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val EaseOut = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val EaseIn = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
    val EaseOutExpo = CubicBezierEasing(0.16f, 1.0f, 0.3f, 1.0f)

    // Standard easing (Material)
    val StandardEasing = EaseInOut
    val EmphasizedEasing = CubicBezierEasing(0.05f, 0.0f, 0.0f, 1.0f)
    val ExpressiveEasing = EaseOutExpo

    // Specs
    val FastTween: TweenSpec<Float> = tween(DurationFast, easing = StandardEasing)
    val MediumTween: TweenSpec<Float> = tween(DurationMedium, easing = StandardEasing)
    val SlowTween: TweenSpec<Float> = tween(DurationSlow, easing = StandardEasing)

    val StandardSpring: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val BouncySpring: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    val StiffSpring: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = Spring.StiffnessHigh
    )

    // Semantic
    val FadeIn = MediumTween
    val FadeOut = FastTween
    val SlideIn = MediumTween
    val SlideOut = FastTween
    val ScaleIn = StandardSpring
    val ScaleOut = FastTween
    val Expand = MediumTween
    val Collapse = FastTween
}

// ============================================================
// ICON RULES
// ============================================================

object IconTokens {
    val SizeXs = 16.dp
    val SizeSm = 20.dp
    val SizeMd = 24.dp
    val SizeLg = 28.dp
    val SizeXl = 32.dp
    val SizeXxl = 48.dp

    val WeightThin = 100
    val WeightLight = 300
    val WeightRegular = 400
    val WeightMedium = 500
    val WeightBold = 600

    // Semantic
    val Navigation = SizeMd
    val Toolbar = SizeMd
    val Button = SizeSm
    val Chip = SizeXs
    val ListItem = SizeMd
    val Card = SizeLg
    val EmptyState = SizeXxl
    val Hero = 64.dp
}

// ============================================================
// BREAKPOINTS
// ============================================================

object Breakpoints {
    val Phone = 0.dp
    val SmallTablet = 600.dp
    val LargeTablet = 840.dp
    val SmallDesktop = 1080.dp
    val MediumDesktop = 1280.dp
    val LargeDesktop = 1440.dp
    val UltraWide = 1920.dp

    // Window size classes (Material 3 Adaptive)
    val CompactWidth = 600.dp
    val MediumWidth = 840.dp
    val ExpandedWidth = 1280.dp
}

// ============================================================
// Z-INDEX
// ============================================================

object ZIndex {
    val Base = 0
    val Content = 10
    val Dropdown = 100
    val Sticky = 200
    val Modal = 300
    val Popover = 400
    val Tooltip = 500
    val Toast = 600
    val Overlay = 1000
}