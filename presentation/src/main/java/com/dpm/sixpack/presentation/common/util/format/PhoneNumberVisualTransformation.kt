package com.dpm.sixpack.presentation.common.util.format

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digitsOnly = text.text.filter { it.isDigit() }
        val formatted = formatPhoneNumberForDisplay(digitsOnly)

        val offsetMapping =
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    var transformedOffset = offset

                    if (offset > 3) transformedOffset++
                    if (offset > 7) {
                        transformedOffset++
                    } else if (offset > 6 && digitsOnly.length <= 10) {
                        transformedOffset++
                    }

                    return transformedOffset.coerceAtMost(formatted.length)
                }

                override fun transformedToOriginal(offset: Int): Int {
                    var originalOffset = offset

                    if (offset > 4) originalOffset--
                    if (digitsOnly.length > 10) {
                        if (offset > 9) originalOffset--
                    } else {
                        if (offset > 8) originalOffset--
                    }

                    return originalOffset.coerceAtMost(digitsOnly.length)
                }
            }

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = offsetMapping,
        )
    }

    private fun formatPhoneNumberForDisplay(digitsOnly: String): String =
        when {
            digitsOnly.length <= 3 -> digitsOnly
            digitsOnly.length <= 7 -> {
                "${digitsOnly.substring(0, 3)}-${digitsOnly.substring(3)}"
            }
            digitsOnly.length <= 10 -> {
                "${digitsOnly.substring(0, 3)}-${digitsOnly.substring(3, 6)}-${digitsOnly.substring(6)}"
            }
            else -> {
                val trimmed = digitsOnly.take(11)
                "${trimmed.substring(0, 3)}-${trimmed.substring(3, 7)}-${trimmed.substring(7)}"
            }
        }
}
