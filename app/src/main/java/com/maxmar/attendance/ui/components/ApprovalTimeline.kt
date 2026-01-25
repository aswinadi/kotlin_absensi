package com.maxmar.attendance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxmar.attendance.data.model.ApprovalInfo
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors

@Composable
fun ApprovalTimeline(
    requesterName: String,
    requestDate: String?,
    ackInfo: ApprovalInfo?,
    appInfo: ApprovalInfo?,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(modifier = modifier.padding(vertical = 8.dp)) {
        // Step 1: Request
        TimelineItem(
            title = "Diajukan Oleh",
            name = requesterName,
            date = requestDate,
            isCompleted = true,
            isLast = false,
            color = appColors.textSecondary
        )

        // Step 2: Acknowledgement
        TimelineItem(
            title = if (ackInfo != null) "Step 1: Diketahui" else "Step 1: Menunggu Diketahui",
            name = ackInfo?.by,
            date = ackInfo?.date,
            notes = ackInfo?.notes,
            isCompleted = ackInfo != null,
            isLast = false,
            color = MaxmarColors.Primary
        )

        // Step 3: Approval
        TimelineItem(
            title = if (appInfo != null) "Step 2: Disetujui" else "Step 2: Menunggu Persetujuan",
            name = appInfo?.by,
            date = appInfo?.date,
            notes = appInfo?.notes,
            isCompleted = appInfo != null,
            isLast = true,
            color = MaxmarColors.Success
        )
    }
}

@Composable
private fun TimelineItem(
    title: String,
    name: String?,
    date: String?,
    notes: String? = null,
    isCompleted: Boolean,
    isLast: Boolean,
    color: Color
) {
    val appColors = LocalAppColors.current

    Row(modifier = Modifier.fillMaxWidth()) {
        // Vertical Indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) color else appColors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(appColors.textSecondary.copy(alpha = 0.3f))
                    )
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp) // Fixed height line for now, can be flexible
                        .background(if (isCompleted) color.copy(alpha = 0.3f) else appColors.surfaceVariant)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = if (isCompleted) color else appColors.textSecondary,
                fontWeight = FontWeight.Bold
            )
            if (name != null) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = appColors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (date != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodySmall,
                            color = appColors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                Text(
                    text = "Belum diproses",
                    style = MaterialTheme.typography.bodySmall,
                    color = appColors.textSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (!notes.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(appColors.surfaceVariant)
                        .padding(8.dp)
                ) {
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = appColors.textPrimary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
