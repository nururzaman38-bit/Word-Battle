package com.wordbattle.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.wordbattle.R
import com.wordbattle.game.models.PlayerType

object Utils {
    
    fun showToast(context: Context, message: String, type: ToastType = ToastType.DEFAULT) {
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 100)
        
        val view = when (type) {
            ToastType.SUCCESS -> {
                val layout = LayoutInflaterWrapper.inflate(context, R.layout.layout_toast, null)
                layout.setBackgroundColor(ContextCompat.getColor(context, R.color.success))
                layout
            }
            ToastType.WARNING -> {
                val layout = LayoutInflaterWrapper.inflate(context, R.layout.layout_toast, null)
                layout.setBackgroundColor(ContextCompat.getColor(context, R.color.warning))
                layout
            }
            ToastType.ERROR -> {
                val layout = LayoutInflaterWrapper.inflate(context, R.layout.layout_toast, null)
                layout.setBackgroundColor(ContextCompat.getColor(context, R.color.error))
                layout
            }
            ToastType.INFO -> {
                val layout = LayoutInflaterWrapper.inflate(context, R.layout.layout_toast, null)
                layout.setBackgroundColor(ContextCompat.getColor(context, R.color.info))
                layout
            }
            ToastType.DEFAULT -> {
                val layout = LayoutInflaterWrapper.inflate(context, R.layout.layout_toast, null)
                layout.setBackgroundColor(ContextCompat.getColor(context, R.color.toast_background))
                layout
            }
        }
        
        val textView = view.findViewById<android.widget.TextView>(R.id.toast_message)
        textView.text = message
        
        toast.view = view
        toast.show()
    }
    
    fun getPlayerTypeDisplayName(type: PlayerType): String {
        return when (type) {
            PlayerType.HUMAN_LOCAL -> "Local"
            PlayerType.HUMAN_ONLINE -> "Online"
            PlayerType.COMPUTER -> "Computer"
        }
    }
    
    fun getRandomAvatarColor(): Long {
        val colors = listOf(
            0xFF5B1E8C.toLong(),
            0xFFFF4E4E.toLong(),
            0xFF00C9A7.toLong(),
            0xFF4C6FFF.toLong(),
            0xFFFFC93C.toLong(),
            0xFF8A3FE0.toLong()
        )
        return colors.random()
    }
}

enum class ToastType {
    DEFAULT,
    SUCCESS,
    WARNING,
    ERROR,
    INFO
}
