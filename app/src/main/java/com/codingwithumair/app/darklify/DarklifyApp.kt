package com.codingwithumair.app.darklify

import android.app.Application
import com.codingwithumair.app.darklify.data.AppContainer

class DarklifyApp: Application() {

	lateinit var container: AppContainer
	override fun onCreate() {
		super.onCreate()
		container = AppContainer(this)
	}
}