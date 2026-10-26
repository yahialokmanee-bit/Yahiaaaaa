package com.example

import com.example.math.IntersectionSolver
import com.example.math.MathParser
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class MathEngineTest {

    @Test
    fun testRationalFunctionEvaluation() {
        val result = MathParser.compile("x + 1/x")
        assertTrue(result.isSuccess)
        val fn = result.getOrThrow()

        assertEquals(2.0, fn.eval(1.0), 1e-6)
        assertEquals(2.5, fn.eval(2.0), 1e-6)
        assertEquals(-2.0, fn.eval(-1.0), 1e-6)
        assertTrue(fn.eval(0.0).isNaN())
    }

    @Test
    fun testPolynomialEvaluation() {
        val result = MathParser.compile("x^3 - 3*x")
        assertTrue(result.isSuccess)
        val fn = result.getOrThrow()

        assertEquals(0.0, fn.eval(0.0), 1e-6)
        assertEquals(-2.0, fn.eval(1.0), 1e-6)
        assertEquals(2.0, fn.eval(-1.0), 1e-6)
        assertEquals(2.0, fn.eval(2.0), 1e-6)
    }

    @Test
    fun testImplicitMultiplication() {
        val result = MathParser.compile("2x + 3(x + 1)")
        assertTrue(result.isSuccess)
        val fn = result.getOrThrow()

        // at x = 1: 2(1) + 3(2) = 8
        assertEquals(8.0, fn.eval(1.0), 1e-6)
    }

    @Test
    fun testIntersectionSolver() {
        // f(x) = x + 1/x with y = x + 1 (a = 1, m = 1) -> 1/x = 1 -> x = 1, y = 2
        val result = MathParser.compile("x + 1/x")
        val fn = result.getOrThrow()

        val inter = IntersectionSolver.findIntersections(
            fn = fn,
            a = 1.0,
            m = 1.0,
            xMin = -5.0,
            xMax = 5.0
        )

        assertEquals(1, inter.points.size)
        assertEquals(1.0, inter.points[0].x, 0.05)
        assertEquals(2.0, inter.points[0].y, 0.05)
    }
}
