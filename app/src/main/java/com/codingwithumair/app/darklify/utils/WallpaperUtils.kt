package com.codingwithumair.app.darklify.utils

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory
import com.codingwithumair.app.darklify.DarklifyApp
import com.codingwithumair.app.darklify.R
import com.codingwithumair.app.darklify.utils.Constants.Dark_Wallpaper_FileName
import com.codingwithumair.app.darklify.utils.Constants.Light_Wallpaper_FileName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object WallpaperUtils {

	val wallpaperPairsSmallList = listOf(
		Triple(1, R.drawable.light1small, R.drawable.dark1small),
		Triple(2, R.drawable.light2small, R.drawable.dark2small),
		Triple(3, R.drawable.light3small, R.drawable.dark3small),
		Triple(4, R.drawable.light4small, R.drawable.dark4small),
		Triple(5, R.drawable.light5small, R.drawable.dark5small),
		Triple(6, R.drawable.light6small, R.drawable.dark6small),
		Triple(7, R.drawable.light7small, R.drawable.dark7small),
		Triple(8, R.drawable.light8small, R.drawable.dark8small),
		Triple(9, R.drawable.light9small, R.drawable.dark9small),
		Triple(10, R.drawable.light10small, R.drawable.dark10small),
		Triple(11, R.drawable.light11small, R.drawable.dark11small)
	)

	private val wallpaperPairsList = listOf(
		Triple(1, R.drawable.light1, R.drawable.dark1),
		Triple(2, R.drawable.light2, R.drawable.dark2),
		Triple(3, R.drawable.light3, R.drawable.dark3),
		Triple(4, R.drawable.light4, R.drawable.dark4),
		Triple(5, R.drawable.light5, R.drawable.dark5),
		Triple(6, R.drawable.light6, R.drawable.dark6),
		Triple(7, R.drawable.light7, R.drawable.dark7),
		Triple(8, R.drawable.light8, R.drawable.dark8),
		Triple(9, R.drawable.light9, R.drawable.dark9),
		Triple(10, R.drawable.light10, R.drawable.dark10),
		Triple(11, R.drawable.light11, R.drawable.dark11)
	)

	fun changeWallpaper(
		application: DarklifyApp,
		changeToDark: Boolean,
		selectedIndex: Int? = null
	){
		CoroutineScope(Dispatchers.IO).launch {

			val preferencesRepository = application.container.preferencesRepository
			val selectedPairIndex = selectedIndex ?: preferencesRepository.preferences.firstOrNull().run {
				this?.selectedItem ?: 1
			}

			val wallpaperBitmap = getWallpaperBitmap(application, changeToDark, selectedPairIndex)

			WallpaperManager.getInstance(application).apply {
				if(wallpaperBitmap != null) {
					setBitmap(wallpaperBitmap)
				}
			}
		}
	}

	private suspend fun getWallpaperBitmap(
		application: DarklifyApp,
		changeToDark: Boolean,
		selectedIndex: Int
	): Bitmap?{
		return withContext(Dispatchers.IO){

			val localFileStorageRepository = application.container.localFileStorageRepository

			val bitmap = when(selectedIndex){
				0 -> {
					if(changeToDark) {
						localFileStorageRepository.loadImageFromInternalStorage(
							Dark_Wallpaper_FileName
						)
					}else{
						localFileStorageRepository.loadImageFromInternalStorage(
							Light_Wallpaper_FileName
						)
					}.firstOrNull()
				}

				else -> {
					BitmapFactory.decodeResource(application.resources, getWallpaperResource(selectedIndex, changeToDark))
				}
			}
			val displayMetrics = application.resources.displayMetrics
			val requiredHeight = displayMetrics.heightPixels
			val requiredWidth = displayMetrics.widthPixels
			val scaledBitmap = if(bitmap != null){
				createScaledBitmap(bitmap, requiredWidth, requiredHeight, true)
			}else null

			return@withContext scaledBitmap
		}
	}

	private fun getWallpaperResource(
		selectedIndex: Int,
		changeToDark: Boolean
	): Int{
		val selectedPair = wallpaperPairsList.first{ it.first == selectedIndex }
		return if(changeToDark) selectedPair.third else selectedPair.second
	}

	private const val TAG = "WallpaperUtils"
}