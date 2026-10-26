package com.example.model

enum class SampleImageType {
    RATIONAL_FRACTION,
    LOGARITHMIC,
    EXPONENTIAL,
    INVERSE_SYMMETRIC
}

data class StudiedFunctionItem(
    val id: String,
    val title: String,
    val formula: String,
    val imageUri: String? = null,
    val sampleType: SampleImageType? = null,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val recommendedSlopeA: Double = 1.0,
    val defaultXMin: Double = -5.0,
    val defaultXMax: Double = 5.0
)

object DefaultStudiedFunctions {
    val sampleItems = listOf(
        StudiedFunctionItem(
            id = "sample_1",
            title = "مسألة بكالوريا: دالة كسرية مع مقارب مائل",
            formula = "(x^2 - 4) / (x - 1)",
            sampleType = SampleImageType.RATIONAL_FRACTION,
            notes = "المطلوب: دراسة التغيرات، تعيين المقارب المائل y = x + 1، ومناقشة بيانية لعدد حلول f(x) = x + m.",
            recommendedSlopeA = 1.0,
            defaultXMin = -6.0,
            defaultXMax = 6.0
        ),
        StudiedFunctionItem(
            id = "sample_2",
            title = "مسألة الدالة اللوغاريتمية f(x) = ln(x)/x",
            formula = "ln(x) / x",
            sampleType = SampleImageType.LOGARITHMIC,
            notes = "معرفة على ]0, +∞[، مستقيم مقارب عمودي x = 0 ومقارب أفقي y = 0. مناقشة أفقية f(x) = m.",
            recommendedSlopeA = 0.0,
            defaultXMin = 0.1,
            defaultXMax = 8.0
        ),
        StudiedFunctionItem(
            id = "sample_3",
            title = "مسألة دالة المقلوب التناظرية f(x) = x + 1/x",
            formula = "x + 1/x",
            sampleType = SampleImageType.INVERSE_SYMMETRIC,
            notes = "دالة فردية، مستقيم مقارب مائل y = x، ومناقشة بيانية مائلة عند a = 1 تكشف عدم وجود حلول لـ m بين -2 و 2.",
            recommendedSlopeA = 1.0,
            defaultXMin = -5.0,
            defaultXMax = 5.0
        ),
        StudiedFunctionItem(
            id = "sample_4",
            title = "مسألة الدالة الأسية f(x) = x * exp(-x)",
            formula = "x * exp(-x)",
            sampleType = SampleImageType.EXPONENTIAL,
            notes = "تقبل مقارب أفقي y = 0 وقيمة حدية عظمى عند x = 1. مناقشة أفقية لعدد نقط التقاطع مع المستقيم y = m.",
            recommendedSlopeA = 0.0,
            defaultXMin = -2.0,
            defaultXMax = 6.0
        )
    )
}
