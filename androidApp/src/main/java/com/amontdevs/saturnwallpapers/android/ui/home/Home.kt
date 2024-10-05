package com.amontdevs.saturnwallpapers.android.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.android.ui.navigation.BottomNavigation
import com.amontdevs.saturnwallpapers.android.ui.navigation.Navigation
import com.amontdevs.saturnwallpapers.android.utils.getPrivateFile
import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import com.amontdevs.saturnwallpapers.resources.Home
import com.amontdevs.saturnwallpapers.utils.toDisplayableString
import com.amontdevs.saturnwallpapers.utils.toInstant
import kotlinx.coroutines.Dispatchers

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val openPicture = { photoId: String -> navController
        .navigate(Navigation.Details.title + "/$photoId")
    }
    val navigateToGalleryFavorites = {
        navController.navigate(Navigation.Gallery.title + "?isFavoriteState=true")
    }
    HomeScreen(
        viewModel,
        navigateToGallery = navigateToGalleryFavorites,
        openPicture = openPicture
    )
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToGallery: () -> Unit,
    openPicture: (String) -> Unit
) {
    val homeState = viewModel.homeState.collectAsStateWithLifecycle()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.background,
    ){
        Column {
            TodayData(homeState.value) { openPicture(homeState.value.saturnPhoto?.id.toString()) }
            Spacer(modifier = Modifier.height(16.dp))
            LastPhotos(
                favoritesPhotos = homeState.value.favoritePhotos,
                onListClick = navigateToGallery,
                onItemClick = openPicture
            )
        }
    }
}

@Composable
fun TodayData(
    homeState: HomeState,
    onClick: () -> Unit
) {
    Text(
        text = Home.getHomeTitle(),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Start
    )
    Text(
        text = homeState.saturnPhoto?.timestamp?.toInstant()?.toDisplayableString() ?: "",
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(16.dp))
    val context = LocalContext.current
    homeState.saturnPhoto?.let { saturnPhoto ->
        val imageRequest = remember(saturnPhoto.regularPath) {
            ImageRequest.Builder(context)
                .dispatcher(Dispatchers.IO)
                .data(context.getPrivateFile(saturnPhoto.regularPath))
                .memoryCacheKey(saturnPhoto.regularPath)
                .diskCacheKey(saturnPhoto.regularPath)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .size(Size.ORIGINAL)
                .build()
        }
        val asyncPainter = rememberAsyncImagePainter(imageRequest)
        Image(
            painter = asyncPainter,
            contentDescription = saturnPhoto.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() }
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = homeState.saturnPhoto?.title.toString(),
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
fun LastPhotos(
    favoritesPhotos: List<SaturnPhoto>,
    onListClick: () -> Unit,
    onItemClick: (String) -> Unit
) {
    if (favoritesPhotos.isNotEmpty()){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onListClick() }
        ) {
            Text(
                text = Home.getHomeFavorites(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
            )
            Row {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = Icons.AutoMirrored.Filled.KeyboardArrowRight.name,
                    Modifier.size(32.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favoritesPhotos){
                FavoriteItem(saturnPhoto = it) { onItemClick(it.id.toString()) }
            }
        }
    }

}

@Composable
fun FavoriteItem(
    saturnPhoto: SaturnPhoto,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val imageRequest = remember(saturnPhoto.regularPath) {
        ImageRequest.Builder(context)
            .dispatcher(Dispatchers.IO)
            .data(context.getPrivateFile(saturnPhoto.regularPath))
            .memoryCacheKey(saturnPhoto.regularPath)
            .diskCacheKey(saturnPhoto.regularPath)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .size(Size.ORIGINAL)
            .build()
    }
    val asyncPainter = rememberAsyncImagePainter(imageRequest)
    Column {
        Image(
            painter = asyncPainter,
            contentDescription = saturnPhoto.title,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .width(100.dp)
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = saturnPhoto.timestamp.toInstant().toDisplayableString(),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview
@Composable
fun HomePreview() {
    SaturnTheme(
        isDarkTheme = true,
        isDynamicColor = true
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigation()
            }
        ){
            it
        }
    }
}