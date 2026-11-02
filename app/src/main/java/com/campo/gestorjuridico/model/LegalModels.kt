package com.example.gestorjuridico.model

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

data class DashboardSummary(
    val userCedula: String,
    val planName: String,
    val isSynced: Boolean,
    val currentDate: String,
    val alertTitle: String,
    val alertSubtitle: String,
    val activeCount: Int,
    val hearingsCount: Int,
    val termsTodayCount: Int,
    val recentProcesses: List<LegalProcess>
)

data class LegalProcess(
    val id: String,
    val ownerCedula: String = "",  // Cédula a la que está vinculado el proceso
    val radicado: String,
    val category: String,          // "Penal", "Civil", "Laboral"
    val courtName: String,
    val stageTag: String,
    val status: String,            // "En trámite", "En audiencia"
    val daysRemaining: Int,        // Valor numérico de días para evaluar vencimiento (1 a 3 días)
    val city: String,
    val parties: String,
    val filingDate: String,
    val lastActionTitle: String,
    val lastActionDate: String,
    val isInMyProcesses: Boolean = true,
    val folders: List<ProcessFolder> = emptyList(),
    val actuaciones: List<ProcessActuacion> = emptyList(),
    val files: List<ProcessFile> = emptyList(),
    val aiConfig: AiAssistantConfig = AiAssistantConfig()
) {
    // Se pone en rojo (urgente) y cuenta en "Términos hoy" cuando le faltan de 1 a 3 días
    val isUrgent: Boolean
        get() = daysRemaining in 1..3

    val daysRemainingText: String
        get() = if (daysRemaining == 1) "1 día restante" else "$daysRemaining días restantes"
}

data class ProcessFolder(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconType: String
)

data class ProcessActuacion(
    val id: String,
    val title: String,
    val date: String,
    val description: String
)

data class ProcessFile(
    val id: String,
    val fileName: String,
    val metadata: String
)

data class AiAssistantConfig(
    val badgeText: String = "IA Jurídica Especializada",
    val title: String = "Asistente de Redacción Legal",
    val description: String = "Recopila y analiza todos los PDFs y pruebas del expediente para estructurar un borrador de respuesta sólida y fundamentada.",
    val checkItems: List<String> = listOf(
        "2 documentos procesados y vectorizados",
        "Jurisprudencia penal colombiana detectada (Ley 906)",
        "Formato procesal y excepciones previas configuradas"
    ),
    val availableProjections: List<String> = listOf(
        "Alegatos de conclusión (Fase final de juicio)",
        "Contestación de demanda y excepciones",
        "Recurso de reposición y en subsidio apelación"
    ),
    val generatedDraftSample: String = """
SEÑOR
JUEZ PENAL DEL CIRCUITO DE BUCARAMANGA
(REPARTO)
E. S. D.

REF.: Radicado Nº 76001-31-05-008-2024-00120-00
PROCESO: Carlos Alberto Muñoz vs. Logística Colombiana S.A.S.
ASUNTO: ALEGATOS DE CONCLUSIÓN

Respetado Señor Juez,

Actuando en calidad de apoderado judicial dentro del proceso de la referencia, estando dentro del término legal oportuno, me permito presentar los ALEGATOS DE CONCLUSIÓN con fundamento en los documentos procesados del expediente...
    """.trimIndent()
)

data class AiDraftRequest(
    val processId: String,
    val actuationType: String
)

data class AiDraftResponse(
    val generatedText: String
)