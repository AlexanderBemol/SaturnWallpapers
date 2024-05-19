package com.amontdevs.saturnwallpapers.android.ui.photodetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.android.R
import com.amontdevs.saturnwallpapers.android.ui.components.ActionChip
import com.amontdevs.saturnwallpapers.android.ui.components.FloatingTransparentButton
import com.amontdevs.saturnwallpapers.utils.toAPODUrl
import com.amontdevs.saturnwallpapers.utils.toDisplayableString
import com.amontdevs.saturnwallpapers.utils.toInstant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File


@Composable
fun FullPictureViewScreen(navController: NavController, viewModel: PhotoDetailViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    FullPictureViewScreen(
        viewModel.fullViewState,
        onFavoriteClick = { viewModel.onFavoriteClick() },
        navigateBack = { navController.navigateUp() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPictureViewScreen(
    photoDetailStateFlow: StateFlow<PhotoDetailState>,
    onFavoriteClick: () -> Unit,
    navigateBack: () -> Unit
) {
    val photoDetailState = photoDetailStateFlow.collectAsState()
    val photoFilepath = if(photoDetailState.value.isHighQuality && photoDetailState.value.saturnPhoto?.mediaType == "image")
        photoDetailState.value.saturnPhoto?.highDefinitionPath.toString()
        else photoDetailState.value.saturnPhoto?.regularPath.toString()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )
    BottomSheetScaffold(
        sheetContent = { BottomSheetInformationContent(photoDetailState) },
        scaffoldState = bottomSheetScaffoldState
    ) {
        ImageContainer(
            filePath = photoFilepath,
            imageDescription = photoDetailState.value.saturnPhoto?.title.toString(),
            isFavorite = photoDetailState.value.saturnPhoto?.isFavorite == true,
            onFavoriteClick = onFavoriteClick,
            goBack = navigateBack
        )
    }
}

@Composable
fun ImageContainer(
    filePath: String,
    imageDescription: String,
    isFavorite: Boolean,
    goBack: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Box {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(
                    File(
                        LocalContext.current.getDir("images", Context.MODE_PRIVATE),
                        filePath
                    )
                )
                .build(),
            contentDescription = imageDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxHeight()
        )
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
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            ) { goBack() }

            FloatingTransparentButton(
                icon = {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite
                        else Icons.Filled.FavoriteBorder,
                        modifier = Modifier.padding(8.dp),
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            ) { onFavoriteClick() }
        }

    }
}

@Composable
fun BottomSheetInformationContent(photoDetailState: State<PhotoDetailState>) {
    val context = LocalContext.current
    val openVideo = {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(photoDetailState.value.saturnPhoto?.videoUrl.toString())
        context.startActivity(intent)
    }
    val openWebsite = {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(photoDetailState.value.saturnPhoto?.timestamp?.toInstant()?.toAPODUrl())
        context.startActivity(intent)
    }

    Column(
        Modifier.padding(horizontal = 16.dp)
    ) {
        BottomSheetHeader(
            title = photoDetailState.value.saturnPhoto?.title.toString(),
            displayDate = photoDetailState.value.saturnPhoto?.timestamp?.toInstant()?.toDisplayableString().toString(),
            authors = photoDetailState.value.saturnPhoto?.authors,
            isVideo = photoDetailState.value.saturnPhoto?.mediaType.toString() == "video",
            openVideo,
            openWebsite
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        DescriptionContent(
            description = photoDetailState.value.saturnPhoto?.description.toString()
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
fun BottomSheetHeader(
    title: String,
    displayDate: String,
    authors: String?,
    isVideo: Boolean = false,
    openVideo: () -> Unit,
    openWebsite: () -> Unit,
) {
    Spacer(modifier = Modifier.height(8.dp))
    InformationRow(
        vector = Icons.Filled.Info,
        vectorDescription = "Information",
        text = title
    )
    InformationRow(
        vector = Icons.Filled.DateRange,
        vectorDescription = "Calendar",
        text = displayDate
    )
    if (authors != null && authors!= "null") {
        InformationRow(
            vector = Icons.Filled.Face,
            vectorDescription = "Face",
            text = authors
        )
    }
    Row(
        Modifier.horizontalScroll(rememberScrollState())
    ) {
        ActionChip(
            text = "Information",
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_world),
                    contentDescription = "Website"
                )
            }
        ) {
            openWebsite()
        }
        if (!isVideo) {
            ActionChip(
                text = "Download",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_download),
                        contentDescription = "Download"
                    )
                }
            ) {

            }
            ActionChip(
                text = "Set Wallpaper",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wallpaper),
                        contentDescription = "Wallpaper"
                    )
                }
            ) {

            }
        } else {
            ActionChip(
                text = "Play Video",
                icon = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play"
                    )
                }
            ) {
                openVideo()
            }
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
        onTextLayout = {
            if (it.lineCount > minimumLineLength - 1) {
                if (it.isLineEllipsized(minimumLineLength - 1))
                    showReadMoreButtonState = true
            }
        }
    )
    if (showReadMoreButtonState) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isExpanded) "Read Less" else "Read More",
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

@Preview()
@Composable
fun FullPictureViewPreview() {
    SaturnTheme(
        isDarkTheme = false
    ) {
        SaturnTheme(
            isDarkTheme = true
        ) {
            Scaffold {
                FullPictureViewScreen(
                    photoDetailStateFlow = MutableStateFlow(PhotoDetailState()),
                    onFavoriteClick = {},
                    navigateBack = {}
                )
                it
            }
        }
    }
}