package com.babanomania.pdfscanner

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.babanomania.pdfscanner.persistance.DocumentDatabase
import com.babanomania.pdfscanner.utils.applyTheme

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        DocumentDatabase.getInstance(applicationContext)

        Handler().postDelayed({
            val i = Intent(
                this@SplashScreen,
                if (restorePrefData()) MainActivity::class.java else IntroActivity::class.java
            )
            startActivity(i)
            finish()
        }, SPLASH_TIME_OUT)
    }

    private fun restorePrefData(): Boolean {
        val pref = applicationContext.getSharedPreferences(
            "myPrefs",
            MODE_PRIVATE
        )
        return pref.getBoolean("isIntroOpnend", false)
    }

    companion object {
        private const val SPLASH_TIME_OUT = 300L
    }
}