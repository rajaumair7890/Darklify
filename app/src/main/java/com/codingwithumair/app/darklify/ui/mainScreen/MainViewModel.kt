package com.codingwithumair.app.darklify.ui.mainScreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.codingwithumair.app.darklify.DarklifyApp
import com.codingwithumair.app.darklify.data.LocalFileStorageRepository
import com.codingwithumair.app.darklify.data.PreferencesRepository
import com.codingwithumair.app.darklify.model.AutoWallPreferences
import com.codingwithumair.app.darklify.model.UiState
import com.codingwithumair.app.darklify.utils.Constants.Dark_Wallpaper_FileName
import com.codingwithumair.app.darklify.utils.Constants.Dark_Wallpaper_FileName_LQ
import com.codingwithumair.app.darklify.utils.Constants.Light_Wallpaper_FileName
import com.codingwithumair.app.darklify.utils.Constants.Light_Wallpaper_FileName_LQ
import com.codingwithumair.app.darklify.utils.WallpaperUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
	private val preferencesRepository: PreferencesRepository,
	private val localFileStorageRepository: LocalFileStorageRepository,
): ViewModel() {
	private val _uiState = MutableStateFlow(UiState())
	val uiState = _uiState.asStateFlow()
	val wallpaperPairsList = WallpaperUtils.wallpaperPairsSmallList
	init {
		viewModelScope.launch(Dispatchers.IO) {

			val preferences = preferencesRepository.preferences.firstOrNull() ?: AutoWallPreferences()

			_uiState.update {
				it.copy(
					lightWallpaperBitmap = localFileStorageRepository.loadImageFromInternalStorage(
						Light_Wallpaper_FileName_LQ).firstOrNull(),
					darkWallpaperBitmap = localFileStorageRepository.loadImageFromInternalStorage(
						Dark_Wallpaper_FileName_LQ).firstOrNull(),
					enabled = preferences.enabled,
					selectedItem = preferences.selectedItem
				)
			}
		}
	}

	fun updateLightWallpaper(uri: Uri){
		viewModelScope.launch {
			val bitmap = localFileStorageRepository.getImageBitmapFromContentUri(uri)
			localFileStorageRepository.saveImageToInternalStorage(Light_Wallpaper_FileName, bitmap)
			val thumbnail = localFileStorageRepository.getScaledImage(bitmap)
			localFileStorageRepository.saveImageToInternalStorage(Light_Wallpaper_FileName_LQ, thumbnail)
			_uiState.update {
				it.copy(
					lightWallpaperBitmap = localFileStorageRepository
						.loadImageFromInternalStorage(Light_Wallpaper_FileName_LQ)
						.firstOrNull()
				)
			}
		}
	}

	fun updateDarkWallpaper(uri: Uri){
		viewModelScope.launch {
			val bitmap = localFileStorageRepository.getImageBitmapFromContentUri(uri)
			localFileStorageRepository.saveImageToInternalStorage(Dark_Wallpaper_FileName, bitmap)
			val thumbnail = localFileStorageRepository.getScaledImage(bitmap)
			localFileStorageRepository.saveImageToInternalStorage(Dark_Wallpaper_FileName_LQ, thumbnail)
			_uiState.update {
				it.copy(
					darkWallpaperBitmap = localFileStorageRepository
						.loadImageFromInternalStorage(Dark_Wallpaper_FileName_LQ)
						.firstOrNull()
				)
			}
		}
	}

	fun updateEnabled(newPreference: Boolean){
		_uiState.update {
			it.copy(enabled = newPreference)
		}.also {
			viewModelScope.launch(Dispatchers.IO) {
				preferencesRepository.changeEnabled(newPreference)
			}
		}
	}

	fun updateSelectedItem(newPreference: Int){
		_uiState.update {
			it.copy(selectedItem = newPreference)
		}.also {
			viewModelScope.launch {
				preferencesRepository.changeSelectedItem(newPreference)
			}
		}
	}
	companion object{
		val factory = viewModelFactory {
			initializer {
				val application = (this[APPLICATION_KEY] as DarklifyApp)
				MainViewModel(
					application.container.preferencesRepository,
					application.container.localFileStorageRepository
				)
			}
		}
	}
}