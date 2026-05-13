package com.example.rakthavahini

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hide action bar on splash
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("donor_prefs", Context.MODE_PRIVATE)
            val isLoggedIn = prefs.getBoolean("is_logged_in", false)

            if (isLoggedIn) {
                // Already signed in — go directly to main app
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Not signed in — go to sign in screen
                startActivity(Intent(this, SignInActivity::class.java))
            }
            finish()
        }, 5000) // 2.5 second splash
    }
}