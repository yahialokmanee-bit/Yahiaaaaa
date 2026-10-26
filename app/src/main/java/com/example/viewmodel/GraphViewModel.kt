package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.math.CompiledFunction
import com.example.math.IntersectionResult
import com.example.math.IntersectionSolver
import com.example.math.MathParser
import com.example.model.FunctionPreset
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

import com.example.model.DefaultStudiedFunctions
import com.example.model.StudiedFunctionItem
import java.util.UUID

data class GraphUiState(
    val formulaInput: String = "x + 1/x",
    val compiledFunction: CompiledFunction? = null,
    val formulaError: String? = null,
    val isParametricActive: Boolean = false,
    val slopeA: Double = 1.0,
    val paramM: Double = 1.0,
    val isPlaying: Boolean = false,
    val xMin: Double = -5.0,
    val xMax: Double = 5.0,
    val yMin: Double = -5.0,
    val yMax: Double = 5.0,
    val intersections: IntersectionResult = IntersectionResult(emptyList(), "المناقشة غير مفعلة", ""),
    val isKeyboardVisible: Boolean = true,
    val showPresetsDialog: Boolean = false,
    val showTableDialog: Boolean = false,
    val isFullscreen: Boolean = false,
    val studiedFunctions: List<StudiedFunctionItem> = DefaultStudiedFunctions.sampleItems,
    val activeStudiedFunction: StudiedFunctionItem? = DefaultStudiedFunctions.sampleItems.firstOrNull(),
    val showStudiedFunctionsSheet: Boolean = false,
    val viewingImageItem: StudiedFunctionItem? = null
)

class GraphViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GraphUiState())
    val uiState: StateFlow<GraphUiState> = _uiState.asStateFlow()

    private var animationJob: Job? = null
    private var animationDirection = 1.0

    init {
        compileFormula(_uiState.value.formulaInput)
    }

    fun onFormulaChange(newFormula: String) {
        _uiState.update { it.copy(formulaInput = newFormula) }
        compileFormula(newFormula)
    }

    fun insertSymbol(sym: String, cursor: Int): Int {
        val curr = _uiState.value.formulaInput
        val safeCursor = cursor.coerceIn(0, curr.length)
        val updated = curr.substring(0, safeCursor) + sym + curr.substring(safeCursor)
        onFormulaChange(updated)
        return safeCursor + sym.length
    }

    fun deleteAt(cursor: Int): Int {
        val curr = _uiState.value.formulaInput
        if (curr.isEmpty() || cursor <= 0) return 0
        val safeCursor = cursor.coerceIn(1, curr.length)
        val updated = curr.substring(0, safeCursor - 1) + curr.substring(safeCursor)
        onFormulaChange(updated)
        return safeCursor - 1
    }

    fun clearFormula() {
        _uiState.update {
            it.copy(
                formulaInput = "",
                compiledFunction = null,
                formulaError = null,
                intersections = IntersectionResult(emptyList(), "لا توجد دالة", "")
            )
        }
    }

    fun toggleParametric() {
        val nextActive = !_uiState.value.isParametricActive
        if (!nextActive && _uiState.value.isPlaying) {
            stopAnimation()
        }
        _uiState.update { it.copy(isParametricActive = nextActive) }
        recalculateIntersections()
    }

    fun setSlopeA(newA: Double) {
        _uiState.update { it.copy(slopeA = newA) }
        recalculateIntersections()
    }

    fun setParamM(newM: Double) {
        _uiState.update { it.copy(paramM = newM) }
        recalculateIntersections()
    }

    fun togglePlayAnimation() {
        if (_uiState.value.isPlaying) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }

    private fun startAnimation() {
        _uiState.update { it.copy(isPlaying = true) }
        animationJob?.cancel()
        animationJob = viewModelScope.launch {
            while (isActive && _uiState.value.isPlaying && _uiState.value.isParametricActive) {
                delay(30)
                var currentM = _uiState.value.paramM + 0.05 * animationDirection
                if (currentM >= 5.0) {
                    currentM = 5.0
                    animationDirection = -1.0
                } else if (currentM <= -5.0) {
                    currentM = -5.0
                    animationDirection = 1.0
                }
                _uiState.update { it.copy(paramM = currentM) }
                recalculateIntersections()
            }
        }
    }

    private fun stopAnimation() {
        animationJob?.cancel()
        animationJob = null
        _uiState.update { it.copy(isPlaying = false) }
    }

    fun setBounds(xMin: Double, xMax: Double, yMin: Double, yMax: Double) {
        _uiState.update {
            it.copy(
                xMin = xMin,
                xMax = xMax,
                yMin = yMin,
                yMax = yMax
            )
        }
        recalculateIntersections()
    }

    fun resetZoom() {
        _uiState.update {
            it.copy(
                xMin = -5.0,
                xMax = 5.0,
                yMin = -5.0,
                yMax = 5.0
            )
        }
        recalculateIntersections()
    }

    fun toggleKeyboard() {
        _uiState.update { it.copy(isKeyboardVisible = !it.isKeyboardVisible) }
    }

    fun setPresetsDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showPresetsDialog = visible) }
    }

    fun setTableDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showTableDialog = visible) }
    }

    fun toggleFullscreen() {
        _uiState.update { it.copy(isFullscreen = !it.isFullscreen) }
    }

    fun setStudiedFunctionsSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showStudiedFunctionsSheet = visible) }
    }

    fun setViewingImageItem(item: StudiedFunctionItem?) {
        _uiState.update { it.copy(viewingImageItem = item) }
    }

    fun addStudiedFunction(title: String, formula: String, notes: String, imageUri: String?) {
        val newItem = StudiedFunctionItem(
            id = UUID.randomUUID().toString(),
            title = title,
            formula = formula,
            imageUri = imageUri,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        _uiState.update { state ->
            val updatedList = listOf(newItem) + state.studiedFunctions
            state.copy(
                studiedFunctions = updatedList,
                activeStudiedFunction = newItem,
                formulaInput = formula
            )
        }
        compileFormula(formula)
    }

    fun deleteStudiedFunction(id: String) {
        _uiState.update { state ->
            val updated = state.studiedFunctions.filterNot { it.id == id }
            val newActive = if (state.activeStudiedFunction?.id == id) updated.firstOrNull() else state.activeStudiedFunction
            state.copy(
                studiedFunctions = updated,
                activeStudiedFunction = newActive
            )
        }
    }

    fun selectStudiedFunction(item: StudiedFunctionItem) {
        _uiState.update {
            it.copy(
                activeStudiedFunction = item,
                formulaInput = item.formula,
                slopeA = item.recommendedSlopeA,
                xMin = item.defaultXMin,
                xMax = item.defaultXMax
            )
        }
        compileFormula(item.formula)
    }

    fun removeActiveStudiedFunction() {
        _uiState.update { it.copy(activeStudiedFunction = null) }
    }

    fun loadPreset(preset: FunctionPreset) {
        _uiState.update {
            it.copy(
                formulaInput = preset.formula,
                slopeA = preset.recommendedSlopeA,
                xMin = preset.defaultXMin,
                xMax = preset.defaultXMax,
                yMin = preset.defaultYMin,
                yMax = preset.defaultYMax
            )
        }
        compileFormula(preset.formula)
    }

    private fun compileFormula(expr: String) {
        if (expr.isBlank()) {
            _uiState.update {
                it.copy(
                    compiledFunction = null,
                    formulaError = null,
                    intersections = IntersectionResult(emptyList(), "لا توجد دالة", "")
                )
            }
            return
        }

        val result = MathParser.compile(expr)
        result.fold(
            onSuccess = { compiled ->
                _uiState.update {
                    it.copy(
                        compiledFunction = compiled,
                        formulaError = null
                    )
                }
                recalculateIntersections()
            },
            onFailure = { err ->
                _uiState.update {
                    it.copy(
                        compiledFunction = null,
                        formulaError = "صيغة رياضية غير صحيحة! تأكد من الأقواس والمتغيرات."
                    )
                }
            }
        )
    }

    private fun recalculateIntersections() {
        val s = _uiState.value
        val fn = s.compiledFunction
        if (fn != null && s.isParametricActive) {
            val res = IntersectionSolver.findIntersections(
                fn = fn,
                a = s.slopeA,
                m = s.paramM,
                xMin = s.xMin,
                xMax = s.xMax
            )
            _uiState.update { it.copy(intersections = res) }
        } else {
            _uiState.update {
                it.copy(
                    intersections = IntersectionResult(
                        emptyList(),
                        if (!s.isParametricActive) "المناقشة غير مفعلة" else "لا توجد دالة",
                        ""
                    )
                )
            }
        }
    }
}
