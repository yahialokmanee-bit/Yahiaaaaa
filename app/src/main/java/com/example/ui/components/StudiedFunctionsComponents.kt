package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.SampleImageType
import com.example.model.StudiedFunctionItem
import com.example.ui.theme.*

/**
 * Visual thumbnail representing either a real photo picked by the user
 * or an illustrated mathematical exercise card.
 */
@Composable
fun StudiedFunctionThumbnail(
    item: StudiedFunctionItem,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Slate950)
            .border(1.dp, Slate800, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (!item.imageUri.isNullOrEmpty()) {
            AsyncImage(
                model = Uri.parse(item.imageUri),
                contentDescription = "صورة العبارة المدروسة",
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
            )
        } else {
            // Illustrated Math Problem Card for built-in samples
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                when (item.sampleType) {
                                    SampleImageType.RATIONAL_FRACTION -> Color(0xFF1E293B)
                                    SampleImageType.LOGARITHMIC -> Color(0xFF172554)
                                    SampleImageType.EXPONENTIAL -> Color(0xFF311042)
                                    SampleImageType.INVERSE_SYMMETRIC -> Color(0xFF064E3B)
                                    null -> Slate900
                                },
                                Slate950
                            )
                        )
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = when (item.sampleType) {
                            SampleImageType.RATIONAL_FRACTION -> Icons.Default.LinearScale
                            SampleImageType.LOGARITHMIC -> Icons.Default.Timeline
                            SampleImageType.EXPONENTIAL -> Icons.Default.TrendingUp
                            SampleImageType.INVERSE_SYMMETRIC -> Icons.Default.AllInclusive
                            null -> Icons.Default.Image
                        },
                        contentDescription = null,
                        tint = MathAmber,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.formula,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "عبارة مدروسة",
                        color = Slate400,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

/**
 * Banner shown on the main screen to display the active studied function photo
 * right above or with the formula input field.
 */
