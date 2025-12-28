package com.example.thecodecup

import android.app.Application
import android.util.Log
import com.example.thecodecup.di.ServiceLocator

/**
 * Application class for The Code Cup app
 * Initializes the ServiceLocator with the application context
 */
class TheCodeCupApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            Log.d("TheCodeCupApp", "Initializing ServiceLocator...")
            // Initialize ServiceLocator with application context
            ServiceLocator.initialize(this)
            Log.d("TheCodeCupApp", "ServiceLocator initialized successfully")
        } catch (e: Exception) {
            Log.e("TheCodeCupApp", "Failed to initialize ServiceLocator", e)
        }
    }
}

