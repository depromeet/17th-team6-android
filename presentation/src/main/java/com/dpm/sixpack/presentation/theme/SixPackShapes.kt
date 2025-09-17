package com.dpm.sixpack.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class SixPackShapes(
    val round4: Shape,
    val round8: Shape,
    val round12: Shape,
    val round16: Shape,
    val round20: Shape,
    val full: Shape,
)

val SixPackShapesValue =
    SixPackShapes(
        round4 = RoundedCornerShape(4.dp),
        round8 = RoundedCornerShape(8.dp),
        round12 = RoundedCornerShape(12.dp),
        round16 = RoundedCornerShape(16.dp),
        round20 = RoundedCornerShape(20.dp),
        full = RoundedCornerShape(50),
    )
