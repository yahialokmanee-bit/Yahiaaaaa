package com.example.math

sealed class Token {
    data class Number(val value: Double) : Token()
    data class Identifier(val name: String) : Token()
    data class Operator(val op: Char) : Token()
    object LeftParen : Token()
    object RightParen : Token()
    object Comma : Token()
}

class MathTokenizer(private val input: String) {
    private var pos = 0
    private val len = input.length

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        while (pos < len) {
            val c = input[pos]
            when {
                c.isWhitespace() -> pos++
                c.isDigit() || c == '.' -> tokens.add(readNumber())
                c.isLetter() || c == '√' || c == 'π' -> tokens.add(readIdentifier())
                c == '(' -> {
                    tokens.add(Token.LeftParen)
                    pos++
                }
                c == ')' -> {
                    tokens.add(Token.RightParen)
                    pos++
                }
                c == '|' -> {
                    // Pipe syntax for abs: convert |x| into abs(x)
                    tokens.add(Token.Identifier("abs"))
                    tokens.add(Token.LeftParen)
                    pos++
                }
                c == ',' -> {
                    tokens.add(Token.Comma)
                    pos++
                }
                c in "+-*/^%" -> {
                    tokens.add(Token.Operator(c))
                    pos++
                }
                c == '×' || c == '•' || c == '*' -> {
                    tokens.add(Token.Operator('*'))
                    pos++
                }
                c == '÷' || c == ':' -> {
                    tokens.add(Token.Operator('/'))
                    pos++
                }
                c == '−' || c == '–' || c == '—' -> {
                    tokens.add(Token.Operator('-'))
                    pos++
                }
                else -> {
                    // Skip or handle unknown
                    pos++
                }
            }
        }
        return insertImplicitMultiplication(tokens)
    }

    private fun readNumber(): Token.Number {
        val start = pos
        var hasDot = false
        while (pos < len) {
            val c = input[pos]
            if (c.isDigit()) {
                pos++
            } else if (c == '.' && !hasDot) {
                hasDot = true
                pos++
            } else {
                break
            }
        }
        val numStr = input.substring(start, pos)
        val value = numStr.toDoubleOrNull() ?: 0.0
        return Token.Number(value)
    }

    private fun readIdentifier(): Token.Identifier {
        val start = pos
        if (input[pos] == '√') {
            pos++
            return Token.Identifier("sqrt")
        }
        if (input[pos] == 'π') {
            pos++
            return Token.Identifier("pi")
        }
        while (pos < len && (input[pos].isLetter() || input[pos].isDigit() || input[pos] == '_')) {
            pos++
        }
        val name = input.substring(start, pos).lowercase()
        return Token.Identifier(name)
    }

    private fun insertImplicitMultiplication(tokens: List<Token>): List<Token> {
        if (tokens.isEmpty()) return tokens
        val result = mutableListOf<Token>()
        for (i in tokens.indices) {
            val curr = tokens[i]
            result.add(curr)
            if (i < tokens.size - 1) {
                val next = tokens[i + 1]
                val needsMult = when {
                    // Number followed by variable, identifier or ( : 2x, 2(x), 3sqrt(x)
                    curr is Token.Number && (next is Token.Identifier || next is Token.LeftParen) -> true
                    // Variable x followed by ( or another identifier: x(x+1)
                    curr is Token.Identifier && curr.name == "x" && (next is Token.LeftParen || (next is Token.Identifier && next.name == "x")) -> true
                    // ) followed by ( or variable or number: (x+1)(x-1), (x+1)2, (x+1)x
                    curr is Token.RightParen && (next is Token.LeftParen || next is Token.Identifier || next is Token.Number) -> true
                    else -> false
                }
                if (needsMult) {
                    result.add(Token.Operator('*'))
                }
            }
        }
        return result
    }
}
