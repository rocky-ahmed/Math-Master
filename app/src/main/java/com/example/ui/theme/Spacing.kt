package com.example.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centralized semantic spacing, padding, inset, and corner-radius system for Math Master (BUG-59..BUG-67).
 * Ensures consistent horizontal/vertical screen insets, card internal padding, section rhythm,
 * and opaque container color blending across all 14 modules and 31 destinations.
 */
@Immutable
data class MathSpacingSystem(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp,

    // Screen-level content insets
    val screenHorizontal: Dp = 16.dp,
    val screenTop: Dp = 12.dp,
    val screenBottom: Dp = 24.dp,

    // Card internal padding hierarchy
    val cardPaddingCompact: Dp = 12.dp,
    val cardPaddingStandard: Dp = 16.dp,
    val cardPaddingHero: Dp = 18.dp,

    // Vertical & grid spacing rhythm
    val sectionSpacing: Dp = 14.dp,
    val itemSpacing: Dp = 12.dp,
    val compactItemSpacing: Dp = 8.dp,
    val inlineIconSpacing: Dp = 8.dp,

    // Corner radius hierarchy
    val cornerRadiusCompact: Dp = 12.dp,
    val cornerRadiusStandard: Dp = 16.dp,
    val cornerRadiusHero: Dp = 20.dp,

    // Minimum interactive dimensions
    val minTouchTarget: Dp = 48.dp,
    val compactButtonHeight: Dp = 38.dp,
    val standardButtonHeight: Dp = 48.dp,
    val primaryCtaButtonHeight: Dp = 52.dp
)

val LocalMathSpacing = staticCompositionLocalOf { MathSpacingSystem() }

object MathSpacing {
    val current: MathSpacingSystem
        @Composable
        @ReadOnlyComposable
        get() = LocalMathSpacing.current

    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp

    val screenHorizontal: Dp = 16.dp
    val screenTop: Dp = 12.dp
    val screenBottom: Dp = 24.dp

    val cardPaddingCompact: Dp = 12.dp
    val cardPaddingStandard: Dp = 16.dp
    val cardPaddingHero: Dp = 18.dp

    val sectionSpacing: Dp = 14.dp
    val itemSpacing: Dp = 12.dp
    val compactItemSpacing: Dp = 8.dp
    val inlineIconSpacing: Dp = 8.dp

    val cornerShapeCompact = RoundedCornerShape(12.dp)
    val cornerShapeStandard = RoundedCornerShape(16.dp)
    val cornerShapeHero = RoundedCornerShape(20.dp)

    /**
     * Standard contentPadding for full-screen LazyColumn / LazyVerticalGrid containers.
     * Prevents top items from touching the TopAppBar and bottom items from touching the bottom edge,
     * while allowing items to scroll smoothly through top and bottom padding without clipping.
     */
    fun screenListContentPadding(
        horizontal: Dp = screenHorizontal,
        top: Dp = screenTop,
        bottom: Dp = screenBottom
    ): PaddingValues = PaddingValues(
        start = horizontal,
        top = top,
        end = horizontal,
        bottom = bottom
    )

    /**
     * BUG-61 FIX: Produces a 100% opaque (alpha = 1f) blended container color against the current
     * theme surface. Prevents the hardware shadow caster layer of ElevatedCard from bleeding
     * through semi-transparent containerColor values (such as `.copy(alpha = 0.5f)`).
     */
    @Composable
    @ReadOnlyComposable
    fun blendedContainerColor(
        containerColor: Color,
        fraction: Float = 0.55f
    ): Color = lerp(
        start = MaterialTheme.colorScheme.surface,
        stop = containerColor.copy(alpha = 1f),
        fraction = fraction.coerceIn(0f, 1f)
    )
}
