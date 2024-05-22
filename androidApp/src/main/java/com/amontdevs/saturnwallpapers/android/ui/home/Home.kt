package com.amontdevs.saturnwallpapers.android.ui.home

import android.content.Context
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
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.android.ui.navigation.BottomNavItem
import com.amontdevs.saturnwallpapers.android.ui.navigation.BottomNavigation
import com.amontdevs.saturnwallpapers.android.ui.navigation.Navigation
import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import com.amontdevs.saturnwallpapers.resources.Home
import com.amontdevs.saturnwallpapers.utils.toDisplayableString
import com.amontdevs.saturnwallpapers.utils.toInstant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val openPicture = { photoId: String -> navController
        .navigate(Navigation.FULL_PICTURE.route + "/$photoId")
    }
    val navigateToGalleryFavorites = {
        navController.navigate(BottomNavItem.Gallery.title + "?isFavoriteState=true")
    }
    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }
    HomeScreen(
        viewModel.homeState,
        navigateToGallery = navigateToGalleryFavorites,
        openPicture = openPicture
    )
}

@Composable
fun HomeScreen(
    homeStateFlow: StateFlow<HomeState>,
    navigateToGallery: () -> Unit,
    openPicture: (String) -> Unit
) {
    val homeState = homeStateFlow.collectAsState()
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
    AsyncImage(
        model = ImageRequest
            .Builder(LocalContext.current)
            .data(
                File(
                    LocalContext.current.getDir("images", Context.MODE_PRIVATE),
                    homeState.saturnPhoto?.regularPath.toString()
                )
            )
            .build(),
        contentDescription = homeState.saturnPhoto?.title.toString(),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    )
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
    Column {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(
                    File(
                        LocalContext.current.getDir("images", Context.MODE_PRIVATE),
                        saturnPhoto.regularPath
                    )
                )
                .build(),
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
            HomeScreen(
                homeStateFlow = MutableStateFlow(HomeState()),
                navigateToGallery = {},
                openPicture = { _: String ->}
            )
            it
        }
    }
}