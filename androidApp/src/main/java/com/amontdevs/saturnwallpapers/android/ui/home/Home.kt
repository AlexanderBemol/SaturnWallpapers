package com.amontdevs.saturnwallpapers.android.ui.home

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import coil.disk.DiskCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.android.ui.components.SaturnImage
import com.amontdevs.saturnwallpapers.android.ui.navigation.BottomNavigation
import com.amontdevs.saturnwallpapers.android.ui.navigation.Navigation
import com.amontdevs.saturnwallpapers.android.utils.getPrivateFile
import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import com.amontdevs.saturnwallpapers.model.SaturnPhotoMedia
import com.amontdevs.saturnwallpapers.model.SaturnPhotoMediaType
import com.amontdevs.saturnwallpapers.model.SaturnPhotoWithMedia
import com.amontdevs.saturnwallpapers.model.getMedia
import com.amontdevs.saturnwallpapers.resources.Home
import com.amontdevs.saturnwallpapers.utils.toDisplayableString
import com.amontdevs.saturnwallpapers.utils.toInstant
import com.amontdevs.saturnwallpapers.utils.toShortDisplayableString
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    val openPicture = { photoId: String ->
        val key = "image-$photoId"
        navController.navigate(Navigation.Details.title + "/$photoId,$key")
    }
    val openHomePicture = { photoId: String ->
        val key = "home-today-image-$photoId"
        navController.navigate(Navigation.Details.title + "/$photoId,$key")
    }
    val navigateToGalleryFavorites = {
        navController.navigate(Navigation.Gallery.title + "?isFavoriteState=true")
    }
    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }
    HomeScreen(
        viewModel,
        navigateToGallery = navigateToGalleryFavorites,
        openPicture = openPicture,
        openHomePicture,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToGallery: () -> Unit,
    openPicture: (String) -> Unit,
    openHomePicture: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    val homeState = viewModel.homeState.collectAsStateWithLifecycle()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.background,
    ){
        Column {
            TodayData(
                homeState.value,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope
            ) { openHomePicture(homeState.value.saturnPhoto?.saturnPhoto?.id.toString()) }
            Spacer(modifier = Modifier.height(16.dp))
            FavoritePhotos(
                favoritesPhotos = homeState.value.favoritePhotos,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onListClick = navigateToGallery,
                onItemClick = openPicture
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TodayData(
    homeState: HomeState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClick: () -> Unit
) {
    Text(
        text = Home.getHomeTitle(),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Start
    )
    Text(
        text = homeState.saturnPhoto?.saturnPhoto?.timestamp?.toInstant()?.toDisplayableString() ?: "",
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(16.dp))
    with(sharedTransitionScope) {
        homeState.saturnPhoto?.let { saturnPhoto ->
            val saturnMedia = saturnPhoto.getMedia(
                if (saturnPhoto.saturnPhoto.isVideo) SaturnPhotoMediaType.VIDEO
                else SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE
            )
            if (saturnMedia != null) {
                SaturnImage(
                    filePath = saturnMedia.filepath,
                    contentDescription = saturnPhoto.saturnPhoto.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(
                                key = "home-today-image-${saturnPhoto.saturnPhoto.id}"
                            ),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .clickable { onClick() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = saturnPhoto.saturnPhoto.title,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.sharedElement(
                        sharedTransitionScope.rememberSharedContentState(
                            key = "title-${saturnPhoto.saturnPhoto.id}"
                        ),
                        animatedVisibilityScope = animatedContentScope
                    )
                )
            }

        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FavoritePhotos(
    favoritesPhotos: List<SaturnPhotoWithMedia>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
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
                it.getMedia(
                    if (it.saturnPhoto.isVideo) SaturnPhotoMediaType.VIDEO
                    else SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE
                )?.let { media ->
                    FavoriteItem(
                        saturnPhoto = it.saturnPhoto,
                        saturnPhotoMedia = media,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope
                    ) { onItemClick(it.saturnPhoto.id.toString()) }
                }
            }
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FavoriteItem(
    saturnPhoto: SaturnPhoto,
    saturnPhotoMedia: SaturnPhotoMedia,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClick: () -> Unit
) {
    Column{
        with(sharedTransitionScope) {
            SaturnImage(
                filePath = saturnPhotoMedia.filepath,
                contentDescription = saturnPhoto.title,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .width(100.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "image-${saturnPhoto.id}"),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .clickable { onClick() }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = saturnPhoto.timestamp.toInstant().toShortDisplayableString(),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
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