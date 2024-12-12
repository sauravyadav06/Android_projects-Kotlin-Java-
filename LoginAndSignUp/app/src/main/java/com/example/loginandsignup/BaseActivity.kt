package com.example.loginandsignup

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check for emulator
        if (isEmulator()) {
            Toast.makeText(this, "This app cannot run on an emulator.", Toast.LENGTH_LONG).show()
            finishAffinity() // Close all activities
            exitProcess(0)   // Exit the app
        }

        // Check for AnyDesk
        if (isAnyDeskInstalled(this)) {
            Toast.makeText(this, "This app cannot run with AnyDesk installed.", Toast.LENGTH_LONG).show()
            finishAffinity() // Close all activities
            exitProcess(0)   // Exit the app
        }
    }

    private fun isEmulator(): Boolean {
        val brand = Build.BRAND
        val device = Build.DEVICE
        val model = Build.MODEL
        val product = Build.PRODUCT
        val manufacturer = Build.MANUFACTURER
        val fingerprint = Build.FINGERPRINT

        // Check for known emulator properties
        return brand.startsWith("generic") || device.startsWith("generic") ||
                model.contains("Emulator") || model.contains("Android SDK built for x86") ||
                product.contains("sdk") || product.contains("emulator") ||
                manufacturer.contains("Genymotion") || // Check for Genymotion
                (Build.FINGERPRINT.startsWith("generic") && Build.FINGERPRINT.endsWith(":vbox86p:")) ||
                product.contains("vbox86p") // Specific for Genymotion
    }

    private fun isAnyDeskInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.anydesk.anydeskandroid", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
