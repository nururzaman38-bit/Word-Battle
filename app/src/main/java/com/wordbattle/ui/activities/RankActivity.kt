package com.wordbattle.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wordbattle.R

class RankActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)
        
        // Rank is handled by fragment in MainActivity
        finish()
    }
}
