package com.codingwithumair.app.darklify.bootCompleted

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.codingwithumair.app.darklify.MainService

class BootCompletedReceiver: BroadcastReceiver() {
	override fun onReceive(context: Context?, intent: Intent?) {
		if(intent?.action == Intent.ACTION_BOOT_COMPLETED){
			Log.d(TAG, "Boot Completed Intent received!")
			val serviceIntent = Intent(context, MainService::class.java)
			context?.startForegroundService(serviceIntent)
		}
	}

	companion object{
		const val TAG = "BootCompletedReceiver"
	}
}