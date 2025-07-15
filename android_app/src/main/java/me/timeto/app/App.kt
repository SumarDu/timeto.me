package me.timeto.app

import android.app.Application
import me.timeto.shared.initKmpAndroid
import me.timeto.app.data.database.AppDatabase
import me.timeto.app.data.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {

    companion object {

        lateinit var instance: App
            private set
    }

    val database by lazy { AppDatabase.getDatabase(this) }
    val eventRepository by lazy { EventRepository(database.eventDao()) }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Supabase and other KMP services
        initKmpAndroid(
            application = this,
            build = BuildConfig.VERSION_CODE,
            version = BuildConfig.VERSION_NAME,
            flavor = BuildConfig.FLAVOR,
        )

        // Start the sync process in a background thread
        CoroutineScope(Dispatchers.IO).launch {
            eventRepository.syncEvents()
        }
    }
}
