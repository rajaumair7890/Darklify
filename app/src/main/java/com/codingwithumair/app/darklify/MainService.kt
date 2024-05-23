package com.codingwithumair.app.darklify

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.codingwithumair.app.darklify.utils.WallpaperUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainService: Service() {

	private var lastDarkModeEnabledState = false

	private val darkModeReceiver = object: BroadcastReceiver(){
		override fun onReceive(context: Context?, intent: Intent?) {
			Log.d("DarkModeReceiver", "on Receive Called")
			if(intent?.action == Intent.ACTION_CONFIGURATION_CHANGED){
				val isDarkMode = this@MainService.application.resources.configuration.isNightModeActive
				Log.d("MainService", "DarkMode: $isDarkMode")
				if(lastDarkModeEnabledState != isDarkMode){
					val application = this@MainService.application as DarklifyApp
					WallpaperUtils.changeWallpaper(application, isDarkMode).also{
						lastDarkModeEnabledState = isDarkMode
					}
				}
			}
		}
	}

	override fun onCreate() {
		super.onCreate()
		startForeground(2, makeNotification())
		checkPreferencesAndStopService()
		lastDarkModeEnabledState = application.resources.configuration.isNightModeActive
		registerReceiver(darkModeReceiver, IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED))
	}

	override fun onDestroy() {
		unregisterReceiver(darkModeReceiver)
		super.onDestroy()
	}

	override fun onBind(p0: Intent?): IBinder? {
		return null
	}

	companion object{
		private const val TAG = "MainService"

		private fun Service.checkPreferencesAndStopService(){
			CoroutineScope(Dispatchers.IO).launch{
				val myApp = application as DarklifyApp
				val preferences = myApp.container.preferencesRepository.preferences.firstOrNull()
				if(preferences == null){
					Log.d(TAG, "preferences are null")
					stopSelf()
					return@launch
				}
				val shouldStartService = preferences.enabled
				if(!shouldStartService){
					stopSelf()
				}
			}
		}

		fun Service.makeNotification(): Notification {
			val notificationChannel = NotificationChannel(
				packageName,
				"Wallpaper Service",
				NotificationManager.IMPORTANCE_LOW
			)
			(getSystemService(NOTIFICATION_SERVICE) as NotificationManager).apply{
				createNotificationChannel(notificationChannel)
			}
			return NotificationCompat
				.Builder(this, packageName)
				.setChannelId(notificationChannel.id)
				.setOngoing(false)
				.setSmallIcon(R.mipmap.ic_launcher_round)
				.setContentTitle("Wallpaper Service is Running")
				.setPriority(NotificationManager.IMPORTANCE_LOW)
				.setCategory(Notification.CATEGORY_SERVICE)
				.build()
		}

		fun startOrStopService(context: Context, shouldStartService: Boolean){
			val serviceIntent = Intent(context, MainService::class.java)
			val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
			val isAlreadyRunning = activityManager?.getRunningServices(Integer.MAX_VALUE)?.any {
				it.service == serviceIntent.component
			} ?: false

			Log.d(TAG, "ServiceAlreadyRunning: $isAlreadyRunning")

			if(shouldStartService) {
				if(!isAlreadyRunning) {
					context.startForegroundService(serviceIntent)
					Log.d(TAG, "ServiceStarted")
				}
			}else {
				if(isAlreadyRunning) {
					context.stopService(serviceIntent)
					Log.d(TAG, "ServiceStopped")
				}
			}
		}
	}
}