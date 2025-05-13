package org.lightscout.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

/**
 * A custom TopAppBar that follows Material 3 elevation guidelines with distinct tonal values for
 * overlapping surfaces.
 *
 * Reference: https://m3.material.io/styles/elevation/applying-elevation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElevatedTopAppBar(
        title: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        navigationIcon: @Composable () -> Unit = {},
        actions: @Composable RowScope.() -> Unit = {},
        windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
        scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val baseColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val primaryTint = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    val blendedColor = primaryTint.compositeOver(baseColor)

    val colors =
            TopAppBarDefaults.topAppBarColors(
                    containerColor = blendedColor,
                    scrolledContainerColor = blendedColor,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
            )

    TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets,
            colors = colors,
            scrollBehavior = scrollBehavior
    )
}

/** Helper function to blend two colors */
private fun blendColors(background: Color, overlay: Color): Color {
    return Color(
            red = overlay.red * overlay.alpha + background.red * (1 - overlay.alpha),
            green = overlay.green * overlay.alpha + background.green * (1 - overlay.alpha),
            blue = overlay.blue * overlay.alpha + background.blue * (1 - overlay.alpha),
            alpha = 1f
    )
}
