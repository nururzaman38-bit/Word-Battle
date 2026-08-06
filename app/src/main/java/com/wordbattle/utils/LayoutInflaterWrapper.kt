package com.wordbattle.utils

import android.content.Context
import android.view.LayoutInflater

object LayoutInflaterWrapper {
    fun inflate(context: Context, resource: Int, root: android.view.View?): android.view.View {
        return LayoutInflater.from(context).inflate(resource, root)
    }
}
