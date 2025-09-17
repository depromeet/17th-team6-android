import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class SixpackTypography(
    val h1Bold: TextStyle,
    val h1Medium: TextStyle,
    val h2Bold: TextStyle,
    val h2Medium: TextStyle,
    val h2Regular: TextStyle,
    val t1Bold: TextStyle,
    val t1Medium: TextStyle,
    val t1Regular: TextStyle,
    val t2Bold: TextStyle,
    val t2Medium: TextStyle,
    val t2Regular: TextStyle,
    val b1Bold: TextStyle,
    val b1Medium: TextStyle,
    val b1Regular: TextStyle,
    val b2Bold: TextStyle,
    val b2Medium: TextStyle,
    val b2Regular: TextStyle,
    val c1Bold: TextStyle,
    val c1Medium: TextStyle,
    val c1Regular: TextStyle,
)

private val pretendard = null
//    FontFamily(
//        Font(R.font.pretendard_regular, FontWeight.Normal),
//        Font(R.font.pretendard_medium, FontWeight.Medium),
//        Font(R.font.pretendard_bold, FontWeight.Bold),
//    )

val SixPackTypographyValue =
    SixpackTypography(
        // Headline
        h1Bold =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 38.sp,
                letterSpacing = (-0.2).sp,
            ),
        h1Medium =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                lineHeight = 38.sp,
                letterSpacing = (-0.2).sp,
            ),
        h2Bold =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 34.sp,
                letterSpacing = (-0.2).sp,
            ),
        h2Medium =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp,
                lineHeight = 34.sp,
                letterSpacing = (-0.2).sp,
            ),
        h2Regular =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                lineHeight = 34.sp,
                letterSpacing = (-0.2).sp,
            ),
        // Title
        t1Bold =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                letterSpacing = (-0.2).sp,
            ),
        t1Medium =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                letterSpacing = (-0.2).sp,
            ),
        t1Regular =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                letterSpacing = (-0.2).sp,
            ),
        t2Bold =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                letterSpacing = (-0.2).sp,
            ),
        t2Medium =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                letterSpacing = (-0.2).sp,
            ),
        t2Regular =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                letterSpacing = (-0.2).sp,
            ),
        // Body
        b1Bold =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = (-0.2).sp,
            ),
        b1Medium =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = (-0.2).sp,
            ),
        b1Regular =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = (-0.2).sp,
            ),
        b2Bold =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                letterSpacing = (-0.2).sp,
            ),
        b2Medium =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                letterSpacing = (-0.2).sp,
            ),
        b2Regular =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                letterSpacing = (-0.2).sp,
            ),
        // Caption
        c1Bold =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                letterSpacing = (-0.2).sp,
            ),
        c1Medium =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                letterSpacing = (-0.2).sp,
            ),
        c1Regular =
            TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                letterSpacing = (-0.2).sp,
            ),
    )
