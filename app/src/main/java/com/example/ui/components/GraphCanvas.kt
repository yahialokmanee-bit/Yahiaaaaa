package com.example.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.CompiledFunction
import com.example.math.Point2D
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun GraphCanvas(
    compiledFunction: CompiledFunction?,
    isParametricActive: Boolean,
    slopeA: Double,
    paramM: Double,
    intersections: List<Point2D>,
    xMin: Double,
    xMax: Double,
    yMin: Double,
    yMax: Double,
    onBoundsChange: (Double, Double, Double, Double) -> Unit,
    onResetZoom: () -> Unit,
    modifier: Modifier = Modifier,
    isFullscreen: Boolean = false,
    onToggleFullscreen: () -> Unit = {}
) {
    // Touch inspector coordinate
    var inspectX by remember { mutableStateOf<Double?>(null) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("math_graph_canvas")
                .pointerInput(xMin, xMax, yMin, yMax) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val currentSpanX = xMax - xMin
                        val currentSpanY = yMax - yMin

                        // Apply zoom around center
                        val newSpanX = (currentSpanX / zoom).coerceIn(1.0, 100.0)
                        val newSpanY = (currentSpanY / zoom).coerceIn(1.0, 100.0)

                        val centerX = (xMin + xMax) / 2.0
                        val centerY = (yMin + yMax) / 2.0

                        // Pan delta in math units
                        val panDeltaX = (pan.x / size.width.toDouble()) * currentSpanX
                        val panDeltaY = (pan.y / size.height.toDouble()) * currentSpanY

                        val nextCenterX = centerX - panDeltaX
                        val nextCenterY = centerY + panDeltaY

                        val newXMin = nextCenterX - newSpanX / 2.0
                        val newXMax = nextCenterX + newSpanX / 2.0
                        val newYMin = nextCenterY - newSpanY / 2.0
                        val newYMax = nextCenterY + newSpanY / 2.0

                        onBoundsChange(newXMin, newXMax, newYMin, newYMax)
                    }
                }
                .pointerInput(xMin, xMax) {
                    detectTapGestures(
                        onPress = { offset ->
                            val xMath = xMin + (offset.x / size.width.toDouble()) * (xMax - xMin)
                            inspectX = xMath
                        },
                        onTap = { offset ->
                            val xMath = xMin + (offset.x / size.width.toDouble()) * (xMax - xMin)
                            inspectX = if (inspectX == null) xMath else null
                        }
                    )
                }
                .pointerInput(xMin, xMax) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            inspectX = xMin + (offset.x / size.width.toDouble()) * (xMax - xMin)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            inspectX = xMin + (change.position.x / size.width.toDouble()) * (xMax - xMin)
                        },
                        onDragEnd = {
                            // Keep last inspected or let tap toggle it
                        }
                    )
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            if (canvasW <= 0f || canvasH <= 0f) return@Canvas

            fun toCanvasX(x: Double): Float = ((x - xMin) / (xMax - xMin) * canvasW).toFloat()
            fun toCanvasY(y: Double): Float = ((yMax - y) / (yMax - yMin) * canvasH).toFloat()

            // 1. Grid lines and tick marks
            val spanX = xMax - xMin
            val spanY = yMax - yMin

            // Determine appropriate tick step (1, 2, 5, 10, etc.)
            val roughStepX = spanX / 8.0
            val gridStepX = calculateNiceStep(roughStepX)
            val roughStepY = spanY / 8.0
            val gridStepY = calculateNiceStep(roughStepY)

            val gridPaint = Paint().apply {
                color = android.graphics.Color.argb(120, 148, 163, 184)
                textSize = 28f
                isAntiAlias = true
            }

            // Draw vertical grid lines & X ticks
            val firstTickX = floor(xMin / gridStepX) * gridStepX
            var currentTickX = firstTickX
            while (currentTickX <= xMax) {
                val cx = toCanvasX(currentTickX)
                if (cx in 0f..canvasW) {
                    drawLine(
                        color = if (abs(currentTickX) < 1e-6) Slate400 else Slate800,
                        start = Offset(cx, 0f),
                        end = Offset(cx, canvasH),
                        strokeWidth = if (abs(currentTickX) < 1e-6) 2.5f else 1f
                    )

                    // Draw tick number on or near X axis
                    if (abs(currentTickX) > 1e-6) {
                        val originY = toCanvasY(0.0).coerceIn(40f, canvasH - 30f)
                        drawContext.canvas.nativeCanvas.drawText(
                            formatTick(currentTickX),
                            cx + 4f,
                            originY + 28f,
                            gridPaint
                        )
                    }
                }
                currentTickX += gridStepX
            }

            // Draw horizontal grid lines & Y ticks
            val firstTickY = floor(yMin / gridStepY) * gridStepY
            var currentTickY = firstTickY
            while (currentTickY <= yMax) {
                val cy = toCanvasY(currentTickY)
                if (cy in 0f..canvasH) {
                    drawLine(
                        color = if (abs(currentTickY) < 1e-6) Slate400 else Slate800,
                        start = Offset(0f, cy),
                        end = Offset(canvasW, cy),
                        strokeWidth = if (abs(currentTickY) < 1e-6) 2.5f else 1f
                    )

                    // Draw tick number on or near Y axis
                    if (abs(currentTickY) > 1e-6) {
                        val originX = toCanvasX(0.0).coerceIn(10f, canvasW - 70f)
                        drawContext.canvas.nativeCanvas.drawText(
                            formatTick(currentTickY),
                            originX + 6f,
                            cy - 6f,
                            gridPaint
                        )
                    }
                }
                currentTickY += gridStepY
            }

            // Draw Origin (0,0) label
            val originX = toCanvasX(0.0)
            val originY = toCanvasY(0.0)
            if (originX in 0f..canvasW && originY in 0f..canvasH) {
                drawContext.canvas.nativeCanvas.drawText(
                    "O",
                    originX - 25f,
                    originY + 28f,
                    gridPaint.apply { color = android.graphics.Color.argb(200, 248, 250, 252) }
                )
            }

            // 2. Draw Parametric Lines if active
            if (isParametricActive) {
                // Dashed reference line y = ax
                val p1x = xMin
                val p1y = slopeA * p1x
                val p2x = xMax
                val p2y = slopeA * p2x

                val refPath = Path().apply {
                    moveTo(toCanvasX(p1x), toCanvasY(p1y))
                    lineTo(toCanvasX(p2x), toCanvasY(p2y))
                }
                drawPath(
                    path = refPath,
                    color = ReferenceBlue,
                    style = Stroke(
                        width = 3.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f),
                        cap = StrokeCap.Round
                    )
                )

                // Parametric Line y = ax + m (Glowing vibrant pink)
                val lm1y = slopeA * p1x + paramM
                val lm2y = slopeA * p2x + paramM

                // Glow shadow
                drawLine(
                    color = ParametricPink.copy(alpha = 0.35f),
                    start = Offset(toCanvasX(p1x), toCanvasY(lm1y)),
                    end = Offset(toCanvasX(p2x), toCanvasY(lm2y)),
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
                // Main line
                drawLine(
                    color = ParametricPink,
                    start = Offset(toCanvasX(p1x), toCanvasY(lm1y)),
                    end = Offset(toCanvasX(p2x), toCanvasY(lm2y)),
                    strokeWidth = 4.5f,
                    cap = StrokeCap.Round
                )
            }

            // 3. Draw Main Function Curve f(x)
            if (compiledFunction != null) {
                val samplePoints = 700
                val dx = spanX / samplePoints
                val curvePath = Path()
                var inSubpath = false
                var prevYVal = Double.NaN

                for (i in 0..samplePoints) {
                    val x = xMin + i * dx
                    val y = compiledFunction.eval(x)

                    val isValid = !y.isNaN() && !y.isInfinite()
                    // Detect vertical asymptote jump
                    val isAsymptoteJump = !prevYVal.isNaN() && isValid && abs(y - prevYVal) > (spanY * 1.5)

                    if (isValid && !isAsymptoteJump) {
                        val cx = toCanvasX(x)
                        val cy = toCanvasY(y)

                        if (!inSubpath) {
                            curvePath.moveTo(cx, cy)
                            inSubpath = true
                        } else {
                            curvePath.lineTo(cx, cy)
                        }
                    } else {
                        inSubpath = false
                    }
                    prevYVal = if (isValid) y else Double.NaN
                }

                // Draw curve glow
                drawPath(
                    path = curvePath,
                    color = MathAmberGlow,
                    style = Stroke(width = 9f, cap = StrokeCap.Round)
                )
                // Draw solid curve
                drawPath(
                    path = curvePath,
                    color = MathAmber,
                    style = Stroke(width = 4.5f, cap = StrokeCap.Round)
                )
            }

            // 4. Draw Intersections Points
            if (isParametricActive && intersections.isNotEmpty()) {
                val ptPaint = Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 28f
                    isAntiAlias = true
                    isFakeBoldText = true
                }

                intersections.forEachIndexed { idx, pt ->
                    val cx = toCanvasX(pt.x)
                    val cy = toCanvasY(pt.y)

                    if (cx in -20f..(canvasW + 20f) && cy in -20f..(canvasH + 20f)) {
                        // Emerald glow outer ring
                        drawCircle(
                            color = EmeraldGreen.copy(alpha = 0.35f),
                            radius = 22f,
                            center = Offset(cx, cy)
                        )
                        // Outer white ring
                        drawCircle(
                            color = Color.White,
                            radius = 12f,
                            center = Offset(cx, cy)
                        )
                        // Inner vibrant green dot
                        drawCircle(
                            color = EmeraldGreen,
                            radius = 8f,
                            center = Offset(cx, cy)
                        )

                        // Label
                        val tagText = "P${idx + 1}(${String.format("%.2f", pt.x)}, ${String.format("%.2f", pt.y)})"
                        drawContext.canvas.nativeCanvas.drawText(
                            tagText,
                            cx + 18f,
                            cy - 12f,
                            ptPaint
                        )
                    }
                }
            }

            // 5. Draw Inspector Crosshair if active
            inspectX?.let { ix ->
                if (compiledFunction != null) {
                    val iy = compiledFunction.eval(ix)
                    val icx = toCanvasX(ix)
                    if (icx in 0f..canvasW) {
                        // Vertical guideline
                        drawLine(
                            color = Slate400.copy(alpha = 0.6f),
                            start = Offset(icx, 0f),
                            end = Offset(icx, canvasH),
                            strokeWidth = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        if (!iy.isNaN() && !iy.isInfinite()) {
                            val icy = toCanvasY(iy)
                            // Horizontal guideline
                            drawLine(
                                color = Slate400.copy(alpha = 0.6f),
                                start = Offset(0f, icy),
                                end = Offset(canvasW, icy),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                            // Target point
                            drawCircle(
                                color = MathAmber,
                                radius = 9f,
                                center = Offset(icx, icy)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 4f,
                                center = Offset(icx, icy)
                            )
                        }
                    }
                }
            }
        }

        // Top-left HUD badge: Current formula or Inspector details
        inspectX?.let { ix ->
            val iy = compiledFunction?.eval(ix) ?: Double.NaN
            Surface(
                color = Slate950.copy(alpha = 0.85f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "X = ${String.format("%.2f", ix)} | ",
                        color = Slate200,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (iy.isNaN()) "غير معرّف" else "Y = ${String.format("%.2f", iy)}",
                        color = if (iy.isNaN()) DangerRed else MathAmber,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Zoom / Pan Control Buttons Overlay (Top-Right)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilledTonalIconButton(
                onClick = {
                    val cx = (xMin + xMax) / 2.0
                    val cy = (yMin + yMax) / 2.0
                    val spanX = (xMax - xMin) * 0.75
                    val spanY = (yMax - yMin) * 0.75
                    onBoundsChange(cx - spanX / 2.0, cx + spanX / 2.0, cy - spanY / 2.0, cy + spanY / 2.0)
                },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("zoom_in_button"),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Slate800.copy(alpha = 0.9f),
                    contentColor = Slate200
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "تكبير", modifier = Modifier.size(18.dp))
            }

            FilledTonalIconButton(
                onClick = {
                    val cx = (xMin + xMax) / 2.0
                    val cy = (yMin + yMax) / 2.0
                    val spanX = (xMax - xMin) * 1.333
                    val spanY = (yMax - yMin) * 1.333
                    onBoundsChange(cx - spanX / 2.0, cx + spanX / 2.0, cy - spanY / 2.0, cy + spanY / 2.0)
                },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("zoom_out_button"),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Slate800.copy(alpha = 0.9f),
                    contentColor = Slate200
                )
            ) {
                Icon(Icons.Default.Remove, contentDescription = "تصغير", modifier = Modifier.size(18.dp))
            }

            FilledTonalIconButton(
                onClick = onResetZoom,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("reset_zoom_button"),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Slate800.copy(alpha = 0.9f),
                    contentColor = Slate200
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "إعادة ضبط", modifier = Modifier.size(18.dp))
            }

            FilledTonalIconButton(
                onClick = onToggleFullscreen,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("toggle_fullscreen_button"),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Slate800.copy(alpha = 0.9f),
                    contentColor = Slate200
                )
            ) {
                Icon(
                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = "ملء الشاشة",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Bottom graph legends
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Slate950.copy(alpha = 0.75f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main curve indicator
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MathAmber))
                Text("(Cf)", color = Slate200, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }

            if (isParametricActive) {
                // y = ax
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ReferenceBlue))
                    Text("y = ax", color = Slate200, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }

                // y = ax + m
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ParametricPink))
                    Text("y = ax + m", color = ParametricPink, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }

                // Intersections
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(EmeraldGreen))
                    Text(
                        "${intersections.size} نقاط تقاطع",
                        color = EmeraldGreen,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

private fun calculateNiceStep(rough: Double): Double {
    if (rough <= 0) return 1.0
    val exponent = floor(log10(rough))
    val fraction = rough / 10.0.pow(exponent)
    val niceFraction = when {
        fraction < 1.5 -> 1.0
        fraction < 3.0 -> 2.0
        fraction < 7.0 -> 5.0
        else -> 10.0
    }
    return niceFraction * 10.0.pow(exponent)
}

private fun formatTick(value: Double): String {
    return if (abs(value - round(value)) < 1e-5) {
        round(value).toLong().toString()
    } else {
        String.format("%.1f", value)
    }
}
