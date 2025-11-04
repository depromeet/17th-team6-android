package com.dpm.sixpack.presentation.common.util.context

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

/**
 * 외부 브라우저 리다이렉트
 *
 * @param url The URL to open
 */
fun Context.openUrlInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    startActivity(intent)
}
