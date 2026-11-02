package com.example.gestorjuridico.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestorjuridico.model.LegalProcess
import com.example.gestorjuridico.model.UiState
import com.example.gestorjuridico.ui.components.CategoryBadge
import com.example.gestorjuridico.ui.components.MainTopBar
import com.example.gestorjuridico.ui.components.RadicarProcesoDialog
import com.example.gestorjuridico.ui.components.SearchInputBar
import com.example.gestorjuridico.ui.theme.*

@Composable
fun ProcessesScreen(
    processesState: UiState<List<LegalProcess>>,
    selectedCategory: String,
    searchQuery: String,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onProcessClick: (String, Int) -> Unit,
    onRadicarProceso: (String) -> Unit,
    onAddProcessToHome: (String) -> Unit
) {
    val categories = listOf("Todos", "Penal", "Civil", "Laboral")
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
                    "Radicado $radicado vinculado a Inicio",
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
        MainTopBar(subtitle = "Procesos")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CardDark)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Folder,
                                contentDescription = null,
                                tint = LightBlueButton,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Procesos",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "Expedientes activos y términos",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 13.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = BlueBadgeBg,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.6f)),
                            modifier = Modifier.clickable { showRadicarDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = LightBlueButton,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Consultar",
                                    color = LightBlueButton,
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }

                        val totalActivos = if (processesState is UiState.Success) {
                            processesState.data.count { it.isInMyProcesses }
                        } else 0

                        Surface(
                            color = CardDark,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "$totalActivos",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "activos",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    lineHeight = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CardDark)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SwapVert,
                                contentDescription = "Ordenar",
                                tint = LightBlueButton,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            item {
                SearchInputBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    placeholder = "Buscar por radicado, cédula, nombre..."
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            Surface(
                                color = if (isSelected) PrimaryBlue else CardDark,
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) PrimaryBlue else BorderSubtle
                                ),
                                modifier = Modifier.clickable { onSelectCategory(category) }
                            ) {
                                Text(
                                    text = category,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(CardDark)
                            .border(1.dp, BorderSubtle, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = "Filtros",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            when (processesState) {
                is UiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    }
                }
                is UiState.Error -> {
                    item {
                        Text(text = processesState.message, color = RedAlertText)
                    }
                }
                is UiState.Success -> {
                    val cleanQuery = searchQuery.trim()

                    // Si el usuario buscó un radicado exacto, mostramos únicamente ese único proceso
                    val exactRadicadoMatch = if (cleanQuery.isNotEmpty()) {
                        processesState.data.filter {
                            it.radicado.equals(cleanQuery, ignoreCase = true)
                        }
                    } else emptyList()

                    val filtered = if (exactRadicadoMatch.isNotEmpty()) {
                        exactRadicadoMatch
                    } else {
                        processesState.data.filter { proc ->
                            val matchesCategory = selectedCategory == "Todos" ||
                                    proc.category.equals(selectedCategory, ignoreCase = true)

                            val matchesQuery = cleanQuery.isEmpty() ||
                                    proc.radicado.contains(cleanQuery, ignoreCase = true) ||
                                    proc.ownerCedula.contains(cleanQuery, ignoreCase = true) ||
                                    proc.parties.contains(cleanQuery, ignoreCase = true) ||
                                    proc.courtName.contains(cleanQuery, ignoreCase = true)

                            matchesCategory && matchesQuery
                        }
                    }

                    if (filtered.isEmpty()) {
                        item {
                            Surface(
                                color = CardDark,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No se encontró ningún proceso con \"$cleanQuery\"",
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(filtered, key = { it.id }) { process ->
                            DetailedProcessCard(
                                process = process,
                                onOpenDetail = { onProcessClick(process.id, 0) },
                                onAddToHome = {
                                    onAddProcessToHome(process.id)
                                    Toast.makeText(
                                        context,
                                        "Proceso agregado a la pantalla de Inicio",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailedProcessCard(
    process: LegalProcess,
    onOpenDetail: () -> Unit,
    onAddToHome: () -> Unit
) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() }
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Gavel,
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

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = CardInnerDark,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Task,
                                contentDescription = null,
                                tint = LightBlueButton,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = process.lastActionTitle,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Última actuación",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = process.lastActionDate,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val alertBg = if (process.isUrgent) RedAlertBg else CardInnerDark
                    val alertBorder = if (process.isUrgent) RedAlertBorder else BorderSubtle
                    val alertColor = if (process.isUrgent) RedAlertText else LightBlueButton

                    Surface(
                        color = alertBg,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, alertBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ErrorOutline,
                                contentDescription = null,
                                tint = alertColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = process.daysRemainingText,
                                color = alertColor,
                                fontSize = 11.sp,
                                lineHeight = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (!process.isInMyProcesses) {
                    Surface(
                        color = BlueBadgeBg,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, PrimaryBlue),
                        modifier = Modifier.clickable { onAddToHome() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar a Inicio",
                                tint = LightBlueButton,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Agregar a Inicio",
                                color = LightBlueButton,
                                fontSize = 11.sp,
                                lineHeight = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "En Inicio",
                            tint = GreenStatusText,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "En Inicio",
                            color = GreenStatusText,
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}