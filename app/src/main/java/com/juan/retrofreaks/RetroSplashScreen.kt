package com.juan.retrofreaks

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.juan.retrofreaks.repository.ExecuteInitialAnimationViewModel

/*
SplashScreen that will also be main screen. Will either launch MainActivity or will show
a SplashScreen before with some spinning letters that reads the name of the app "RetroFreaks".
 */
class RetroSplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)
        val animationViewModel = ExecuteInitialAnimationViewModel(this)
        val animationExecuting = animationViewModel.isInitialAnimationExecuting.value!!

        if (animationExecuting)
            showSpinningLetters()
        else
            showMainActivityDelayed()

    }

    //Will create a set of spinning letters to be shown when app is in onCreate()
    fun showSpinningLetters() {
        val spinningText:AppCompatTextView = findViewById(R.id.app_name_text_view)
        val rotateAnimation = RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
        rotateAnimation.repeatCount = 5
        rotateAnimation.repeatMode = RotateAnimation.REVERSE
        rotateAnimation.duration = 800
        spinningText.startAnimation(rotateAnimation)
        showMainActivityDelayed()
    }

    //Launches MainActivity with some delay using a fade_in/fade_out effect (Transition)
    fun showMainActivityDelayed() {
        Handler(Looper.getMainLooper()).postDelayed ({
            startActivity(Intent(this,MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        },2400)
    }
}