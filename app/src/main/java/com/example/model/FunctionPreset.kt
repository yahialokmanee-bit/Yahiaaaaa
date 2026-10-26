package com.example.model

data class FunctionPreset(
    val titleAr: String,
    val formula: String,
    val category: FunctionCategory,
    val recommendedSlopeA: Double,
    val descriptionAr: String,
    val defaultXMin: Double = -5.0,
    val defaultXMax: Double = 5.0,
    val defaultYMin: Double = -5.0,
    val defaultYMax: Double = 5.0
)

enum class FunctionCategory(val titleAr: String) {
    RATIONAL("دوال كسرية وتناظرية"),
    POLYNOMIAL("كثيرات الحدود"),
    EXPONENTIAL_LOG("دوال أسية ولوغاريتمية"),
    ROOTS_ABS("جذور وقيمة مطلقة"),
    TRIGONOMETRIC("دوال مثلثية")
}

object PresetRepository {
    val presets: List<FunctionPreset> = listOf(
        // Rational Functions
        FunctionPreset(
            titleAr = "دالة مقلوب كسرية شهيرة",
            formula = "x + 1/x",
            category = FunctionCategory.RATIONAL,
            recommendedSlopeA = 1.0,
            descriptionAr = "تحتوي على مستقيم مقارب مائل y = x ومستقيم مقارب عمودي x = 0. المناقشة المائلة عند a = 1 تُظهر قيم m التي لا تقطع فيها الدالة المنحنى (بين -2 و 2)."
        ),
        FunctionPreset(
            titleAr = "دالة كسرية مع مقارب مائل",
            formula = "(x^2 - 4) / (x - 1)",
            category = FunctionCategory.RATIONAL,
            recommendedSlopeA = 1.0,
            descriptionAr = "دالة ناطقة شهيرة في امتحانات البكالوريا، مستقيم مقارب عمودي x = 1 ومستقيم مقارب مائل y = x + 1."
        ),
        FunctionPreset(
            titleAr = "دالة تناظرية (Homographic)",
            formula = "(2*x + 1) / (x - 1)",
            category = FunctionCategory.RATIONAL,
            recommendedSlopeA = 0.0,
            descriptionAr = "دالة تناظرية مركز تناظرها (1, 2) ومستقيم مقارب أفقي y = 2 ومستقيم عمودي x = 1."
        ),
        FunctionPreset(
            titleAr = "دالة كسرية فردية",
            formula = "(x^2 - 1) / x",
            category = FunctionCategory.RATIONAL,
            recommendedSlopeA = 1.0,
            descriptionAr = "دالة فردية متناظرة بالنسبة للمبدأ، مستقيمها المقارب المائل y = x ومستقيمها العمودي x = 0."
        ),

        // Polynomials
        FunctionPreset(
            titleAr = "كثيرة حدود درجة ثالثة",
            formula = "x^3 - 3*x",
            category = FunctionCategory.POLYNOMIAL,
            recommendedSlopeA = 0.0,
            descriptionAr = "دالة تكعيبية تمتلك قيمتين حديتين عند x = -1 (قيمة عظمى y = 2) وعند x = 1 (قيمة صغرى y = -2). المناقشة الأفقية m تكشف 1، 2 أو 3 حلول."
        ),
        FunctionPreset(
            titleAr = "دالة تكعيبية غير متناظرة",
            formula = "x^3 - 3*x^2 + 2",
            category = FunctionCategory.POLYNOMIAL,
            recommendedSlopeA = 0.0,
            descriptionAr = "دالة بكالوريا كلاسيكية مع نقطة انعطاف وقيم حدية محلية."
        ),
        FunctionPreset(
            titleAr = "دالة ثنائية التربيع (درجة 4)",
            formula = "x^4 - 2*x^2",
            category = FunctionCategory.POLYNOMIAL,
            recommendedSlopeA = 0.0,
            descriptionAr = "دالة زوجية على شكل حرف W، تمتلك قيمة عظمى عند (0, 0) وقيمتين صغريين عند x = ±1."
        ),

        // Exponential & Logarithmic
        FunctionPreset(
            titleAr = "دالة أسية كلاسيكية",
            formula = "x * exp(-x)",
            category = FunctionCategory.EXPONENTIAL_LOG,
            recommendedSlopeA = 0.0,
            descriptionAr = "تقبل مستقيم مقارب أفقي y = 0 عند +∞ وقيمة حدية عظمى عند x = 1 حيث y = 1/e ≈ 0.37."
        ),
        FunctionPreset(
            titleAr = "دالة لوغاريتمية شهيرة",
            formula = "ln(x) / x",
            category = FunctionCategory.EXPONENTIAL_LOG,
            recommendedSlopeA = 0.0,
            descriptionAr = "معرفة على ]0, +∞[، مستقيم مقارب عمودي x = 0 ومقارب أفقي y = 0، قيمة حدية عظمى عند x = e.",
            defaultXMin = 0.1,
            defaultXMax = 10.0
        ),
        FunctionPreset(
            titleAr = "الفرق بين الأسية والمستقيم",
            formula = "exp(x) - x - 1",
            category = FunctionCategory.EXPONENTIAL_LOG,
            recommendedSlopeA = 0.0,
            descriptionAr = "دالة موجبة دوماً تقبل قيمة حدية دنيا (0, 0)، تُستعمل لإثبات المتباينة e^x ≥ x + 1."
        ),

        // Roots and Absolute value
        FunctionPreset(
            titleAr = "دالة الجذر التربيعي",
            formula = "sqrt(x + 3)",
            category = FunctionCategory.ROOTS_ABS,
            recommendedSlopeA = 0.5,
            descriptionAr = "معرفة على [-3, +∞[، نصف قطع مكافئ أفقي يبدأ من النقطة (-3, 0)."
        ),
        FunctionPreset(
            titleAr = "قيمة مطلقة لكثيرة حدود",
            formula = "abs(x^2 - 4)",
            category = FunctionCategory.ROOTS_ABS,
            recommendedSlopeA = 0.0,
            descriptionAr = "دالة تنعكس أجزاؤها السالبة للأعلى، ممتازة لدراسة نقاط الزاوية (non-differentiable) والمناقشة البيانية."
        ),

        // Trigonometric
        FunctionPreset(
            titleAr = "مجموع دوال جيبية",
            formula = "sin(x) + cos(2*x)",
            category = FunctionCategory.TRIGONOMETRIC,
            recommendedSlopeA = 0.0,
            descriptionAr = "دالة دورية تتكرر كل 2π، تظهر تراكب الموجات والنقاط الحرجة المتعددة."
        ),
        FunctionPreset(
            titleAr = "دالة الجيب المخمد (Sinc)",
            formula = "sin(2*x) / x",
            category = FunctionCategory.TRIGONOMETRIC,
            recommendedSlopeA = 0.0,
            descriptionAr = "دالة شهيرة في معالجة الإشارات، نهايتها عند الصفر تساوي 2."
        )
    )
}
