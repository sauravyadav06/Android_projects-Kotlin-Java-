package com.example.loginandsignup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for splash screen
        setContentView(R.layout.activity_splash_screen)

        // Load the fade-in animation from res/anim/fade_in.xml
        val fadeInAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Find the splash screen logo view in the layout (make sure you have an ImageView with this id in your layout)
        val splashView = findViewById<View>(R.id.splash_logo)

        // Start the fade-in animation on the splash logo
        splashView.startAnimation(fadeInAnimation)

        // Set up a listener to move to the MainActivity once the animation finishes
        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Start the MainActivity after the animation ends
                startActivity(Intent(this@SplashScreenActivity, SignInActivity::class.java))
                // Close the SplashScreenActivity so it's not accessible via back button
                finish()
            }
        })
    }
}
