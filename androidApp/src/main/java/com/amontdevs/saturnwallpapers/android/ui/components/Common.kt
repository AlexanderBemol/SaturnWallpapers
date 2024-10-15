package com.amontdevs.saturnwallpapers.android.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.amontdevs.saturnwallpapers.android.utils.getPrivateFile
import kotlinx.coroutines.Dispatchers

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

@Composable
fun SaturnImage(
    filePath: String,
    contentDescription: String,
    contentScale: ContentScale,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val imageRequest = remember(filePath) {
        ImageRequest.Builder(context)
            .dispatcher(Dispatchers.IO)
            .data(context.getPrivateFile(filePath))
            .memoryCacheKey(filePath)
            .diskCacheKey(filePath)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .size(Size.ORIGINAL)
            .build()

    }
    val asyncPainter = rememberAsyncImagePainter(imageRequest)
    Image(
        painter = asyncPainter,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
}