package com.amontdevs.saturnwallpapers.android.ui.photodetail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.android.R
import com.amontdevs.saturnwallpapers.android.ui.components.ActionChip
import com.amontdevs.saturnwallpapers.android.ui.components.FloatingTransparentButton
import com.amontdevs.saturnwallpapers.android.ui.components.SaturnImage
import com.amontdevs.saturnwallpapers.android.ui.dialogs.wallpaperbottomsheet.BottomSheetOptions
import com.amontdevs.saturnwallpapers.android.ui.dialogs.wallpaperbottomsheet.WallpaperBottomSheetViewModel
import com.amontdevs.saturnwallpapers.model.SaturnPhotoMediaType
import com.amontdevs.saturnwallpapers.model.getMedia
import com.amontdevs.saturnwallpapers.resources.DetailsScreen
import com.amontdevs.saturnwallpapers.utils.toAPODUrl
import com.amontdevs.saturnwallpapers.utils.toDisplayableString
import com.amontdevs.saturnwallpapers.utils.toInstant
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.getKoin
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FullPictureViewScreen(
    navController: NavController,
    viewModel: PhotoDetailViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    FullPictureViewScreen(
        viewModel.fullViewState,
        sharedTransitionScope,
        animatedContentScope,
        onFavoriteClick = { viewModel.onFavoriteClick() },
        navigateBack = { navController.navigateUp() }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FullPictureViewScreen(
    photoDetailStateFlow: StateFlow<PhotoDetailState>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onFavoriteClick: () -> Unit,
    navigateBack: () -> Unit,
) {
    val photoDetailState = photoDetailStateFlow.collectAsState()
    val displayFullscreen = remember { mutableStateOf(false) }
    val onFullscreenClick = {
        displayFullscreen.value = true
    }

    val regularMedia = photoDetailState.value.saturnPhoto?.getMedia(
        if(photoDetailState.value.saturnPhoto!!.saturnPhoto.isVideo) SaturnPhotoMediaType.VIDEO
        else SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE
    )

    if (!displayFullscreen.value) {
        if (photoDetailState.value.saturnPhoto != null) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                ImageContainer(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    filePath = regularMedia?.filepath.toString(),
                    imageId = photoDetailState.value.saturnPhoto?.saturnPhoto?.id.toString(),
                    imageDescription = photoDetailState.value.saturnPhoto?.saturnPhoto?.title.toString(),
                    isFavorite = photoDetailState.value.saturnPhoto?.saturnPhoto?.isFavorite == true,
                    isImage = photoDetailState.value.saturnPhoto?.saturnPhoto?.isVideo == false,
                    onFavoriteClick = onFavoriteClick,
                    goBack = navigateBack,
                    onFullscreenClick = onFullscreenClick
                )
                BottomInformationContent(photoDetailState)
            }
        }
    } else {
        Dialog(
            onDismissRequest = { displayFullscreen.value = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            val filePath = photoDetailState.value.saturnPhoto
                ?.getMedia(SaturnPhotoMediaType.HIGH_QUALITY_IMAGE) ?: regularMedia
            SaturnImage(
                filePath = filePath?.filepath.toString(),
                contentDescription = photoDetailState.value.saturnPhoto?.saturnPhoto?.title.toString(),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { displayFullscreen.value = false }
            )
        }

    }


}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ImageContainer(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    filePath: String,
    imageId: String,
    imageDescription: String,
    isFavorite: Boolean,
    isImage: Boolean,
    goBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    onFullscreenClick: () -> Unit
) {
    Box{
        with(sharedTransitionScope) {
            SaturnImage(
                filePath = filePath,
                contentDescription = imageDescription,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "image-$imageId"),
                        animatedVisibilityScope = animatedContentScope
                    )
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            FloatingTransparentButton(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = DetailsScreen.getBackButton(),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            ) { goBack() }

            Column {
                FloatingTransparentButton(
                    icon = {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite
                            else Icons.Filled.FavoriteBorder,
                            modifier = Modifier.padding(8.dp),
                            contentDescription = DetailsScreen.getFavoriteButton(),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                ) { onFavoriteClick() }
                Spacer(modifier = Modifier.height(8.dp))
                if (isImage) {
                    FloatingTransparentButton(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_whole_screen),
                                modifier = Modifier.padding(8.dp),
                                contentDescription = DetailsScreen.getFavoriteButton(),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    ) { onFullscreenClick() }
                }
            }
        }
    }
}

