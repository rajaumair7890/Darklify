package com.codingwithumair.app.darklify.model

import android.graphics.Bitmap

data class UiState(
	val lightWallpaperBitmap: Bitmap? = null,
	val darkWallpaperBitmap: Bitmap? = null,
	val enabled: Boolean = false,
	val selectedItem: Int = 1
)

data class AutoWallPreferences(
	val enabled: Boolean = false,
	val selectedItem: Int = 1
)