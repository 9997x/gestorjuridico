package com.example.gestorjuridico.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestorjuridico.model.DashboardSummary
import com.example.gestorjuridico.model.LegalProcess
import com.example.gestorjuridico.model.UiState
import com.example.gestorjuridico.ui.components.CategoryBadge
import com.example.gestorjuridico.ui.components.MainTopBar
import com.example.gestorjuridico.ui.components.RadicarProcesoDialog
import com.example.gestorjuridico.ui.components.SearchInputBar
import com.example.gestorjuridico.ui.theme.*

@Composable
fun HomeScreen(
    dashboardState: UiState<DashboardSummary>,
    onNavigateToProcesses: () -> Unit,
    onSearchToProcesses: (String) -> Unit,
    onProcessClick: (String, Int) -> Unit,
    onRadicarProceso: (String) -> Unit
) {
    var localSearch by remember { mutableStateOf("") }
    var showRadicarDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showRadicarDialog) {
        RadicarProcesoDialog(
            onDismiss = { showRadicarDialog = false },
            onConfirmRadicar = { radicado ->
                showRadicarDialog = false
                onRadicarProceso(radicado)
                Toast.makeText(
                    context,
                    "Radicado $radicado vinculado en Inicio",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        MainTopBar(subtitle = "Inicio")

        when (dashboardState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = dashboardState.message, color = RedAlertText)
                }
            }
            is UiState.Success -> {
                val data = dashboardState.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = BlueBadgeBg,
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, BlueBadgeBorder)
                                ) {
                                    Text(
                                        text = data.planName,
                                        color = LightBlueButton,
                                        fontSize = 11.sp,
                                        lineHeight = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(GreenStatusText)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Sincronizado",
                                    color = GreenStatusText,
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = data.currentDate,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Tus procesos, siempre a la mano.",
                            color = LightBlueButton,
                            fontSize = 15.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Barra de búsqueda: al buscar un radicado/cédula/nombre cambia a la pestaña Procesos mostrando solo ese proceso
                    item {
                        SearchInputBar(
                            query = localSearch,
                            onQueryChange = { localSearch = it },
                            placeholder = "Buscar por radicado, cédula, nombre...",
                            onSearchExecute = { query ->
                                onSearchToProcesses(query)
                            }
                        )
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(AlertBannerBrush)
                                .border(1.dp, BlueBadgeBorder, RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF1C355E))
                                            .border(1.dp, PrimaryBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.NotificationsActive,
                                            contentDescription = null,
                                            tint = LightBlueButton,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = data.alertTitle,
                                                color = TextPrimary,
                                                fontSize = 14.sp,
                                                lineHeight = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFD98A6C))
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = data.alertSubtitle,
                                            color = LightBlueButton.copy(alpha = 0.85f),
                                            fontSize = 12.sp,
                                            lineHeight = 15.sp,
                                            maxLines = 2
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(BgDark.copy(alpha = 0.6f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            KpiCard(
                                title = "Activos",
                                value = data.activeCount.toString(),
                                valueColor = TextPrimary,
                                icon = Icons.Outlined.CheckCircle,
                                iconTint = GreenStatusText,
                                modifier = Modifier.weight(1f)
                            )
                            KpiCard(
                                title = "Audiencias",
                                value = data.hearingsCount.toString(),
                                valueColor = PrimaryBlue,
                                icon = Icons.Outlined.CalendarToday,
                                iconTint = LightBlueButton,
                                modifier = Modifier.weight(1f)
                            )
                            KpiCard(
                                title = "Términos hoy",
                                value = data.termsTodayCount.toString(),
                                valueColor = Color(0xFFF87171),
                                icon = Icons.Outlined.Alarm,
                                iconTint = Color(0xFFF87171),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Mis procesos",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    lineHeight = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = CardDark,
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, BorderSubtle)
                                ) {
                                    Text(
                                        text = "${data.recentProcesses.size} recientes",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        lineHeight = 13.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onNavigateToProcesses() }
                            ) {
                                Text(
                                    text = "Ver todos",
                                    color = LightBlueButton,
                                    fontSize = 13.sp,
                                    lineHeight = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = LightBlueButton,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    items(data.recentProcesses, key = { it.id }) { process ->
                        HomeProcessCard(
                            process = process,
                            onClick = { onProcessClick(process.id, 0) }
                        )
                    }

                    item {
                        Button(
                            onClick = { showRadicarDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LightBlueButton,
                                contentColor = DarkTextOnLightBlue
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Consultar y Vincular Radicado Judicial",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    valueColor: Color,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    color = valueColor,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun HomeProcessCard(
    process: LegalProcess,
    onClick: () -> Unit
) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                CategoryBadge(category = process.category)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = CardInnerDark,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        val courtIcon = when (process.category) {
                            "Penal" -> Icons.Outlined.Balance
                            "Civil" -> Icons.Outlined.AccountBalance
                            else -> Icons.Outlined.Gavel
                        }
                        Icon(
                            imageVector = courtIcon,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = process.courtName,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = process.stageTag,
                        color = LightBlueButton,
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Estado:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                        maxLines = 1
                    )

                    Surface(
                        color = GreenStatusBg,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GreenStatusBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(GreenStatusText)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = process.status,
                                color = GreenStatusText,
                                fontSize = 11.sp,
                                lineHeight = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2
                            )
                        }
                    }

                    val daysBg = if (process.isUrgent) RedAlertBg else CardInnerDark
                    val daysBorder = if (process.isUrgent) RedAlertBorder else BorderSubtle
                    val daysText = if (process.isUrgent) RedAlertText else LightBlueButton

                    Surface(
                        color = daysBg,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, daysBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (process.isUrgent) Icons.Outlined.Alarm else Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = daysText,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = process.daysRemainingText,
                                color = daysText,
                                fontSize = 10.sp,
                                lineHeight = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 2
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Detalle",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                        maxLines = 1
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}