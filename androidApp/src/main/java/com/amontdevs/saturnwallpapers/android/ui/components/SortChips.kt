package com.amontdevs.saturnwallpapers.android.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SortChips(
    sortChips: List<SortChip>,
    onDescSort:(Boolean) -> Unit
) {
    LazyRow {
        items(
            items = sortChips,
            key = {it.id})
        { chip ->
            FilterChip(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .animateItemPlacement(
                        animationSpec = tween(durationMillis = 500)
                    ),
                selected = chip.isSelected,
                leadingIcon = { Icon(
                        imageVector = chip.icon,
                        contentDescription = ""
                    ) },
                onClick = { onDescSort(chip.id == 1) },
                label = { Text(text = chip.text, style = MaterialTheme.typography.labelSmall) }
            )
        }
    }
}

data class SortChip(
    val id: Int,
    var isSelected: Boolean,
    val text: String,
    val icon: ImageVector
)