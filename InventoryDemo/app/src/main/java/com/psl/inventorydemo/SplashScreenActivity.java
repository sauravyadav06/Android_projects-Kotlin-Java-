package com.psl.inventorydemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for splash screen
        setContentView(R.layout.activity_splash_screen);

        // Load the fade-in animation from res/anim/fade_in.xml
        final Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.splashanim);

        // Find the splash screen logo view in the layout (make sure you have an ImageView with this id in your layout)
        final View splashView = findViewById(R.id.splash_logo);

        // Start the fade-in animation on the splash logo
        splashView.startAnimation(fadeInAnimation);

        // Set up a listener to move to the MainActivity once the animation finishes
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // No action needed here
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // No action needed here
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start the SignInActivity after the animation ends
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                // Close the SplashScreenActivity so it's not accessible via back button
                finish();
            }
        });
    }
}