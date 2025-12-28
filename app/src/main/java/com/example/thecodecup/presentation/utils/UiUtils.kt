package com.example.thecodecup.presentation.utils

import android.content.Context
import com.example.thecodecup.R

/**
 * Utility functions for UI operations
 */
object UiUtils {

    /**
     * Get drawable resource ID from image name string
     *
     * @param context Android context
     * @param imageName The name of the drawable resource (without extension)
     * @return Resource ID of the drawable, or placeholder if not found
     */
    fun getDrawableId(context: Context, imageName: String?): Int {
        if (imageName.isNullOrBlank()) {
            return R.drawable.img_placeholder
        }

        return try {
            val resId = context.resources.getIdentifier(
                imageName,
                "drawable",
                context.packageName
            )
            if (resId != 0) resId else R.drawable.img_placeholder
        } catch (e: Exception) {
            R.drawable.img_placeholder
        }
    }

    /**
     * Get drawable resource ID with default fallback
     */
    fun getDrawableIdOrDefault(context: Context, imageName: String?, defaultRes: Int): Int {
        if (imageName.isNullOrBlank()) {
            return defaultRes
        }

        return try {
            val resId = context.resources.getIdentifier(
                imageName,
                "drawable",
                context.packageName
            )
            if (resId != 0) resId else defaultRes
        } catch (e: Exception) {
            defaultRes
        }
    }
}

/**
 * Extension function to get drawable ID for a Coffee imageName
 */
fun String?.toDrawableId(context: Context): Int {
    return UiUtils.getDrawableId(context, this)
}