@Composable
fun ActiveStudiedFunctionBanner(
    activeItem: StudiedFunctionItem?,
    onOpenGallery: () -> Unit,
    onViewImage: (StudiedFunctionItem) -> Unit,
    onRemoveActive: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_studied_function_banner"),
        shape = RoundedCornerShape(16.dp),
        color = Slate900,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (activeItem != null) MathAmber.copy(alpha = 0.6f) else Slate800)
    ) {
        if (activeItem != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MathAmber.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MathAmber)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Collections,
                                contentDescription = null,
                                tint = MathAmber,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(4.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "العبارة الدالة المدروسة المرفقة:",
                                color = MathAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = activeItem.title,
                                color = Slate200,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { onViewImage(activeItem) },
                            modifier = Modifier.size(32.dp).testTag("btn_zoom_active_image")
                        ) {
                            Icon(
                                Icons.Default.ZoomIn,
                                contentDescription = "تكبير الصورة",
                                tint = ReferenceBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = onOpenGallery,
                            modifier = Modifier.size(32.dp).testTag("btn_switch_studied_image")
                        ) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                contentDescription = "تغيير الصورة",
                                tint = Slate300,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = onRemoveActive,
                            modifier = Modifier.size(32.dp).testTag("btn_remove_active_image")
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "إلغاء الإرفاق",
                                tint = Slate400,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Thumbnail + notes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewImage(activeItem) },
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StudiedFunctionThumbnail(
                        item = activeItem,
                        modifier = Modifier
                            .size(width = 80.dp, height = 55.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "f(x) = ${activeItem.formula}",
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (activeItem.notes.isNotEmpty()) {
                            Text(
                                text = activeItem.notes,
                                color = Slate400,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        } else {
            // Empty state banner inviting the user to place/attach a studied function photo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenGallery() }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Slate800,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = MathAmber,
                            modifier = Modifier
                                .size(36.dp)
                                .padding(8.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "صور العبارات الدالة المدروسة",
                            color = Slate200,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "ضع أو التقط صور المسائل والعبارات لرسمها ومطابقتها",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onOpenGallery,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Slate800,
                        contentColor = MathAmber
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_attach_studied_expression")
                ) {
                    Text("إرفاق صورة 📸", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Bottom Sheet / Dialog managing the gallery of studied function pictures:
 * - Adding from Photo Picker
 * - Editing formula and title
 * - Applying to the main graph
 * - Viewing full image
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudiedFunctionsSheet(
    items: List<StudiedFunctionItem>,
    activeItemId: String?,
    onSelectItem: (StudiedFunctionItem) -> Unit,
    onAddNewItem: (title: String, formula: String, notes: String, imageUri: String?) -> Unit,
    onDeleteItem: (String) -> Unit,
    onViewImage: (StudiedFunctionItem) -> Unit,
    onDismiss: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Slate600) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "صور العبارات الدالة المدروسة",
                        color = MathAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "أضف صور مسائل الدوال من كراسك أو كتابك لتطبيقها ومناقشتها بيانيا",
                        color = Slate400,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MathAmber,
                        contentColor = Slate950
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_add_new_function_image")
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("إضافة صورة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            HorizontalDivider(color = Slate800)

            // Items List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    val isActive = item.id == activeItemId

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isActive) 1.5.dp else 1.dp,
                                color = if (isActive) MathAmber else Slate800,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        color = if (isActive) Slate950 else Slate900
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Clickable Thumbnail for zoom
                                    Box(
                                        modifier = Modifier
                                            .size(width = 84.dp, height = 64.dp)
                                            .clickable { onViewImage(item) }
                                    ) {
                                        StudiedFunctionThumbnail(
                                            item = item,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        // Zoom hint icon overlay
                                        Surface(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(2.dp),
                                            shape = CircleShape,
                                            color = Slate950.copy(alpha = 0.7f)
                                        ) {
                                            Icon(
                                                Icons.Default.ZoomIn,
                                                contentDescription = "تكبير",
                                                tint = ReferenceBlue,
                                                modifier = Modifier.size(16.dp).padding(2.dp)
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = item.title,
                                            color = Slate100,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Surface(
                                            color = Slate950,
                                            shape = RoundedCornerShape(6.dp),
                                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Slate800)
                                        ) {
                                            Text(
                                                text = "f(x) = ${item.formula}",
                                                color = MathAmber,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        if (item.notes.isNotEmpty()) {
                                            Text(
                                                text = item.notes,
                                                color = Slate400,
                                                fontSize = 11.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }

                                if (item.imageUri != null) {
                                    IconButton(
                                        onClick = { onDeleteItem(item.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "حذف الصورة",
                                            tint = Slate500,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onSelectItem(item)
                                        onDismiss()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isActive) EmeraldGreen else Slate800,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 6.dp)
                                ) {
                                    Icon(
                                        if (isActive) Icons.Default.Check else Icons.Default.ShowChart,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = if (isActive) "مُفعلة حالياً في المنحنى" else "رسم هذه العبارة بيانيا",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = { onViewImage(item) },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = ReferenceBlue
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Fullscreen, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("معاينة", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddNewStudiedFunctionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, formula, notes, uri ->
                onAddNewItem(title, formula, notes, uri)
                showAddDialog = false
            }
        )
    }
}

/**
 * Dialog to add a new studied function image via Photo Picker or manual entry
 */
@Composable
fun AddNewStudiedFunctionDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, formula: String, notes: String, imageUri: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var formula by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Android Modern Zero-Permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            color = Slate900,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "إضافة صورة عبارة دالة مدروسة 📸",
                    color = MathAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "اختر صورة للمسألة أو العبارة الرياضية من معرض الصور، ثم اكتب عبارتها f(x) لرسمها ومطابقتها:",
                    color = Slate300,
                    fontSize = 11.sp
                )

                // Image picker preview box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate950)
                        .border(1.dp, if (selectedImageUri != null) MathAmber else Slate800, RoundedCornerShape(12.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "الصورة المختارة",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Button to change
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Slate900.copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Slate700)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = ReferenceBlue, modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("تغيير الصورة", color = Slate200, fontSize = 10.sp)
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = MathAmber,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "انقر هنا لاختيار صورة العبارة / المسألة",
                                color = Slate300,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "(من ألبوم الصور أو التقاطها بالكاميرا)",
                                color = Slate500,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Title input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان المسألة / التمرين", fontSize = 12.sp) },
                    placeholder = { Text("مثال: تمرين بكالوريا دالة لوغاريتمية", color = Slate600) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate950,
                        unfocusedContainerColor = Slate950,
                        focusedBorderColor = MathAmber,
                        unfocusedBorderColor = Slate700,
                        focusedLabelColor = MathAmber,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Formula input
                OutlinedTextField(
                    value = formula,
                    onValueChange = { formula = it },
                    label = { Text("عبارة الدالة f(x)", fontSize = 12.sp) },
                    placeholder = { Text("مثال: (x^2 - 1)/x أو ln(x)/x", color = Slate600) },
                    leadingIcon = {
                        Text(
                            text = "f(x) =",
                            color = MathAmber,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate950,
                        unfocusedContainerColor = Slate950,
                        focusedBorderColor = MathAmber,
                        unfocusedBorderColor = Slate700,
                        focusedLabelColor = MathAmber,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Notes input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات أو أسئلة المسألة (اختياري)", fontSize = 12.sp) },
                    placeholder = { Text("مثال: مناقشة بيانية مائلة للمستقيم y = x + m", color = Slate600) },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate950,
                        unfocusedContainerColor = Slate950,
                        focusedBorderColor = MathAmber,
                        unfocusedBorderColor = Slate700,
                        focusedLabelColor = MathAmber,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                errorMessage?.let {
                    Text(text = it, color = DangerRed, fontSize = 11.sp)
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColors(contentColor = Slate400)
                    ) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            if (formula.isBlank()) {
                                errorMessage = "يرجى كتابة عبارة الدالة f(x)!"
                            } else {
                                val finalTitle = if (title.isBlank()) "عبارة دالة مدروسة" else title
                                onConfirm(finalTitle, formula.trim(), notes.trim(), selectedImageUri?.toString())
                            }
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = MathAmber, contentColor = Slate950),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("حفظ وإضافة", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Fullscreen / High resolution dialog to examine the studied function image in detail
 */
@Composable
fun FullImageViewerDialog(
    item: StudiedFunctionItem,
    onApplyFormula: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            color = Color.Black.copy(alpha = 0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "f(x) = ${item.formula}",
                            color = MathAmber,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Slate800, CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                // Middle Image Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!item.imageUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = Uri.parse(item.imageUri),
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // Illustrated math card presentation
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(260.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Slate900,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, MathAmber)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = MathAmber,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = item.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "f(x) = ${item.formula}",
                                    color = MathAmber,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                if (item.notes.isNotEmpty()) {
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        text = item.notes,
                                        color = Slate300,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (item.notes.isNotEmpty() && !item.imageUri.isNullOrEmpty()) {
                        Surface(
                            color = Slate900,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Slate800)
                        ) {
                            Text(
                                text = "ملاحظات المسألة: ${item.notes}",
                                color = Slate300,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onApplyFormula(item.formula)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MathAmber,
                            contentColor = Slate950
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("رسم هذه العبارة فوراً في المنحنى 📈", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
