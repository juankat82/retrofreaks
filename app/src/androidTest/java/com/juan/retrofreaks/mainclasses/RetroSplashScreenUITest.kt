package com.juan.retrofreaks.mainclasses

import android.content.Context
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.juan.retrofreaks.RetroSplashScreen
import com.juan.retrofreaks.repository.ExecuteInitialAnimationViewModel
import org.junit.Before
import org.junit.Test

class RetroSplashScreenUITest {


    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    val animationViewModel = ExecuteInitialAnimationViewModel(context)
    var animationExecuting = false

    @Before
    fun init() {
        animationViewModel.setIsInitialAnimationExecuting(true)
    }

    @Test
    fun test_is_initial_animation_enabled() {
        ActivityScenario.launch(RetroSplashScreen::class.java)
        animationExecuting = animationViewModel.isInitialAnimationExecuting.value!!
        assert(animationExecuting)
        Log.i("ISANIMATIONEXECUTING",animationExecuting.toString())
    }

    @Test
    fun test_is_initial_animation_disabled() {
        animationViewModel.setIsInitialAnimationExecuting(animationExecuting)
        ActivityScenario.launch(RetroSplashScreen::class.java)
        animationExecuting = animationViewModel.isInitialAnimationExecuting.value!!
        assert(!animationExecuting)
        Log.i("ISANIMATIONEXECUTING",animationExecuting.toString())
    }
}