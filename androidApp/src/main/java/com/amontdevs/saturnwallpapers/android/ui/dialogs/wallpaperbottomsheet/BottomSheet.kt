package com.amontdevs.saturnwallpapers.android.ui.dialogs.wallpaperbottomsheet

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.android.R
import com.amontdevs.saturnwallpapers.model.MediaQuality
import com.amontdevs.saturnwallpapers.model.SaturnPhotoMediaType
import com.amontdevs.saturnwallpapers.model.WallpaperScreen
import com.amontdevs.saturnwallpapers.model.getMedia
import com.amontdevs.saturnwallpapers.resources.GalleryBottomMenu.getDownload
import com.amontdevs.saturnwallpapers.resources.GalleryBottomMenu.getDownloadHigh
import com.amontdevs.saturnwallpapers.resources.GalleryBottomMenu.getDownloadNormal
import com.amontdevs.saturnwallpapers.resources.GalleryBottomMenu.getSetBothScreens
import com.amontdevs.saturnwallpapers.resources.GalleryBottomMenu.getSetHomeScreen
import com.amontdevs.saturnwallpapers.resources.GalleryBottomMenu.getSetLockScreen
import com.amontdevs.saturnwallpapers.resources.GalleryBottomMenu.getSetWallpaperFor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheetOptions(
    wallpaperBottomSheetViewModel: WallpaperBottomSheetViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val wallpaperBottomSheetStateFlow = wallpaperBottomSheetViewModel.wallpaperBottomSheetState
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        contentWindowInsets = {BottomSheetDefaults.windowInsets}
    ) {
        BottomSheetContent(
            wallpaperBottomSheetStateFlow = wallpaperBottomSheetStateFlow,
            onWallpaperHomeClick = {
                wallpaperBottomSheetViewModel.setWallpaper(WallpaperScreen.HOME_SCREEN)
                                   },
            onWallpaperLockClick = {
                wallpaperBottomSheetViewModel.setWallpaper(WallpaperScreen.LOCK_SCREEN)
            },
            onWallpaperBothClick = {
                wallpaperBottomSheetViewModel.setWallpaper(WallpaperScreen.ALL)
            },
            onDownloadNormalClick = {
                wallpaperBottomSheetViewModel.downloadPhoto(MediaQuality.NORMAL)
            },
            onDownloadHighClick = {
                wallpaperBottomSheetViewModel.downloadPhoto(MediaQuality.HIGH)
            },
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BottomSheetContent(
    wallpaperBottomSheetStateFlow: StateFlow<WallpaperBottomSheetState>,
    onWallpaperHomeClick: () -> Unit,
    onWallpaperLockClick: () -> Unit,
    onWallpaperBothClick: () -> Unit,
    onDownloadNormalClick: () -> Unit,
    onDownloadHighClick: () -> Unit,
    onDismiss: () -> Unit = {}
){
    val wallpaperBottomSheetState = wallpaperBottomSheetStateFlow.collectAsStateWithLifecycle()
    val isHQAvailable = wallpaperBottomSheetState.value.saturnPhoto.getMedia(SaturnPhotoMediaType.HIGH_QUALITY_IMAGE) != null
    if(wallpaperBottomSheetState.value.displayToast){
        Toast.makeText(
            LocalContext.current,
            wallpaperBottomSheetState.value.toastMessage,
            Toast.LENGTH_SHORT
        ).show()
        onDismiss()
    }
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row {
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = getSetWallpaperFor(),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconItemOption(
                name = getSetLockScreen(),
                drawableId =  R.drawable.ic_lock_screen,
                contentDescription = getSetLockScreen(),
                isLoading = wallpaperBottomSheetState.value.isSetWallpaperLockScreenLoading
            ) { onWallpaperLockClick() }
            IconItemOption(
                name = getSetHomeScreen(),
                drawableId = R.drawable.ic_home_screen,
                contentDescription = getSetHomeScreen(),
                isLoading = wallpaperBottomSheetState.value.isSetWallpaperHomeScreenLoading
            ) { onWallpaperHomeClick() }
            IconItemOption(
                name = getSetBothScreens(),
                drawableId = R.drawable.ic_wallpaper,
                contentDescription = getSetBothScreens(),
                isLoading = wallpaperBottomSheetState.value.isSetWallpaperBothLoading
            ) { onWallpaperBothClick() }
        }
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = getDownload(),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ){
            IconItemOption(
                name = getDownloadNormal(),
                drawableId =  R.drawable.ic_download,
                contentDescription = getDownloadNormal(),
                isLoading = wallpaperBottomSheetState.value.isDownloadNormalLoading
            ) { onDownloadNormalClick() }
            if (isHQAvailable) {
                IconItemOption(
                    name = getDownloadHigh(),
                    drawableId =  R.drawable.ic_hq,
                    contentDescription = getDownloadHigh(),
                    isLoading = wallpaperBottomSheetState.value.isDownloadHQLoading
                ) { onDownloadHighClick() }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
    Spacer(
        Modifier.windowInsetsBottomHeight(
            WindowInsets.navigationBarsIgnoringVisibility
        )
    )
}

@Composable
fun IconItemOption(
    name: String,
    drawableId: Int,
    contentDescription: String,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if(!isLoading) Modifier.clickable { onClick() } else Modifier
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(
                    painter = painterResource(id = drawableId),
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview()
@Composable
fun BottomSheetPreview() {
    val state =  rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded)
    )
    SaturnTheme(
        isDarkTheme = true
    ) {
        BottomSheetScaffold(
            scaffoldState = state,
            //sheetContent = {BottomSheetContent()},
            sheetContent = { BottomSheetContent(
                wallpaperBottomSheetStateFlow = MutableStateFlow(WallpaperBottomSheetState()),
                onWallpaperHomeClick = {},
                onWallpaperLockClick = {},
                onWallpaperBothClick = {},
                onDownloadNormalClick = {},
                onDownloadHighClick = {}
            ) },
        ){

        }
    }
}