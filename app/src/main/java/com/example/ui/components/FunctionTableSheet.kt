package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.math.CompiledFunction
import com.example.math.IntersectionSolver
import com.example.ui.theme.*

@Composable
fun FunctionTableSheet(
    compiledFunction: CompiledFunction?,
    xMin: Double,
    xMax: Double,
    onDismiss: () -> Unit
) {
    var stepSize by remember { mutableStateOf(0.5) }

    val tableValues = remember(compiledFunction, xMin, xMax, stepSize) {
        if (compiledFunction == null) emptyList()
        else {
            val list = mutableListOf<Pair<Double, Double>>()
            var x = xMin
            val safeStep = stepSize.coerceAtLeast(0.1)
            while (x <= xMax + 1e-6) {
                val y = compiledFunction.eval(x)
                list.add(x to y)
                x += safeStep
            }
            list
        }
    }

    val extrema = remember(compiledFunction, xMin, xMax) {
        if (compiledFunction == null) emptyList()
        else IntersectionSolver.findLocalExtrema(compiledFunction, xMin, xMax)
    }

    val yIntercept = remember(compiledFunction) {
        compiledFunction?.eval(0.0) ?: Double.NaN
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Slate700, RoundedCornerShape(20.dp)),
            color = Slate900
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = null,
                            tint = ReferenceBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "جدول القيم وخواص الدالة",
                            color = Slate100,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_table_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Slate400)
                    }
                }

                // Summary properties
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = Slate950,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("التقاطع مع محور Y:", color = Slate400, fontSize = 10.sp)
                            Text(
                                text = if (yIntercept.isNaN()) "غير معرّف" else "f(0) = ${String.format("%.2f", yIntercept)}",
                                color = if (yIntercept.isNaN()) DangerRed else MathAmber,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = Slate950,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("القيم الحدية المحلية:", color = Slate400, fontSize = 10.sp)
                            Text(
                                text = if (extrema.isEmpty()) "لا توجد في هذا المجال" else "${extrema.size} قيم حدية",
                                color = ReferenceBlue,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Step Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("خطوة الجدول (Step):", color = Slate400, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(0.25, 0.5, 1.0, 2.0).forEach { s ->
                            FilterChip(
                                selected = stepSize == s,
                                onClick = { stepSize = s },
                                label = { Text(s.toString(), fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ReferenceBlue,
                                    selectedLabelColor = Slate950,
                                    containerColor = Slate800,
                                    labelColor = Slate300
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(Slate800)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("x (السابقة)", color = Slate200, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("f(x) (الصورة)", color = MathAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Values List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                        .background(Slate950)
                ) {
                    items(tableValues) { (x, y) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Slate800)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format("%.2f", x),
                                color = Slate200,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (y.isNaN()) "غير معرّف" else String.format("%.4f", y),
                                color = if (y.isNaN()) DangerRed else MathAmber,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
