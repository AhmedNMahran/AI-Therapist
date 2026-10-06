package com.github.ahmednmahran.aitherapist

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class AITherapistApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase SDK
        FirebaseApp.initializeApp(this)

        // Install Firebase App Check Provider
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        
        if (BuildConfig.DEBUG) {
            // In debug mode, use Debug provider.
            // NOTE: You must register the debug token printed in Logcat into the Firebase Console.
            // Otherwise, AppCheck will fail with a 403 App attestation failed error.
            Log.d("AppCheck", "Using DebugAppCheckProviderFactory. Check logcat for the debug secret and add it to Firebase Console if you encounter 403 errors.")
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            // In release mode, use Play Integrity provider.
            firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
    }
}
