package com.example.gestorjuridico.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestorjuridico.model.*
import com.example.gestorjuridico.ui.components.CategoryBadge
import com.example.gestorjuridico.ui.components.DetailTopBar
import com.example.gestorjuridico.ui.theme.*
import kotlin.math.hypot

@Composable
fun ProcessDetailScreen(
    processState: UiState<LegalProcess>,
    initialTab: Int = 0,
    onBackClick: () -> Unit,
    onAddProcessToHome: (String) -> Unit
) {
    // 0 = Actuaciones, 1 = Respuesta IA (Se eliminó Expediente)
    var selectedTab by remember(initialTab) {
        mutableIntStateOf(if (initialTab > 1) 1 else initialTab)
    }
    val context = LocalContext.current

    when (processState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BgDark),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BgDark),
                contentAlignment = Alignment.Center
            ) {
                Text(text = processState.message, color = RedAlertText)
            }
        }
        is UiState.Success -> {
            val process = processState.data

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BgDark)
            ) {
                DetailTopBar(
                    courtSubtitle = process.courtName,
                    onBackClick = onBackClick
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Tarjeta Superior del Radicado y Estado
                    item {
                        Surface(
                            color = CardDark,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "RADICADO",
                                            color = TextSecondary,
                                            fontSize = 10.sp,
                                            lineHeight = 12.sp,
                                            letterSpacing = 0.8.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = process.radicado,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            lineHeight = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CategoryBadge(category = process.category)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = GreenStatusBg,
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, GreenStatusBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(GreenStatusText)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = process.status,
                                                color = GreenStatusText,
                                                fontSize = 11.sp,
                                                lineHeight = 13.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    val daysBg = if (process.isUrgent) RedAlertBg else CardInnerDark
                                    val daysBorder = if (process.isUrgent) RedAlertBorder else BorderSubtle
                                    val daysColor = if (process.isUrgent) RedAlertText else LightBlueButton

                                    Surface(
                                        color = daysBg,
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, daysBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Schedule,
                                                contentDescription = null,
                                                tint = daysColor,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = process.daysRemainingText,
                                                color = daysColor,
                                                fontSize = 11.sp,
                                                lineHeight = 13.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    if (process.isInMyProcesses) {
                                        Surface(
                                            color = GreenStatusBg,
                                            shape = RoundedCornerShape(50),
                                            border = BorderStroke(1.dp, GreenStatusBorder)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = GreenStatusText,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = "En Mis Procesos",
                                                    color = GreenStatusText,
                                                    fontSize = 11.sp,
                                                    lineHeight = 13.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    } else {
                                        Surface(
                                            color = BlueBadgeBg,
                                            shape = RoundedCornerShape(50),
                                            border = BorderStroke(1.dp, PrimaryBlue),
                                            modifier = Modifier.clickable {
                                                onAddProcessToHome(process.id)
                                                Toast.makeText(
                                                    context,
                                                    "Proceso agregado a tu pantalla de Inicio",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.AddCircleOutline,
                                                    contentDescription = null,
                                                    tint = LightBlueButton,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = "Agregar Proceso a Inicio",
                                                    color = LightBlueButton,
                                                    fontSize = 11.sp,
                                                    lineHeight = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Tarjeta de Información General
                    item {
                        Surface(
                            color = CardDark,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        tint = LightBlueButton,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Información general",
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        lineHeight = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                InfoRowItem(
                                    icon = Icons.Outlined.AccountBalance,
                                    label = "Despacho",
                                    value = process.courtName
                                )
                                InfoRowItem(
                                    icon = Icons.Outlined.LocationOn,
                                    label = "Ciudad",
                                    value = process.city
                                )
                                InfoRowItem(
                                    icon = Icons.Outlined.Groups,
                                    label = "Partes",
                                    value = process.parties
                                )
                                InfoRowItem(
                                    icon = Icons.Outlined.CalendarToday,
                                    label = "Fecha de radicación",
                                    value = process.filingDate
                                )
                            }
                        }
                    }

                    // 3. Selector de 2 Pestañas: Actuaciones | Respuesta IA (Se eliminó Expediente)
                    item {
                        Surface(
                            color = CardDark,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TabButton(
                                    text = "Actuaciones",
                                    isSelected = selectedTab == 0,
                                    isAiTab = false,
                                    onClick = { selectedTab = 0 },
                                    modifier = Modifier.weight(1f)
                                )
                                TabButton(
                                    text = "Respuesta IA",
                                    isSelected = selectedTab == 1,
                                    isAiTab = true,
                                    onClick = { selectedTab = 1 },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // 4. Contenido según la pestaña seleccionada
                    when (selectedTab) {
                        0 -> {
                            item {
                                ActuacionesTabContent(actuaciones = process.actuaciones)
                            }
                        }
                        1 -> {
                            item {
                                RespuestaIaEnDesarrolloTab()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRowItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Surface(
        color = CardInnerDark,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = LightBlueButton,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = label,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 13.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    isAiTab: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .height(46.dp)
            .clip(shape)
            .then(
                when {
                    isSelected && isAiTab -> Modifier.background(AiGradientBrush)
                    isSelected -> Modifier.background(PrimaryBlue)
                    else -> Modifier.background(Color.Transparent)
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            if (isAiTab) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else LightBlueButton,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 13.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ActuacionesTabContent(actuaciones: List<ProcessActuacion>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Historial de Actuaciones",
                color = TextPrimary,
                fontSize = 15.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${actuaciones.size} providencias",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        actuaciones.forEach { act ->
            Surface(
                color = CardDark,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = act.title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = CardInnerDark,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = act.date,
                                color = LightBlueButton,
                                fontSize = 11.sp,
                                lineHeight = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = act.description,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// ==================== PESTAÑA RESPUESTA IA: GLOBITOS CHOCANDO + "EN DESARROLLO" ====================
private class BubbleNode(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val radius: Float,
    val color: Color
)

@Composable
private fun RespuestaIaEnDesarrolloTab() {
    val bubbles = remember {
        listOf(
            BubbleNode(120f, 110f, 4.2f, 3.4f, 42f, Color(0xFF8B5CF6)),
            BubbleNode(280f, 150f, -3.8f, 4.0f, 36f, Color(0xFF60A5FA)),
            BubbleNode(420f, 100f, 3.5f, -3.6f, 46f, Color(0xFF34D399)),
            BubbleNode(180f, 240f, -4.0f, -3.2f, 38f, Color(0xFFF472B6)),
            BubbleNode(350f, 260f, 3.9f, 3.7f, 40f, Color(0xFFA78BFA)),
            BubbleNode(240f, 180f, -3.3f, -4.1f, 34f, Color(0xFF38BDF8))
        )
    }

    var frameTick by remember { mutableLongStateOf(0L) }
    var canvasWidth by remember { mutableFloatStateOf(0f) }
    var canvasHeight by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameTime ->
                if (canvasWidth > 0f && canvasHeight > 0f) {
                    // 1. Mover cada globito y rebotar con las paredes
                    for (b in bubbles) {
                        b.x += b.vx
                        b.y += b.vy

                        if (b.x - b.radius < 0f) {
                            b.x = b.radius
                            b.vx = -b.vx
                        } else if (b.x + b.radius > canvasWidth) {
                            b.x = canvasWidth - b.radius
                            b.vx = -b.vx
                        }

                        if (b.y - b.radius < 0f) {
                            b.y = b.radius
                            b.vy = -b.vy
                        } else if (b.y + b.radius > canvasHeight) {
                            b.y = canvasHeight - b.radius
                            b.vy = -b.vy
                        }
                    }

                    // 2. Detectar y resolver choques elásticos entre globitos
                    for (i in 0 until bubbles.size) {
                        for (j in i + 1 until bubbles.size) {
                            val b1 = bubbles[i]
                            val b2 = bubbles[j]
                            val dx = b2.x - b1.x
                            val dy = b2.y - b1.y
                            val dist = hypot(dx, dy)
                            val minDist = b1.radius + b2.radius

                            if (dist < minDist && dist > 0.001f) {
                                val nx = dx / dist
                                val ny = dy / dist

                                // Separar globitos para evitar que se superpongan
                                val overlap = (minDist - dist) / 2f
                                b1.x -= overlap * nx
                                b1.y -= overlap * ny
                                b2.x += overlap * nx
                                b2.y += overlap * ny

                                // Intercambiar velocidad a lo largo de la normal de choque
                                val kx = b1.vx - b2.vx
                                val ky = b1.vy - b2.vy
                                val p = 2f * (nx * kx + ny * ky) / 2f

                                b1.vx -= p * nx
                                b1.vy -= p * ny
                                b2.vx += p * nx
                                b2.vy += p * ny
                            }
                        }
                    }
                }
                frameTick = frameTime
            }
        }
    }

    Surface(
        color = AiCardBg,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AiCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Lienzo con los globitos chocando entre sí
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardInnerDark),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Lectura de frameTick para redibujar en cada fotograma
                    val tick = frameTick
                    if (tick >= 0L) {
                        canvasWidth = size.width
                        canvasHeight = size.height

                        bubbles.forEach { b ->
                            // Cuerpo del globito con degradado esférico
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        b.color.copy(alpha = 0.95f),
                                        b.color.copy(alpha = 0.45f)
                                    ),
                                    center = Offset(b.x - b.radius * 0.25f, b.y - b.radius * 0.25f),
                                    radius = b.radius * 1.1f
                                ),
                                radius = b.radius,
                                center = Offset(b.x, b.y)
                            )

                            // Brillo superior de cada globito
                            drawCircle(
                                color = Color.White.copy(alpha = 0.45f),
                                radius = b.radius * 0.25f,
                                center = Offset(b.x - b.radius * 0.3f, b.y - b.radius * 0.3f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = AiBadgeBg,
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, AiPurple.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFD8B4FE),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "En desarrollo",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "El módulo de Respuesta IA se encuentra en desarrollo y estará disponible próximamente.",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}