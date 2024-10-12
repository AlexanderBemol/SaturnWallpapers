package com.amontdevs.saturnwallpapers.android.ui.gallery

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.android.R
import com.amontdevs.saturnwallpapers.android.ui.components.SaturnImage
import com.amontdevs.saturnwallpapers.android.ui.dialogs.wallpaperbottomsheet.BottomSheetOptions
import com.amontdevs.saturnwallpapers.android.ui.dialogs.wallpaperbottomsheet.WallpaperBottomSheetViewModel
import com.amontdevs.saturnwallpapers.android.ui.navigation.BottomNavigation
import com.amontdevs.saturnwallpapers.android.ui.navigation.Navigation
import com.amontdevs.saturnwallpapers.model.SaturnPhotoMediaType
import com.amontdevs.saturnwallpapers.model.SaturnPhotoWithMedia
import com.amontdevs.saturnwallpapers.model.getMedia
import com.amontdevs.saturnwallpapers.resources.Gallery.getFavorites
import com.amontdevs.saturnwallpapers.resources.Gallery.getTitle
import com.amontdevs.saturnwallpapers.utils.toDisplayableString
import com.amontdevs.saturnwallpapers.utils.toInstant
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.getKoin
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GalleryScreen(
    navController: NavController,
    viewModel: GalleryViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    val openPicture = { photoId: Long ->
        navController.navigate(Navigation.Details.title + "/$photoId")
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
        sharedTransitionScope,
        animatedContentScope,
        openPicture,
        onToggleFiltersVisibility,
        onSortAndFilter,
        onBottomScroll
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GalleryScreen(
    galleryStateFlow: StateFlow<GalleryState>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onOpenPicture: (Long) -> Unit,
    onToggleFiltersVisibility: () -> Unit,
    onSortAndFilter: (toggleAscSort: Boolean, toggleFilterByFav:Boolean) -> Unit,
    onBottomScroll: () -> Unit
){
    Log.d("Gallery", "Gallery Screen")
    val galleryState = galleryStateFlow.collectAsStateWithLifecycle()
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
            if (galleryState.value.isLoaded) {
                GalleryGrid(
                    sharedTransitionScope,
                    animatedContentScope,
                    onOpenPicture,
                    galleryState.value.saturnPhotos,
                    lazyStaggeredGridState,
                    galleryState.value.isFetchingPhotos,
                    onBottomScroll
                )
            }
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
        /* Temporary disabled
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
        */
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GalleryGrid(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    openPicture: (Long) -> Unit,
    listOfData: List<SaturnPhotoWithMedia>,
    lazyStaggeredGridState: LazyStaggeredGridState,
    isFetchingPhotos: Boolean,
    onBottomScroll: () -> Unit
) {
    Log.d("Gallery", "Grid")
    var openBottomSheet by remember {
        mutableStateOf(false)
    }
    var selectedId by remember {
        mutableLongStateOf(0)
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
        items(listOfData, key = {it.saturnPhoto.id.toString()}){ saturnPhoto ->
            ImageItem(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                saturnPhoto = saturnPhoto,
                modifier = Modifier
                    .animateItem(
                        fadeInSpec = tween(500),
                        fadeOutSpec = tween(500),
                        placementSpec = tween(500)
                    ),
                onClickMore = {
                    selectedId = saturnPhoto.saturnPhoto.id
                    openBottomSheet = true
                }
            ) {
                openPicture(saturnPhoto.saturnPhoto.id)
                Log.d("Gallery", "Opening: ${saturnPhoto.saturnPhoto.id}")
            }
        }
        if (isFetchingPhotos) {
            repeat(4){
                item {
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ImageItem(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    saturnPhoto: SaturnPhotoWithMedia,
    modifier: Modifier = Modifier,
    onClickMore: () -> Unit,
    onItemClick: (String) -> Unit
){
    val saturnMedia = saturnPhoto.getMedia(SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE)
    if (saturnMedia != null) {
        Column(
            modifier = modifier
        ) {
            with(sharedTransitionScope) {
                SaturnImage(
                    filePath = saturnMedia.filepath,
                    contentDescription = saturnPhoto.saturnPhoto.title,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "image-${saturnPhoto.saturnPhoto.id}"),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .clickable { onItemClick(saturnPhoto.saturnPhoto.id.toString()) }
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(
                    text = saturnPhoto.saturnPhoto.timestamp.toInstant().toDisplayableString(),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically)
                )
                if(!saturnPhoto.saturnPhoto.isVideo) {
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

}

@OptIn(ExperimentalSharedTransitionApi::class)
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
            SharedTransitionLayout {
                /*
                GalleryScreen(
                    galleryStateFlow = MutableStateFlow(GalleryState()),
                    onOpenPicture = {},
                    onToggleFiltersVisibility = { /*TODO*/ },
                    onSortAndFilter = {_: Boolean,_:Boolean ->},
                    onBottomScroll = {}
                )
                 */
            }
            it
        }
    }
}
