package com.codingwithumair.app.darklify

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codingwithumair.app.darklify.ui.mainScreen.MainScreen
import com.codingwithumair.app.darklify.ui.mainScreen.MainViewModel
import com.codingwithumair.app.darklify.ui.mainScreen.components.WallpaperSettingDialog
import com.codingwithumair.app.darklify.ui.theme.DarklifyTheme
import com.codingwithumair.app.darklify.utils.WallpaperUtils
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			val permission = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
			if(permission == PackageManager.PERMISSION_DENIED){
				requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
			}
		}
		setContent {
			DarklifyTheme {

				var showDialog by remember {
					mutableStateOf(false)
				}

				LaunchedEffect(key1 = showDialog) {
					delay(5000)
					showDialog = false
				}

				Box(
					modifier = Modifier.fillMaxSize()
				) {
					Surface(
						modifier = Modifier.fillMaxSize(),
						color = MaterialTheme.colorScheme.background
					) {

						val viewModel: MainViewModel = viewModel(factory = MainViewModel.factory)
						val uiState by viewModel.uiState.collectAsState()

						val application = remember {
							this@MainActivity.application as DarklifyApp
						}

						val darkMode = isSystemInDarkTheme()

						LaunchedEffect(key1 = uiState.enabled) {
							delay(300) // It takes some time to read values from dataStore
							val shouldStartService = uiState.enabled
							MainService.startOrStopService(this@MainActivity, shouldStartService)
						}

						MainScreen(
							enabled = uiState.enabled,
							onEnabledChange = viewModel::updateEnabled,
							lightWallpaperBitmap = uiState.lightWallpaperBitmap,
							darkWallpaperBitmap = uiState.darkWallpaperBitmap,
							onLightWallpaperChange = viewModel::updateLightWallpaper,
							onDarkWallpaperChange = viewModel::updateDarkWallpaper,
							listOfWallpapers = viewModel.wallpaperPairsList,
							selectedItem = uiState.selectedItem,
							onSelectedItemChange = {
								showDialog = true
								WallpaperUtils.changeWallpaper(application, darkMode, it)
								viewModel.updateSelectedItem(it)
							}
						)
					}

					if(showDialog){
						WallpaperSettingDialog()
					}
				}
			}
		}
	}
}

