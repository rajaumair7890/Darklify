package com.codingwithumair.app.darklify.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.codingwithumair.app.darklify.model.AutoWallPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferencesRepository(
	context: Context
) {

	private val dataStore = context.dataStore

	val preferences: Flow<AutoWallPreferences> = dataStore.data
		.catch {
			if(it is IOException){
				Log.e(TAG, "error reading preferences", it)
				emit(emptyPreferences())
			}else{
				throw it
			}
		}.map{ preferences ->
			AutoWallPreferences(
				enabled = preferences[Enabled] ?: false,
				selectedItem = preferences[SelectedItem] ?: 1
			)
		}

	suspend fun changeEnabled(newPreference: Boolean){
		dataStore.edit { preferences ->
			preferences[Enabled] = newPreference
		}
	}

	suspend fun changeSelectedItem(newPreference: Int){
		dataStore.edit {preferences ->
			preferences[SelectedItem] = newPreference
		}
	}

	private companion object{
		private val Enabled = booleanPreferencesKey("enabled")
		private val SelectedItem = intPreferencesKey("selectedItem")

		private const val WALLPAPER_DATASTORE = "wallpaper_datastore"

		private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
			name = WALLPAPER_DATASTORE
		)

		const val TAG = "PreferencesRepository"
	}
}