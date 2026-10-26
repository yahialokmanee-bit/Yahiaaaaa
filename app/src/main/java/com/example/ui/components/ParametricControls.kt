package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.IntersectionResult
import com.example.ui.theme.*

@Composable
fun ParametricControls(
    isParametricActive: Boolean,
    onToggleParametric: () -> Unit,
    slopeA: Double,
    onSlopeAChange: (Double) -> Unit,
    paramM: Double,
    onParamMChange: (Double) -> Unit,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    intersectionResult: IntersectionResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isParametricActive) ParametricPink.copy(alpha = 0.5f) else Slate800,
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with toggle switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isParametricActive) ParametricPink else Slate600)
                    )
                    Text(
                        text = "المناقشة البيانية بالوسيط (m)",
                        color = if (isParametricActive) ParametricPink else Slate200,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Switch(
                    checked = isParametricActive,
                    onCheckedChange = { onToggleParametric() },
                    modifier = Modifier.testTag("parametric_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ParametricPink,
                        uncheckedThumbColor = Slate400,
                        uncheckedTrackColor = Slate800
                    )
                )
            }

            AnimatedVisibility(
                visible = isParametricActive,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Line Formula & Value Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "معادلة المستقيم: y = ${formatSlopeEquation(slopeA, paramM)}",
                            color = ReferenceBlue,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ParametricPinkDark.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ParametricPink.copy(alpha = 0.7f))
                        ) {
                            Text(
                                text = "m = ${if (paramM >= 0) "+" else ""}${String.format("%.2f", paramM)}",
                                color = ParametricPink,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Slope Selector Chips
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "نوع المناقشة وميل المستقيم (a):",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SlopeChip("أفقية (a=0)", slopeA == 0.0, Modifier.weight(1f)) { onSlopeAChange(0.0) }
                            SlopeChip("مائلة (a=1)", slopeA == 1.0, Modifier.weight(1f)) { onSlopeAChange(1.0) }
                            SlopeChip("مائلة (a=-1)", slopeA == -1.0, Modifier.weight(1f)) { onSlopeAChange(-1.0) }
                            SlopeChip("مائلة (a=2)", slopeA == 2.0, Modifier.weight(1f)) { onSlopeAChange(2.0) }
                        }
                    }

                    // Slider for m
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Slider(
                            value = paramM.toFloat(),
                            onValueChange = { onParamMChange(it.toDouble()) },
                            valueRange = -6f..6f,
                            modifier = Modifier.testTag("param_m_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = ParametricPink,
                                activeTrackColor = ParametricPink,
                                inactiveTrackColor = Slate800
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("-6.0", color = Slate600, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            Text("0.0", color = Slate600, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            Text("+6.0", color = Slate600, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    // Fine controls & Animation button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onParamMChange((paramM - 0.1).coerceIn(-10.0, 10.0)) },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = Slate200),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("step_down_m_button"),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Text("-0.1", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }

                        Button(
                            onClick = onTogglePlay,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlaying) ParametricPinkDark else ParametricPink,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.8f).testTag("play_pause_m_button"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "إيقاف مؤقت" else "تحريك m",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isPlaying) "إيقاف m" else "تحريك m تلقائياً",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { onParamMChange((paramM + 0.1).coerceIn(-10.0, 10.0)) },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = Slate200),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("step_up_m_button"),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Text("+0.1", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                    }

                    // Solution Status Card
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Slate950,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "الوضع النسبي وعدد الحلول:",
                                color = Slate400,
                                fontSize = 11.sp
                            )
                            Text(
                                text = intersectionResult.solutionSummaryAr,
                                color = if (intersectionResult.points.isEmpty()) DangerRed else EmeraldGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = intersectionResult.detailsAr,
                                color = Slate300,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                            if (intersectionResult.points.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    intersectionResult.points.forEachIndexed { i, pt ->
                                        Text(
                                            text = "• P${i + 1}(${String.format("%.2f", pt.x)}, ${String.format("%.2f", pt.y)})",
                                            color = EmeraldGreen,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlopeChip(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) ReferenceBlue.copy(alpha = 0.25f) else Slate800,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) ReferenceBlue else Slate700
        )
    ) {
        Box(modifier = Modifier.padding(vertical = 5.dp), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (isSelected) ReferenceBlue else Slate400,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

private fun formatSlopeEquation(a: Double, m: Double): String {
    val aPart = when {
        kotlin.math.abs(a) < 1e-4 -> ""
        kotlin.math.abs(a - 1.0) < 1e-4 -> "x"
        kotlin.math.abs(a + 1.0) < 1e-4 -> "-x"
        else -> "${String.format("%.1f", a)}x"
    }
    val mPart = when {
        kotlin.math.abs(m) < 1e-4 -> if (aPart.isEmpty()) "0" else ""
        m > 0 -> if (aPart.isNotEmpty()) " + ${String.format("%.2f", m)}" else String.format("%.2f", m)
        else -> if (aPart.isNotEmpty()) " - ${String.format("%.2f", kotlin.math.abs(m))}" else "-${String.format("%.2f", kotlin.math.abs(m))}"
    }
    return (aPart + mPart).trim()
}
