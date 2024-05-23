package com.codingwithumair.app.darklify.data

import android.content.Context

class AppContainer(
	private val context: Context
) {

	val localFileStorageRepository by lazy{
		LocalFileStorageRepository(context)
	}

	val preferencesRepository by lazy {
		PreferencesRepository(context)
	}
}