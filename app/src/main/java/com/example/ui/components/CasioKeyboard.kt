package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import com.example.ui.theme.*

@Composable
fun CasioKeyboard(
    onInsert: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAdvanced by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
        color = Slate950
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "لوحة مفاتيح CASIO الرياضية",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (showAdvanced) "◀ الدوال الأساسية" else "دوال متقدمة (sin, exp, π) ▶",
                    color = ReferenceBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { showAdvanced = !showAdvanced }
                        .padding(4.dp)
                )
            }

            if (showAdvanced) {
                // Advanced Math Functions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CasioKey("sin", "sin(", CasioKeyType.FUNCTION, Modifier.weight(1f)) { onInsert("sin(") }
                    CasioKey("cos", "cos(", CasioKeyType.FUNCTION, Modifier.weight(1f)) { onInsert("cos(") }
                    CasioKey("tan", "tan(", CasioKeyType.FUNCTION, Modifier.weight(1f)) { onInsert("tan(") }
                    CasioKey("eˣ", "exp(", CasioKeyType.FUNCTION, Modifier.weight(1f)) { onInsert("exp(") }
                    CasioKey("π", "pi", CasioKeyType.FUNCTION, Modifier.weight(1f)) { onInsert("pi") }
                }
            }

            // Row 1: x, x², xⁿ, √x, DEL
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CasioKey("x", "x", CasioKeyType.VARIABLE, Modifier.weight(1f)) { onInsert("x") }
                CasioKey("x²", "^2", CasioKeyType.FUNCTION, Modifier.weight(1f)) { onInsert("^2") }
                CasioKey("xⁿ", "^", CasioKeyType.FUNCTION, Modifier.weight(1f)) { onInsert("^") }
                CasioKey("√x", "sqrt(", CasioKeyType.FUNCTION, Modifier.weight(1f)) { onInsert("sqrt(") }
                CasioKey("DEL", "del", CasioKeyType.DANGER, Modifier.weight(1f)) { onDelete() }
            }

            // Row 2: (, ), /, |x|, AC
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CasioKey("(", "(", CasioKeyType.FUNCTION, Modifier.weight(1f)) { onInsert("(") }
                CasioKey(")", ")", CasioKeyType.FUNCTION, Modifier.weight(1f)) { onInsert(")") }
                CasioKey("÷", "/", CasioKeyType.OPERATOR, Modifier.weight(1f)) { onInsert("/") }
                CasioKey("|x|", "abs(", CasioKeyType.FUNCTION, Modifier.weight(1f)) { onInsert("abs(") }
                CasioKey("AC", "ac", CasioKeyType.DANGER, Modifier.weight(1f)) { onClear() }
            }

            // Row 3: 7, 8, 9, ×, -
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CasioKey("7", "7", CasioKeyType.NUMBER, Modifier.weight(1f)) { onInsert("7") }
                CasioKey("8", "8", CasioKeyType.NUMBER, Modifier.weight(1f)) { onInsert("8") }
                CasioKey("9", "9", CasioKeyType.NUMBER, Modifier.weight(1f)) { onInsert("9") }
                CasioKey("×", "*", CasioKeyType.OPERATOR, Modifier.weight(1f)) { onInsert("*") }
                CasioKey("−", "-", CasioKeyType.OPERATOR, Modifier.weight(1f)) { onInsert("-") }
            }

            // Row 4: 4, 5, 6, +, ln
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CasioKey("4", "4", CasioKeyType.NUMBER, Modifier.weight(1f)) { onInsert("4") }
                CasioKey("5", "5", CasioKeyType.NUMBER, Modifier.weight(1f)) { onInsert("5") }
                CasioKey("6", "6", CasioKeyType.NUMBER, Modifier.weight(1f)) { onInsert("6") }
                CasioKey("+", "+", CasioKeyType.OPERATOR, Modifier.weight(1f)) { onInsert("+") }
                CasioKey("ln", "ln(", CasioKeyType.FUNCTION, Modifier.weight(1f)) { onInsert("ln(") }
            }

            // Row 5: 1, 2, 3, 0, .
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CasioKey("1", "1", CasioKeyType.NUMBER, Modifier.weight(1f)) { onInsert("1") }
                CasioKey("2", "2", CasioKeyType.NUMBER, Modifier.weight(1f)) { onInsert("2") }
                CasioKey("3", "3", CasioKeyType.NUMBER, Modifier.weight(1f)) { onInsert("3") }
                CasioKey("0", "0", CasioKeyType.NUMBER, Modifier.weight(1f)) { onInsert("0") }
                CasioKey(".", ".", CasioKeyType.NUMBER, Modifier.weight(1f)) { onInsert(".") }
            }
        }
    }
}

enum class CasioKeyType {
    NUMBER,
    OPERATOR,
    FUNCTION,
    VARIABLE,
    DANGER
}

@Composable
private fun CasioKey(
    label: String,
    tag: String,
    type: CasioKeyType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = when (type) {
        CasioKeyType.NUMBER -> Slate800
        CasioKeyType.OPERATOR -> CasioOpBg
        CasioKeyType.FUNCTION -> Slate700
        CasioKeyType.VARIABLE -> Slate700
        CasioKeyType.DANGER -> DangerRed
    }

    val textColor = when (type) {
        CasioKeyType.NUMBER -> Slate100
        CasioKeyType.OPERATOR -> Color.White
        CasioKeyType.FUNCTION -> ReferenceBlue
        CasioKeyType.VARIABLE -> MathAmber
        CasioKeyType.DANGER -> Color.White
    }

    val borderColor = when (type) {
        CasioKeyType.OPERATOR -> Color(0xFF60A5FA)
        CasioKeyType.DANGER -> Color(0xFFF87171)
        CasioKeyType.VARIABLE -> MathAmber.copy(alpha = 0.5f)
        else -> Slate600
    }

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .testTag("casio_key_$tag"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