@Composable
fun BottomInformationContent(photoDetailState: State<PhotoDetailState>) {
    val context = LocalContext.current
    val openVideo = {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(photoDetailState.value.saturnPhoto?.saturnPhoto?.videoUrl.toString())
        context.startActivity(intent)
    }
    val openWebsite = {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(photoDetailState.value.saturnPhoto?.saturnPhoto?.timestamp?.toInstant()?.toAPODUrl())
        context.startActivity(intent)
    }

    Column(
        Modifier.padding(horizontal = 16.dp)
    ) {
        BottomHeader(
            selectedId = photoDetailState.value.saturnPhoto?.saturnPhoto?.id ?: 0,
            title = photoDetailState.value.saturnPhoto?.saturnPhoto?.title.toString(),
            displayDate = photoDetailState.value.saturnPhoto?.saturnPhoto?.timestamp?.toInstant()?.toDisplayableString().toString(),
            authors = photoDetailState.value.saturnPhoto?.saturnPhoto?.authors?.replace("\n","").toString(),
            isVideo = photoDetailState.value.saturnPhoto?.saturnPhoto?.isVideo == false,
            openVideo,
            openWebsite
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        DescriptionContent(
            description = photoDetailState.value.saturnPhoto?.saturnPhoto?.description.toString()
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
fun BottomHeader(
    selectedId: Long,
    title: String,
    displayDate: String,
    authors: String?,
    isVideo: Boolean = false,
    openVideo: () -> Unit,
    openWebsite: () -> Unit,
) {
    var openBottomSheet by remember {
        mutableStateOf(false)
    }
    Spacer(modifier = Modifier.height(8.dp))
    InformationRow(
        vector = Icons.Filled.Info,
        vectorDescription = Icons.Filled.Info.name,
        text = title
    )
    InformationRow(
        vector = Icons.Filled.DateRange,
        vectorDescription = Icons.Filled.DateRange.name,
        text = displayDate
    )
    if (authors != null && authors!= "null") {
        InformationRow(
            vector = Icons.Filled.Face,
            vectorDescription = Icons.Filled.Face.name,
            text = authors
        )
    }
    Row(
        Modifier.horizontalScroll(rememberScrollState())
    ) {
        ActionChip(
            text = DetailsScreen.getInformationButton(),
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_world),
                    contentDescription = DetailsScreen.getWebsiteIcon()
                )
            }
        ) { openWebsite() }
        if (!isVideo) {
            ActionChip(
                text = DetailsScreen.getSetWallpaperButton(),
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wallpaper),
                        contentDescription = DetailsScreen.getSetWallpaperButton()
                    )
                }
            ) {
                openBottomSheet = true
            }
        } else {
            ActionChip(
                text = DetailsScreen.getPlayVideoButton(),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = DetailsScreen.getPlayVideoButton()
                    )
                }
            ) { openVideo() }
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
fun DescriptionContent(
    description: String
) {
    val minimumLineLength = 5
    var isExpanded by remember { mutableStateOf(false) }
    var showReadMoreButtonState by remember { mutableStateOf(false) }
    val maxLines = if (isExpanded) 200 else minimumLineLength
    Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines,
        modifier = Modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        onTextLayout = {
            if (it.lineCount > minimumLineLength - 1) {
                if (it.isLineEllipsized(minimumLineLength - 1))
                    showReadMoreButtonState = true
            }
        }
    )
    AnimatedVisibility(visible = showReadMoreButtonState) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isExpanded) DetailsScreen.getReadLessButton()
            else DetailsScreen.getReadMoreButton(),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.clickable {
                isExpanded = !isExpanded
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun InformationRow(
    vector: ImageVector,
    vectorDescription: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = vector, contentDescription = vectorDescription)
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview()
@Composable
fun FullPictureViewPreview() {
    SharedTransitionLayout {
        SaturnTheme(
            isDarkTheme = true
        ) {
            Scaffold {
                /*
                FullPictureViewScreen(
                    photoDetailStateFlow = MutableStateFlow(PhotoDetailState()),
                    this@SharedTransitionLayout,
                    this@composable,
                    onFavoriteClick = {},
                    navigateBack = {}
                )
                */
                it
            }
        }
    }
}