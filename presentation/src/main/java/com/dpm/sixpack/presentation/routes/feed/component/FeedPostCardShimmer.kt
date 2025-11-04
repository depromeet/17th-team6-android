package com.dpm.sixpack.presentation.routes.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * FeedPostCard의 Shimmer 로딩 UI
 */
@Composable
fun FeedPostCardShimmer(modifier: Modifier = Modifier) {
    val shimmerColor = SixpackTheme.colors.gray100

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(SixpackTheme.colors.gray0),
    ) {
        // User Info Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Profile Image
            Box(
                modifier =
                    Modifier
                        .size(36.dp)
                        .background(shimmerColor, CircleShape),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                // Username
                Box(
                    modifier =
                        Modifier
                            .width(100.dp)
                            .height(14.dp)
                            .background(shimmerColor, RoundedCornerShape(4.dp)),
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Time
                Box(
                    modifier =
                        Modifier
                            .width(60.dp)
                            .height(12.dp)
                            .background(shimmerColor, RoundedCornerShape(4.dp)),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Post Image with Record
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(shimmerColor, RoundedCornerShape(12.dp)),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Reaction Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(3) {
                Box(
                    modifier =
                        Modifier
                            .width(50.dp)
                            .height(28.dp)
                            .background(shimmerColor, RoundedCornerShape(14.dp)),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedPostCardShimmerPreview() {
    DoRunPreviewWrapper {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(40.dp),
        ) {
            FeedPostCardShimmer(modifier = Modifier.fillMaxWidth())
            FeedPostCardShimmer(modifier = Modifier.fillMaxWidth())
        }
    }
}
