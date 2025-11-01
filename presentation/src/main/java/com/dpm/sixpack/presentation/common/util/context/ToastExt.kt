package com.dpm.sixpack.presentation.common.util.context

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Shows a short duration toast message.
 *
 * @param context The application context
 * @param message The message to display
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Shows a short duration toast message using a string resource ID.
 *
 * @param context The application context
 * @param messageResId The resource ID of the message to display
 */
fun Context.showToastByResId(messageResId: Int) {
    Toast.makeText(this, this.getString(messageResId), Toast.LENGTH_SHORT).show()
}

