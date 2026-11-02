package com.example.gestorjuridico.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gestorjuridico.ui.theme.*
import kotlinx.coroutines.delay

private enum class LoginAnimStage {
    FORM,               // Ventana emergente pidiendo la cédula
    CONFIRM_DIALOG,     // Segunda ventana emergente confirmando la cédula digitada
    SHRINK_TO_CIRCLE,   // La ventana se contrae en un círculo
    EXPAND_BLUE_SQUARE  // El círculo se expande dejando un cuadrado azul en toda la pantalla
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CedulaLoginScreen(
    onLoginSuccess: (String) -> Unit
) {
    var cedulaInput by remember { mutableStateOf("1098765432") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var animStage by remember { mutableStateOf(LoginAnimStage.FORM) }

    val demoAccounts = listOf(
        "1098765432" to "Carlos Alberto Muñoz (3 procesos)"
    )

    // Controlador de tiempos para la secuencia de animación
    LaunchedEffect(animStage) {
        when (animStage) {
            LoginAnimStage.SHRINK_TO_CIRCLE -> {
                delay(480) // Tiempo en que la ventana se contrae formando el círculo
                animStage = LoginAnimStage.EXPAND_BLUE_SQUARE
            }
            LoginAnimStage.EXPAND_BLUE_SQUARE -> {
                delay(550) // Tiempo en que el círculo se expande dejando el cuadrado azul
                onLoginSuccess(cedulaInput.trim())
            }
            else -> {}
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // Dimensiones animadas según la etapa actual
        val targetWidth by animateDpAsState(
            targetValue = when (animStage) {
                LoginAnimStage.FORM, LoginAnimStage.CONFIRM_DIALOG -> screenWidth - 36.dp
                LoginAnimStage.SHRINK_TO_CIRCLE -> 76.dp
                LoginAnimStage.EXPAND_BLUE_SQUARE -> screenWidth
            },
            animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing),
            label = "boxWidth"
        )

        val targetHeight by animateDpAsState(
            targetValue = when (animStage) {
                LoginAnimStage.FORM, LoginAnimStage.CONFIRM_DIALOG -> 470.dp
                LoginAnimStage.SHRINK_TO_CIRCLE -> 76.dp
                LoginAnimStage.EXPAND_BLUE_SQUARE -> screenHeight
            },
            animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing),
            label = "boxHeight"
        )

        // Radio de las esquinas: 22.dp (ventana) -> 38.dp (círculo exacto) -> 0.dp (cuadrado azul)
        val targetCorner by animateDpAsState(
            targetValue = when (animStage) {
                LoginAnimStage.FORM, LoginAnimStage.CONFIRM_DIALOG -> 22.dp
                LoginAnimStage.SHRINK_TO_CIRCLE -> 38.dp
                LoginAnimStage.EXPAND_BLUE_SQUARE -> 0.dp
            },
            animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing),
            label = "boxCorner"
        )

        val targetColor by animateColorAsState(
            targetValue = when (animStage) {
                LoginAnimStage.FORM, LoginAnimStage.CONFIRM_DIALOG -> CardDark
                LoginAnimStage.SHRINK_TO_CIRCLE, LoginAnimStage.EXPAND_BLUE_SQUARE -> PrimaryBlue
            },
            animationSpec = tween(durationMillis = 420),
            label = "boxColor"
        )

        // Contenedor principal que se transforma de ventana -> círculo -> cuadrado azul
        Surface(
            color = targetColor,
            shape = RoundedCornerShape(targetCorner),
            border = if (animStage == LoginAnimStage.FORM || animStage == LoginAnimStage.CONFIRM_DIALOG) {
                BorderStroke(1.dp, BorderSubtle)
            } else null,
            modifier = Modifier.size(width = targetWidth, height = targetHeight)
        ) {
            // El contenido del formulario desaparece suavemente cuando inicia la contracción
            AnimatedVisibility(
                visible = animStage == LoginAnimStage.FORM || animStage == LoginAnimStage.CONFIRM_DIALOG,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(180))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Icono Superior
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(BlueBadgeBg)
                            .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Balance,
                            contentDescription = "Logo",
                            tint = LightBlueButton,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Vinculación Única de Cédula",
                            color = TextPrimary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ingresa tu documento de identidad para cargar los procesos judiciales asociados a tu cuenta.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Campo para digitar la Cédula
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "NÚMERO DE CÉDULA",
                            color = LightBlueButton,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = cedulaInput,
                            onValueChange = {
                                cedulaInput = it.filter { char -> char.isDigit() }
                                errorMessage = null
                            },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Badge,
                                    contentDescription = null,
                                    tint = PrimaryBlue
                                )
                            },
                            placeholder = {
                                Text("Ej: 1098765432", color = TextSecondary, fontSize = 13.sp)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardInnerDark,
                                unfocusedContainerColor = CardInnerDark,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = BorderSubtle
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = errorMessage!!,
                                color = RedAlertText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Cédulas de prueba almacenadas en la Base de Datos
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "CÉDULAS EN BASE DE DATOS (TOCA PARA PROBAR):",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        demoAccounts.forEach { (cc, description) ->
                            val isSelected = cedulaInput == cc
                            Surface(
                                color = if (isSelected) BlueBadgeBg else CardInnerDark,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) PrimaryBlue else BorderSubtle
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        cedulaInput = cc
                                        errorMessage = null
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "C.C. $cc",
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = description,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Outlined.VerifiedUser,
                                        contentDescription = null,
                                        tint = if (isSelected) GreenStatusText else TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Botón que abre la segunda ventana emergente de confirmación
                    Button(
                        onClick = {
                            if (cedulaInput.trim().length < 5) {
                                errorMessage = "Ingresa un número de cédula válido (mínimo 5 dígitos)"
                            } else {
                                animStage = LoginAnimStage.CONFIRM_DIALOG
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightBlueButton,
                            contentColor = DarkTextOnLightBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Continuar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // ==================== SEGUNDA VENTANA EMERGENTE DE CONFIRMACIÓN ====================
        if (animStage == LoginAnimStage.CONFIRM_DIALOG) {
            Dialog(
                onDismissRequest = { animStage = LoginAnimStage.FORM },
                properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
            ) {
                Surface(
                    color = CardDark,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(RedAlertBg)
                                .border(1.dp, RedAlertBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.WarningAmber,
                                contentDescription = "Confirmar",
                                tint = RedAlertText,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Text(
                            text = "¿Confirmas que esta es tu cédula?",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        // Recuadro mostrando la cédula que acaba de digitar
                        Surface(
                            color = CardInnerDark,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "DOCUMENTO DIGITADO",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "C.C. ${cedulaInput.trim()}",
                                    color = LightBlueButton,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Aviso de bloqueo futuro
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CardInnerDark)
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = LightBlueButton,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Atención: Esta cédula no podrá ser cambiada en un futuro y todos los procesos cargados serán únicamente los que contengan este documento.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }

                        // Botones: Corregir o Aceptar (dispara la animación del círculo -> cuadrado azul)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { animStage = LoginAnimStage.FORM },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = "Corregir",
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = {
                                    // Inicia la contracción en círculo -> expansión a cuadrado azul
                                    animStage = LoginAnimStage.SHRINK_TO_CIRCLE
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = "Aceptar",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}