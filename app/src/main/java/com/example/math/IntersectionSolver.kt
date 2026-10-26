package com.example.math

import kotlin.math.abs

data class Point2D(val x: Double, val y: Double)

data class IntersectionResult(
    val points: List<Point2D>,
    val solutionSummaryAr: String,
    val detailsAr: String
)

object IntersectionSolver {

    /**
     * Finds intersections of f(x) with line y = ax + m in [xMin, xMax].
     */
    fun findIntersections(
        fn: CompiledFunction,
        a: Double,
        m: Double,
        xMin: Double,
        xMax: Double,
        steps: Int = 1000
    ): IntersectionResult {
        val points = mutableListOf<Point2D>()
        val step = (xMax - xMin) / steps.toDouble()

        var prevX = xMin
        var prevDiff = evaluateDiff(fn, a, m, prevX)

        for (i in 1..steps) {
            val currX = xMin + i * step
            val currDiff = evaluateDiff(fn, a, m, currX)

            if (prevDiff != null && currDiff != null && !prevDiff.isNaN() && !currDiff.isNaN()) {
                // Sign change detected
                if ((prevDiff <= 0.0 && currDiff >= 0.0) || (prevDiff >= 0.0 && currDiff <= 0.0)) {
                    // Refine root using bisection
                    val rootX = refineRoot(fn, a, m, prevX, currX, prevDiff, currDiff)
                    if (rootX != null) {
                        val rootY = a * rootX + m
                        val verifyFx = fn.eval(rootX)
                        // Verify that this is a true root and not an asymptote jump
                        if (!verifyFx.isNaN() && abs(verifyFx - rootY) < 0.05) {
                            // Avoid duplicate roots within tolerance
                            val isDuplicate = points.any { abs(it.x - rootX) < step * 1.2 }
                            if (!isDuplicate) {
                                points.add(Point2D(rootX, rootY))
                            }
                        }
                    }
                }
            }

            prevX = currX
            prevDiff = currDiff
        }

        val count = points.size
        val summaryAr = when (count) {
            0 -> "لا توجد حلول (0 نقطة تقاطع)"
            1 -> "حل وحيد (نقطة تقاطع واحدة)"
            2 -> "حلان متمايزان (نقطتا تقاطع)"
            else -> "$count حلول متمايزة ($count نقاط تقاطع)"
        }

        val mFormatted = if (m >= 0) "+${String.format("%.2f", m)}" else String.format("%.2f", m)
        val detailsAr = when (count) {
            0 -> "المستقيم y = ${formatLineEq(a, m)} لا يقطع منحنى الدالة في هذا المجال."
            1 -> "المستقيم y = ${formatLineEq(a, m)} يقطع المنحنى في نقطة واحدة عند m = $mFormatted."
            2 -> "المستقيم يقطع المنحنى مرتين عند الفاصلتين:\n" +
                    points.mapIndexed { idx, p -> "X${idx + 1} = ${String.format("%.2f", p.x)}" }.joinToString(" ، ")
            else -> "يوجد $count تقاطعات للمستقيم مع المنحنى عند m = $mFormatted."
        }

        return IntersectionResult(points, summaryAr, detailsAr)
    }

    private fun evaluateDiff(fn: CompiledFunction, a: Double, m: Double, x: Double): Double? {
        val fx = fn.eval(x)
        if (fx.isNaN() || fx.isInfinite()) return null
        val lineY = a * x + m
        return fx - lineY
    }

    private fun refineRoot(
        fn: CompiledFunction,
        a: Double,
        m: Double,
        x1: Double,
        x2: Double,
        d1: Double,
        d2: Double
    ): Double? {
        var low = x1
        var high = x2
        var fLow = d1
        var fHigh = d2

        // If across a vertical asymptote where values explode in opposite directions (e.g. +1000 and -1000)
        if (abs(fLow - fHigh) > 20.0) {
            // Check middle point
            val mid = (low + high) / 2.0
            val fMid = evaluateDiff(fn, a, m, mid) ?: return null
            if (abs(fMid) > 10.0) return null // likely a pole/asymptote
        }

        // Bisection for 24 iterations (gives accuracy < 1e-7)
        for (iter in 0..24) {
            val mid = (low + high) / 2.0
            val fMid = evaluateDiff(fn, a, m, mid) ?: return null

            if (abs(fMid) < 1e-9) return mid

            if ((fLow <= 0 && fMid >= 0) || (fLow >= 0 && fMid <= 0)) {
                high = mid
                fHigh = fMid
            } else {
                low = mid
                fLow = fMid
            }
        }
        return (low + high) / 2.0
    }

    fun formatLineEq(a: Double, m: Double): String {
        val aPart = when {
            abs(a) < 1e-5 -> ""
            abs(a - 1.0) < 1e-5 -> "x"
            abs(a + 1.0) < 1e-5 -> "-x"
            else -> "${String.format("%.2f", a)}x"
        }
        val mPart = when {
            abs(m) < 1e-5 -> if (aPart.isEmpty()) "0" else ""
            m > 0 -> if (aPart.isNotEmpty()) " + ${String.format("%.2f", m)}" else String.format("%.2f", m)
            else -> if (aPart.isNotEmpty()) " - ${String.format("%.2f", abs(m))}" else "-${String.format("%.2f", abs(m))}"
        }
        return (aPart + mPart).trim()
    }

    /**
     * Finds local extrema (peaks and valleys) of f(x) in [xMin, xMax].
     */
    fun findLocalExtrema(fn: CompiledFunction, xMin: Double, xMax: Double, steps: Int = 300): List<Pair<Point2D, String>> {
        val result = mutableListOf<Pair<Point2D, String>>()
        val step = (xMax - xMin) / steps
        var prevX = xMin
        var prevY = fn.eval(prevX)
        var prevSlope = Double.NaN

        for (i in 1..steps) {
            val currX = xMin + i * step
            val currY = fn.eval(currX)
            if (!prevY.isNaN() && !currY.isNaN()) {
                val slope = (currY - prevY) / step
                if (!prevSlope.isNaN()) {
                    if (prevSlope > 0 && slope < 0) {
                        result.add(Point2D(prevX, prevY) to "قيمة حدية عظمى")
                    } else if (prevSlope < 0 && slope > 0) {
                        result.add(Point2D(prevX, prevY) to "قيمة حدية صغرى")
                    }
                }
                prevSlope = slope
            } else {
                prevSlope = Double.NaN
            }
            prevX = currX
            prevY = currY
        }
        return result
    }
}
