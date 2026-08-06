package com.wordbattle.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wordbattle.R

class FriendsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        
        // Friends is handled by fragment in MainActivity
        finish()
    }
}
