package com.wordbattle.ui.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import com.wordbattle.R
import com.wordbattle.databinding.ActivitySplashBinding
import com.wordbattle.ui.activities.MainActivity
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupLetterTiles()
        animateLoadingDots()
        
        // Auto transition after 2 seconds
        kotlinx.coroutines.GlobalScope.launch {
            delay(2000)
            navigateToMain()
        }
    }

    private fun setupLetterTiles() {
        // W
        findViewById<android.widget.FrameLayout>(R.id.tile_w)?.let { frame ->
            val tileView = frame.findViewById<com.wordbattle.ui.components.LetterTileView>(R.id.tile_view)
            tileView?.setLetter("W")
        }
        
        // O (first)
        findViewById<android.widget.FrameLayout>(R.id.tile_o1)?.let { frame ->
            val tileView = frame.findViewById<com.wordbattle.ui.components.LetterTileView>(R.id.tile_view)
            tileView?.setLetter("O")
        }
        
        // R
        findViewById<android.widget.FrameLayout>(R.id.tile_r)?.let { frame ->
            val tileView = frame.findViewById<com.wordbattle.ui.components.LetterTileView>(R.id.tile_view)
            tileView?.setLetter("R")
        }
        
        // D
        findViewById<android.widget.FrameLayout>(R.id.tile_d)?.let { frame ->
            val tileView = frame.findViewById<com.wordbattle.ui.components.LetterTileView>(R.id.tile_view)
            tileView?.setLetter("D")
        }
        
        // B (first)
        findViewById<android.widget.FrameLayout>(R.id.tile_b1)?.let { frame ->
            val tileView = frame.findViewById<com.wordbattle.ui.components.LetterTileView>(R.id.tile_view)
            tileView?.setLetter("B")
            tileView?.setGold(true)
        }
        
        // A
        findViewById<android.widget.FrameLayout>(R.id.tile_a)?.let { frame ->
            val tileView = frame.findViewById<com.wordbattle.ui.components.LetterTileView>(R.id.tile_view)
            tileView?.setLetter("A")
        }
        
        // T (first)
        findViewById<android.widget.FrameLayout>(R.id.tile_t)?.let { frame ->
            val tileView = frame.findViewById<com.wordbattle.ui.components.LetterTileView>(R.id.tile_view)
            tileView?.setLetter("T")
        }
        
        // T (second)
        findViewById<android.widget.FrameLayout>(R.id.tile_t2)?.let { frame ->
            val tileView = frame.findViewById<com.wordbattle.ui.components.LetterTileView>(R.id.tile_view)
            tileView?.setLetter("T")
        }
        
        // L (from le)
        findViewById<android.widget.FrameLayout>(R.id.tile_l_e)?.let { frame ->
            val tileView = frame.findViewById<com.wordbattle.ui.components.LetterTileView>(R.id.tile_view)
            tileView?.setLetter("L")
        }
    }

    private fun animateLoadingDots() {
        val dots = listOf(
            binding.dot1,
            binding.dot2,
            binding.dot3
        )
        
        dots.forEachIndexed { index, dot ->
            val anim = ObjectAnimator.ofFloat(dot, "alpha", 0.3f, 1f, 0.3f)
            anim.duration = 800
            anim.repeatCount = ObjectAnimator.INFINITE
            anim.repeatMode = ObjectAnimator.REVERSE
            anim.startDelay = index * 200L
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.start()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
