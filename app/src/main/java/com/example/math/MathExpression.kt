package com.example.math

import kotlin.math.*

sealed interface MathNode {
    fun evaluate(x: Double): Double
}

data class ConstantNode(val value: Double) : MathNode {
    override fun evaluate(x: Double): Double = value
}

object VariableNode : MathNode {
    override fun evaluate(x: Double): Double = x
}

data class UnaryOpNode(val op: Char, val child: MathNode) : MathNode {
    override fun evaluate(x: Double): Double {
        val v = child.evaluate(x)
        return when (op) {
            '-' -> -v
            '+' -> v
            else -> Double.NaN
        }
    }
}

data class BinaryOpNode(val op: Char, val left: MathNode, val right: MathNode) : MathNode {
    override fun evaluate(x: Double): Double {
        val l = left.evaluate(x)
        val r = right.evaluate(x)
        if (l.isNaN() || r.isNaN()) return Double.NaN
        return when (op) {
            '+' -> l + r
            '-' -> l - r
            '*' -> l * r
            '/' -> {
                if (abs(r) < 1e-14) Double.NaN else l / r
            }
            '^' -> {
                // If l is negative and r is integer or fractional
                if (l < 0.0) {
                    val rInt = r.toLong()
                    if (abs(r - rInt) < 1e-9) {
                        if (rInt % 2L == 0L) (-l).pow(r) else -((-l).pow(r))
                    } else {
                        Double.NaN
                    }
                } else {
                    l.pow(r)
                }
            }
            '%' -> l % r
            else -> Double.NaN
        }
    }
}

data class FunctionCallNode(val name: String, val args: List<MathNode>) : MathNode {
    override fun evaluate(x: Double): Double {
        val evaluatedArgs = args.map { it.evaluate(x) }
        if (evaluatedArgs.any { it.isNaN() }) return Double.NaN
        val arg0 = evaluatedArgs.getOrNull(0) ?: return Double.NaN

        return when (name) {
            "sqrt" -> if (arg0 < 0.0) Double.NaN else sqrt(arg0)
            "abs" -> abs(arg0)
            "ln", "log" -> if (arg0 <= 0.0) Double.NaN else ln(arg0)
            "log10" -> if (arg0 <= 0.0) Double.NaN else log10(arg0)
            "exp" -> exp(arg0)
            "sin" -> sin(arg0)
            "cos" -> cos(arg0)
            "tan" -> {
                // Check near pi/2 + k*pi
                val mod = (arg0 - PI / 2.0) % PI
                if (abs(mod) < 1e-6 || abs(mod - PI) < 1e-6) Double.NaN else tan(arg0)
            }
            "asin", "arcsin" -> if (arg0 < -1.0 || arg0 > 1.0) Double.NaN else asin(arg0)
            "acos", "arccos" -> if (arg0 < -1.0 || arg0 > 1.0) Double.NaN else acos(arg0)
            "atan", "arctan" -> atan(arg0)
            "floor" -> floor(arg0)
            "ceil" -> ceil(arg0)
            "round" -> round(arg0)
            "sinh" -> sinh(arg0)
            "cosh" -> cosh(arg0)
            "tanh" -> tanh(arg0)
            else -> Double.NaN
        }
    }
}

class CompiledFunction(val rawExpression: String, val root: MathNode) {
    fun eval(x: Double): Double {
        return try {
            val res = root.evaluate(x)
            if (res.isInfinite() || res.isNaN()) Double.NaN else res
        } catch (_: Exception) {
            Double.NaN
        }
    }
}
