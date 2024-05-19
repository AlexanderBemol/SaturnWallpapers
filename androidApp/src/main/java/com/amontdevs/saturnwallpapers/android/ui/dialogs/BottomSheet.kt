package com.amontdevs.saturnwallpapers.android.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.android.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheetOptions(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = BottomSheetDefaults.windowInsets
    ) {
        BottomSheetContent()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BottomSheetContent() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row {
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Set as wallpaper for ...",
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
                name = "Lock Screen",
                drawableId =  R.drawable.ic_lock_screen,
                contentDescription = "Lock Screen"
            ) {}
            IconItemOption(
                name = "Home Screen",
                drawableId = R.drawable.ic_home_screen,
                contentDescription = "Home Screen"
            ) {}
            IconItemOption(
                name = "Both Screens",
                drawableId = R.drawable.ic_wallpaper,
                contentDescription = "Wallpaper Icon"
            ) {}
        }
        HorizontalDivider()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp).clickable {  }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_download),
                contentDescription = "Download",
                modifier = Modifier.height(24.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Download",
                style = MaterialTheme.typography.titleMedium
            )
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
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        FilledIconButton(
            onClick = { onClick() },
        ) {
            Icon(
                painter = painterResource(id = drawableId),
                contentDescription = contentDescription,
            )
        }
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
            sheetContent = {BottomSheetContent()},
        ){

        }
    }
}