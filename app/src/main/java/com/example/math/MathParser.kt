package com.example.math

import kotlin.math.E
import kotlin.math.PI

class MathParseException(message: String) : Exception(message)

class MathParser(private val tokens: List<Token>) {
    private var pos = 0

    fun parse(): MathNode {
        if (tokens.isEmpty()) {
            throw MathParseException("Expression is empty")
        }
        val node = parseExpression()
        if (pos < tokens.size) {
            val unexpected = tokens[pos]
            throw MathParseException("Unexpected token at end: $unexpected")
        }
        return node
    }

    // Expr = Term ( ('+' | '-') Term )*
    private fun parseExpression(): MathNode {
        var node = parseTerm()
        while (pos < tokens.size) {
            val token = tokens[pos]
            if (token is Token.Operator && (token.op == '+' || token.op == '-')) {
                pos++
                val right = parseTerm()
                node = BinaryOpNode(token.op, node, right)
            } else {
                break
            }
        }
        return node
    }

    // Term = Power ( ('*' | '/' | '%') Power )*
    private fun parseTerm(): MathNode {
        var node = parseUnary()
        while (pos < tokens.size) {
            val token = tokens[pos]
            if (token is Token.Operator && (token.op == '*' || token.op == '/' || token.op == '%')) {
                pos++
                val right = parseUnary()
                node = BinaryOpNode(token.op, node, right)
            } else {
                break
            }
        }
        return node
    }

    // Unary = ('-' | '+')* Power
    private fun parseUnary(): MathNode {
        if (pos < tokens.size) {
            val token = tokens[pos]
            if (token is Token.Operator && (token.op == '-' || token.op == '+')) {
                pos++
                val child = parseUnary()
                return UnaryOpNode(token.op, child)
            }
        }
        return parsePower()
    }

    // Power = Factor ( '^' Unary )* (right-associative)
    private fun parsePower(): MathNode {
        val left = parseFactor()
        if (pos < tokens.size) {
            val token = tokens[pos]
            if (token is Token.Operator && token.op == '^') {
                pos++
                val right = parseUnary()
                return BinaryOpNode('^', left, right)
            }
        }
        return left
    }

    // Factor = Number | Variable | Constant | FunctionCall | '(' Expr ')'
    private fun parseFactor(): MathNode {
        if (pos >= tokens.size) {
            throw MathParseException("Unexpected end of expression")
        }

        return when (val token = tokens[pos]) {
            is Token.Number -> {
                pos++
                ConstantNode(token.value)
            }
            is Token.Identifier -> {
                pos++
                when (token.name) {
                    "x" -> VariableNode
                    "pi" -> ConstantNode(PI)
                    "e" -> ConstantNode(E)
                    else -> {
                        // Function call like sin(x) or sqrt(x)
                        val funcName = token.name
                        if (pos < tokens.size && tokens[pos] is Token.LeftParen) {
                            pos++ // consume '('
                            val args = mutableListOf<MathNode>()
                            if (pos < tokens.size && tokens[pos] !is Token.RightParen) {
                                args.add(parseExpression())
                                while (pos < tokens.size && tokens[pos] is Token.Comma) {
                                    pos++
                                    args.add(parseExpression())
                                }
                            }
                            if (pos >= tokens.size || tokens[pos] !is Token.RightParen) {
                                throw MathParseException("Missing closing parenthesis for $funcName")
                            }
                            pos++ // consume ')'
                            FunctionCallNode(funcName, args)
                        } else {
                            // If just "sqrt x" or something, attempt to parse next factor as argument
                            if (isKnownFunction(funcName)) {
                                val arg = parseFactor()
                                FunctionCallNode(funcName, listOf(arg))
                            } else {
                                throw MathParseException("Unknown identifier: ${token.name}")
                            }
                        }
                    }
                }
            }
            is Token.LeftParen -> {
                pos++
                val inner = parseExpression()
                if (pos >= tokens.size || tokens[pos] !is Token.RightParen) {
                    throw MathParseException("Missing closing parenthesis ')'")
                }
                pos++
                inner
            }
            else -> {
                throw MathParseException("Unexpected token: $token")
            }
        }
    }

    private fun isKnownFunction(name: String): Boolean {
        return name in setOf(
            "sqrt", "abs", "ln", "log", "log10", "exp",
            "sin", "cos", "tan", "asin", "acos", "atan",
            "floor", "ceil", "round", "sinh", "cosh", "tanh"
        )
    }

    companion object {
        fun compile(rawFormula: String): Result<CompiledFunction> {
            return try {
                val cleaned = sanitizeFormula(rawFormula)
                val tokens = MathTokenizer(cleaned).tokenize()
                val parser = MathParser(tokens)
                val root = parser.parse()
                Result.success(CompiledFunction(rawFormula, root))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        private fun sanitizeFormula(raw: String): String {
            var expr = raw.trim()
            // Remove f(x)= or y=
            expr = expr.replace(Regex("""^[a-zA-Z]\s*\(\s*x\s*\)\s*="""), "")
            expr = expr.replace(Regex("""^[yY]\s*="""), "")
            // Normalize unicode superscripts: x² -> x^2, x³ -> x^3
            expr = expr.replace("²", "^2")
                .replace("³", "^3")
                .replace("⁴", "^4")
                .replace("⁵", "^5")
            return expr
        }
    }
}
