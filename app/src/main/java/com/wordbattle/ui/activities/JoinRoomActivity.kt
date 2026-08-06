package com.wordbattle.ui.activities

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wordbattle.R
import com.wordbattle.data.firebase.FirebaseManager
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.ActivityJoinRoomBinding
import com.wordbattle.utils.Utils
import kotlinx.coroutines.*

class JoinRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinRoomBinding
    private lateinit var userPrefs: UserPrefsManager
    private lateinit var firebaseManager: FirebaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPrefsManager(this)
        firebaseManager = FirebaseManager()

        setupInputListeners()
        setupJoinButton()
    }

    private fun setupInputListeners() {
        binding.etRoomId.setText("") // Clear for new input
        binding.etPasscode.setText("")
        binding.etName.setText(userPrefs.currentUserName)

        // Auto capitalize room ID
        binding.etRoomId.addTextChangedListener {
            if (it.isNotEmpty()) {
                binding.etRoomId.setText(it.uppercase())
                binding.etRoomId.setSelection(it.uppercase().length)
            }
        }

        // Set done action on name field
        binding.etName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptJoin()
                true
            } else {
                false
            }
        }
    }

    private fun setupJoinButton() {
        binding.btnJoin.setOnClickListener {
            attemptJoin()
        }
    }

    private fun attemptJoin() {
        val roomId = binding.etRoomId.text.toString().trim()
        val passcode = binding.etPasscode.text.toString().trim()
        val playerName = binding.etName.text.toString().trim()

        if (roomId.isEmpty()) {
            Utils.showToast(this, getString(R.string.enter_room_id), com.wordbattle.utils.ToastType.WARNING)
            return
        }

        if (passcode.isEmpty()) {
            Utils.showToast(this, getString(R.string.enter_passcode), com.wordbattle.utils.ToastType.WARNING)
            return
        }

        if (playerName.isEmpty()) {
            Utils.showToast(this, getString(R.string.enter_your_name), com.wordbattle.utils.ToastType.WARNING)
            return
        }

        // Update user name if changed
        if (playerName != userPrefs.currentUserName) {
            userPrefs.currentUserName = playerName
        }

        joinRoom(roomId, passcode, playerName)
    }

    private fun joinRoom(roomId: String, passcode: String, playerName: String) {
        Utils.showToast(this, getString(R.string.joining_room), com.wordbattle.utils.ToastType.INFO)

        // Simulate join delay
        Handler(Looper.getMainLooper()).postDelayed({
            // For demo, just navigate to lobby
            val intent = Intent(this, LobbyActivity::class.java).apply {
                putExtra(LobbyActivity.EXTRA_ROOM_ID, roomId)
                putExtra(LobbyActivity.EXTRA_PLAYER_NAME, playerName)
            }
            startActivity(intent)
            finish()
        }, 1500)
    }

    private val Handler = android.os.Handler(Looper.getMainLooper())
}
