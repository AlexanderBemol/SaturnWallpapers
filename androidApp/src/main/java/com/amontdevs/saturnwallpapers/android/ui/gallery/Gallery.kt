package com.amontdevs.saturnwallpapers.android.ui.gallery

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.android.R
import com.amontdevs.saturnwallpapers.android.ui.dialogs.wallpaperbottomsheet.BottomSheetOptions
import com.amontdevs.saturnwallpapers.android.ui.dialogs.wallpaperbottomsheet.WallpaperBottomSheetViewModel
import com.amontdevs.saturnwallpapers.android.ui.navigation.BottomNavigation
import com.amontdevs.saturnwallpapers.android.ui.navigation.Navigation
import com.amontdevs.saturnwallpapers.resources.Gallery.getFavorites
import com.amontdevs.saturnwallpapers.resources.Gallery.getTitle
import com.amontdevs.saturnwallpapers.utils.toDisplayableString
import com.amontdevs.saturnwallpapers.utils.toInstant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.getKoin
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.io.File

@Composable
fun GalleryScreen(
    navController: NavController,
    viewModel: GalleryViewModel
) {
    val openPicture = { photoId: String -> navController
        .navigate(Navigation.FULL_PICTURE.route + "/$photoId")
    }
    val onToggleFiltersVisibility = { viewModel.toggleFiltersVisibility() }
    val onSortAndFilter = { toggleAscSort: Boolean, toggleFilterByFav: Boolean ->
        viewModel.sortAndFilter(toggleFilterByFav, toggleAscSort)
    }
    val onBottomScroll = {
        viewModel.onBottomScroll()
    }
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    GalleryScreen(
        viewModel.galleryState,
        openPicture,
        onToggleFiltersVisibility,
        onSortAndFilter,
        onBottomScroll
    )
}

@Composable
fun GalleryScreen(
    galleryStateFlow: StateFlow<GalleryState>,
    onOpenPicture: (String) -> Unit,
    onToggleFiltersVisibility: () -> Unit,
    onSortAndFilter: (toggleAscSort: Boolean, toggleFilterByFav:Boolean) -> Unit,
    onBottomScroll: () -> Unit
){
    val galleryState = galleryStateFlow.collectAsState()
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.background
    ){
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = getTitle(),
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(
                    onClick = { onToggleFiltersVisibility() }
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_filter),
                        contentDescription = "Sort"
                    )
                }
            }

            AnimatedVisibility(visible = galleryState.value.areFiltersVisible) {
                Chips(galleryState.value, onSortAndFilter)
            }
            Spacer(modifier = Modifier.height(8.dp))
            GalleryGrid(
                onOpenPicture,
                galleryState.value.saturnPhotos,
                lazyStaggeredGridState,
                galleryState.value.isFetchingPhotos,
                onBottomScroll
            )
        }
    }
}

@Composable
fun Chips(
    galleryState: GalleryState,
    onSortAndFilter: (toggleAscSort: Boolean, toggleFilterByFav:Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        FilterChip(
            onClick = { onSortAndFilter(false, true) },
            label = {
                Text(getFavorites(), style = MaterialTheme.typography.labelMedium)
            },
            selected = galleryState.isFavoriteSelected,
            leadingIcon = if (galleryState.isFavoriteSelected) {
                {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = Icons.Filled.Favorite.name,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = Icons.Filled.FavoriteBorder.name,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        FilterChip(
            selected = galleryState.isAscSortSelected ,
            onClick = { onSortAndFilter(true, false) },
            label = { Text(text = if(galleryState.isAscSortSelected) "Ascendant" else "Descendant",
                style = MaterialTheme.typography.labelMedium) },
            leadingIcon = {
                Icon(imageVector = if(galleryState.isAscSortSelected) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown,
                contentDescription = "Arrow" )}
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryGrid(
    openPicture: (String) -> Unit,
    listOfData: List<SaturnPhoto>,
    lazyStaggeredGridState: LazyStaggeredGridState,
    isFetchingPhotos: Boolean,
    onBottomScroll: () -> Unit
) {
    var openBottomSheet by remember {
        mutableStateOf(false)
    }
    var selectedId by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(lazyStaggeredGridState.canScrollForward) {
        if (!lazyStaggeredGridState.canScrollForward && lazyStaggeredGridState.firstVisibleItemIndex > 0) {
            Log.d("Gallery", "Bottom Scroll Reached")
            onBottomScroll()
        }
    }
    LaunchedEffect(isFetchingPhotos) {
        if (isFetchingPhotos){
            lazyStaggeredGridState.animateScrollToItem(listOfData.size + 1)
        }
    }
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = lazyStaggeredGridState,
        reverseLayout = false,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(listOfData, key = {it.id.toString()}){ saturnPhoto ->
            ImageItem(
                saturnPhoto = saturnPhoto,
                modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(500)
                ),
                onClickMore = {
                    selectedId = saturnPhoto.id
                    openBottomSheet = true
                }
            ) {
                openPicture(saturnPhoto.id.toString())
                Log.d("Gallery", "Opening: ${saturnPhoto.id}")
            }
        }
        repeat(2){
            item {
                if (isFetchingPhotos) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(64.dp)
                    )
                }
            }
        }

    }
    if (openBottomSheet) {
        BottomSheetOptions(
           wallpaperBottomSheetViewModel = getKoin().get<WallpaperBottomSheetViewModel>(parameters = { parametersOf(selectedId) })
        ) {
            openBottomSheet = false
        }
    }
}

@Composable
fun ImageItem(
    saturnPhoto: SaturnPhoto,
    modifier: Modifier,
    onClickMore: () -> Unit,
    onItemClick: (String) -> Unit
){
    Column(
        modifier = modifier
    ) {
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
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onItemClick(saturnPhoto.id.toString()) }
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = saturnPhoto.timestamp.toInstant().toDisplayableString(),
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
            )
            if(saturnPhoto.mediaType != "video") {
                Image(
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = "settings",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.clickable {
                        onClickMore()
                    }
                )
            }
        }


    }
}

@Preview()
@Composable
fun GalleryPreview() {
    SaturnTheme(
        isDarkTheme = true
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigation()
            }
        ){
            GalleryScreen(
                galleryStateFlow = MutableStateFlow(GalleryState()),
                onOpenPicture = {},
                onToggleFiltersVisibility = { /*TODO*/ },
                onSortAndFilter = {_: Boolean,_:Boolean ->},
                onBottomScroll = {}
            )
            it
        }
    }
}
