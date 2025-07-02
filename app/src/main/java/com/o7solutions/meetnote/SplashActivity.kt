package com.o7solutions.meetnote

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.o7solutions.meetnote.auth.LoginActivity


class SplashActivity : AppCompatActivity() {

    // Duration of the splash screen in milliseconds
    private val SPLASH_DELAY: Long = 2000 // 2 seconds

    // Optional: If you're using Firebase for authentication, declare it here
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent: Intent

            // Check if the user is already logged in (example with Firebase Auth)
            if (auth.currentUser != null) {
                // User is logged in, go to MainActivity
                intent = Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                // User is not logged in, go to LoginActivity
                intent = Intent(this@SplashActivity, LoginActivity::class.java)
            }

            startActivity(intent)
            // Finish the splash activity so the user cannot go back to it
            finish()
        }, SPLASH_DELAY)
    }
}