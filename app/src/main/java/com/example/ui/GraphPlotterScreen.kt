package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.GraphViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphPlotterScreen(
    viewModel: GraphViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Maintain text field value with cursor selection
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(state.formulaInput, TextRange(state.formulaInput.length)))
    }

    // Keep textFieldValue in sync when state formula changes externally (e.g. from preset)
    LaunchedEffect(state.formulaInput) {
        if (textFieldValue.text != state.formulaInput) {
            textFieldValue = TextFieldValue(state.formulaInput, TextRange(state.formulaInput.length))
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950),
        topBar = {
            if (!state.isFullscreen) {
                Surface(
                    color = Slate900,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Slate800)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "حاسبة رسم المنحنيات والمناقشة البيانية",
                                    color = MathAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "رسم بياني دقيق مع وسيط المناقشة y = ax + m",
                                    color = Slate400,
                                    fontSize = 11.sp
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilledTonalIconButton(
                                    onClick = { viewModel.setStudiedFunctionsSheetVisible(true) },
                                    modifier = Modifier.size(38.dp).testTag("studied_functions_gallery_button"),
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = if (state.activeStudiedFunction != null) MathAmber.copy(alpha = 0.25f) else Slate800,
                                        contentColor = MathAmber
                                    )
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = "صور العبارات المدروسة", modifier = Modifier.size(20.dp))
                                }

                                FilledTonalIconButton(
                                    onClick = { viewModel.setPresetsDialogVisible(true) },
                                    modifier = Modifier.size(38.dp).testTag("presets_button"),
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = Slate800,
                                        contentColor = MathAmber
                                    )
                                ) {
                                    Icon(Icons.Default.Functions, contentDescription = "نماذج بكالوريا", modifier = Modifier.size(20.dp))
                                }

                                FilledTonalIconButton(
                                    onClick = { viewModel.setTableDialogVisible(true) },
                                    modifier = Modifier.size(38.dp).testTag("table_button"),
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = Slate800,
                                        contentColor = ReferenceBlue
                                    )
                                ) {
                                    Icon(Icons.Default.TableChart, contentDescription = "جدول القيم", modifier = Modifier.size(20.dp))
                                }

                                FilledTonalIconButton(
                                    onClick = { viewModel.toggleKeyboard() },
                                    modifier = Modifier.size(38.dp).testTag("toggle_keyboard_btn"),
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = if (state.isKeyboardVisible) CasioOpBg else Slate800,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(Icons.Default.Keyboard, contentDescription = "لوحة كاسيو", modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = Slate950
    ) { innerPadding ->
        if (state.isFullscreen) {
            // Fullscreen Canvas mode
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                GraphCanvas(
                    compiledFunction = state.compiledFunction,
                    isParametricActive = state.isParametricActive,
                    slopeA = state.slopeA,
                    paramM = state.paramM,
                    intersections = state.intersections.points,
                    xMin = state.xMin,
                    xMax = state.xMax,
                    yMin = state.yMin,
                    yMax = state.yMax,
                    onBoundsChange = { x1, x2, y1, y2 -> viewModel.setBounds(x1, x2, y1, y2) },
                    onResetZoom = { viewModel.resetZoom() },
                    isFullscreen = true,
                    onToggleFullscreen = { viewModel.toggleFullscreen() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // Standard scrollable workstation layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Interactive Graph Canvas (Height adapted for mobile viewing)
                GraphCanvas(
                    compiledFunction = state.compiledFunction,
                    isParametricActive = state.isParametricActive,
                    slopeA = state.slopeA,
                    paramM = state.paramM,
                    intersections = state.intersections.points,
                    xMin = state.xMin,
                    xMax = state.xMax,
                    yMin = state.yMin,
                    yMax = state.yMax,
                    onBoundsChange = { x1, x2, y1, y2 -> viewModel.setBounds(x1, x2, y1, y2) },
                    onResetZoom = { viewModel.resetZoom() },
                    isFullscreen = false,
                    onToggleFullscreen = { viewModel.toggleFullscreen() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )

                // Active Studied Function Picture Banner
                ActiveStudiedFunctionBanner(
                    activeItem = state.activeStudiedFunction,
                    onOpenGallery = { viewModel.setStudiedFunctionsSheetVisible(true) },
                    onViewImage = { item -> viewModel.setViewingImageItem(item) },
                    onRemoveActive = { viewModel.removeActiveStudiedFunction() }
                )

                // Function Formula Input Section
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Slate900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "عبارة الدالة الرئيسية f(x):",
                                color = Slate300,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            Text(
                                text = if (state.isKeyboardVisible) "إخفاء لوحة كاسيو" else "إظهار لوحة كاسيو ⌨️",
                                color = ReferenceBlue,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .clickable { viewModel.toggleKeyboard() }
                                    .padding(4.dp)
                            )
                        }

                        // Formula Input with Prefix
                        OutlinedTextField(
                            value = textFieldValue,
                            onValueChange = { newVal ->
                                textFieldValue = newVal
                                viewModel.onFormulaChange(newVal.text)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("formula_input_field"),
                            leadingIcon = {
                                Text(
                                    text = "f(x) =",
                                    color = MathAmber,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                                )
                            },
                            trailingIcon = {
                                if (textFieldValue.text.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            textFieldValue = TextFieldValue("")
                                            viewModel.clearFormula()
                                        }
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = "مسح", tint = Slate400)
                                    }
                                }
                            },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Slate100,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            placeholder = {
                                Text(
                                    "x + 1/x",
                                    color = Slate600,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MathAmber,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Syntax Error if invalid
                        state.formulaError?.let { err ->
                            Text(
                                text = err,
                                color = DangerRed,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }

                        // Quick Example Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("x + 1/x", "x^3 - 3*x", "(x^2 - 4)/(x - 1)", "sqrt(x + 3)", "ln(x)/x", "abs(x^2 - 4)").forEach { ex ->
                                SuggestionChip(
                                    onClick = {
                                        textFieldValue = TextFieldValue(ex, TextRange(ex.length))
                                        viewModel.onFormulaChange(ex)
                                    },
                                    label = {
                                        Text(
                                            text = ex,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = Slate300
                                        )
                                    },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = Slate950
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                                )
                            }
                        }
                    }
                }

                // Casio Scientific Keyboard (Collapsible)
                AnimatedVisibility(
                    visible = state.isKeyboardVisible,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    CasioKeyboard(
                        onInsert = { sym ->
                            val currentCursor = textFieldValue.selection.end
                            val newCursor = viewModel.insertSymbol(sym, currentCursor)
                            textFieldValue = TextFieldValue(viewModel.uiState.value.formulaInput, TextRange(newCursor))
                        },
                        onDelete = {
                            val currentCursor = textFieldValue.selection.end
                            val newCursor = viewModel.deleteAt(currentCursor)
                            textFieldValue = TextFieldValue(viewModel.uiState.value.formulaInput, TextRange(newCursor))
                        },
                        onClear = {
                            viewModel.clearFormula()
                            textFieldValue = TextFieldValue("")
                        }
                    )
                }

                // Parametric Discussion Section
                ParametricControls(
                    isParametricActive = state.isParametricActive,
                    onToggleParametric = { viewModel.toggleParametric() },
                    slopeA = state.slopeA,
                    onSlopeAChange = { viewModel.setSlopeA(it) },
                    paramM = state.paramM,
                    onParamMChange = { viewModel.setParamM(it) },
                    isPlaying = state.isPlaying,
                    onTogglePlay = { viewModel.togglePlayAnimation() },
                    intersectionResult = state.intersections
                )

                // Manual Domain Bounds Settings Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "مجال المحاور البيانية (X Range):",
                            color = Slate400,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("أدنى قيمة (X Min)", color = Slate400, fontSize = 10.sp)
                                OutlinedTextField(
                                    value = String.format("%.1f", state.xMin),
                                    onValueChange = { input ->
                                        input.toDoubleOrNull()?.let { v ->
                                            if (v < state.xMax) viewModel.setBounds(v, state.xMax, state.yMin, state.yMax)
                                        }
                                    },
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        color = Slate100,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Slate950,
                                        unfocusedContainerColor = Slate950,
                                        focusedBorderColor = Slate600,
                                        unfocusedBorderColor = Slate800
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("أعلى قيمة (X Max)", color = Slate400, fontSize = 10.sp)
                                OutlinedTextField(
                                    value = String.format("%.1f", state.xMax),
                                    onValueChange = { input ->
                                        input.toDoubleOrNull()?.let { v ->
                                            if (v > state.xMin) viewModel.setBounds(state.xMin, v, state.yMin, state.yMax)
                                        }
                                    },
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        color = Slate100,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Slate950,
                                        unfocusedContainerColor = Slate950,
                                        focusedBorderColor = Slate600,
                                        unfocusedBorderColor = Slate800
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                }

                // Footer spacer for edge-to-edge system navigation bars
                Spacer(Modifier.navigationBarsPadding().height(16.dp))
            }
        }
    }

    // Presets Dialog
    if (state.showPresetsDialog) {
        PresetsDialog(
            onSelectPreset = { preset ->
                viewModel.loadPreset(preset)
                textFieldValue = TextFieldValue(preset.formula, TextRange(preset.formula.length))
            },
            onDismiss = { viewModel.setPresetsDialogVisible(false) }
        )
    }

    // Function Table Dialog
    if (state.showTableDialog) {
        FunctionTableSheet(
            compiledFunction = state.compiledFunction,
            xMin = state.xMin,
            xMax = state.xMax,
            onDismiss = { viewModel.setTableDialogVisible(false) }
        )
    }

    // Studied Functions Gallery & Management Sheet
    if (state.showStudiedFunctionsSheet) {
        StudiedFunctionsSheet(
            items = state.studiedFunctions,
            activeItemId = state.activeStudiedFunction?.id,
            onSelectItem = { item ->
                viewModel.selectStudiedFunction(item)
                textFieldValue = TextFieldValue(item.formula, TextRange(item.formula.length))
            },
            onAddNewItem = { title, formula, notes, uri ->
                viewModel.addStudiedFunction(title, formula, notes, uri)
                textFieldValue = TextFieldValue(formula, TextRange(formula.length))
            },
            onDeleteItem = { id -> viewModel.deleteStudiedFunction(id) },
            onViewImage = { item -> viewModel.setViewingImageItem(item) },
            onDismiss = { viewModel.setStudiedFunctionsSheetVisible(false) }
        )
    }

    // Full Image / Problem Detail Viewer Dialog
    state.viewingImageItem?.let { item ->
        FullImageViewerDialog(
            item = item,
            onApplyFormula = { formula ->
                viewModel.onFormulaChange(formula)
                textFieldValue = TextFieldValue(formula, TextRange(formula.length))
            },
            onDismiss = { viewModel.setViewingImageItem(null) }
        )
    }
}
