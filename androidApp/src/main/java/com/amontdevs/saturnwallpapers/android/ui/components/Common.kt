package com.amontdevs.saturnwallpapers.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.amontdevs.saturnwallpapers.android.R

@Composable
fun FloatingTransparentButton(
    icon: @Composable () -> Unit, onClick: () -> Unit
) {
    Surface(
        Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
    ) {
        icon()
    }
}


@Composable
fun ActionChip(
    text: String, icon: @Composable () -> Unit, onClick: () -> Unit
) {
    AssistChip(
        onClick = { onClick() },
        leadingIcon = { icon() },
        label = {
            Text(
                text = text, style = MaterialTheme.typography.labelSmall
            )
        },
        modifier = Modifier.padding(end = 8.dp)
    )
}