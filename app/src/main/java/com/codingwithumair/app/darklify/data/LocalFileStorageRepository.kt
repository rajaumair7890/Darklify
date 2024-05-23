package com.codingwithumair.app.darklify.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class LocalFileStorageRepository(
	private val context: Context
) {

	suspend fun saveImageToInternalStorage(filename: String, bitmap: Bitmap): Boolean{
		return withContext(Dispatchers.IO) {
			return@withContext try {

				val isExistingFile = context.filesDir.listFiles()?.firstOrNull {
					it.canRead() && it.isFile && it.nameWithoutExtension == filename
				}?.exists() ?: false

				if (isExistingFile) {
					deleteImageFromInternalStorage(filename)
				}

				context.openFileOutput(filename, Context.MODE_PRIVATE).use { stream ->
					val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
					if (!success) {
						throw IOException("Couldn't save Bitmap!")
					}
				}
				true
			} catch (e: IOException) {
				Log.e(TAG, "error saving image", e)
				false
			}
		}
	}

	suspend fun loadImageFromInternalStorage(filename: String) = flow {
		emit(
			context.filesDir.listFiles()?.filter {
				it.canRead() && it.isFile && it.nameWithoutExtension == filename
			}?.map{
				val bytes = it.readBytes()
				BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
			}?.firstOrNull()
		)
	}.flowOn(Dispatchers.IO)

	private suspend fun deleteImageFromInternalStorage(filename: String): Boolean{
		return withContext(Dispatchers.IO) {
			return@withContext try {
				context.deleteFile(filename)
			} catch (e: Exception) {
				Log.e(TAG, "error deleting image", e)
				false
			}
		}
	}

	suspend fun getImageBitmapFromContentUri(uri: Uri): Bitmap {
		return withContext(Dispatchers.IO) {
			return@withContext context.contentResolver.openInputStream(uri).use { inputStream ->
				BitmapFactory.decodeStream(inputStream)
			}
		}
	}

	suspend fun getScaledImage(bitmap: Bitmap): Bitmap{
		return withContext(Dispatchers.IO){
			createScaledBitmap(bitmap, 269, 568, true)
		}
	}

	private companion object{
		const val TAG = "LocalFileStorageRepository"
	}

}

