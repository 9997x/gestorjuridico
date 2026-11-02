package com.example.gestorjuridico.repository

import com.example.gestorjuridico.model.*
import com.example.gestorjuridico.network.LegalApiService
import com.example.gestorjuridico.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class LegalRepository {

    private val useMockMvp = true
    private val api: LegalApiService by lazy { RetrofitClient.apiService }

    companion object {
        private var activeCedula: String = "1098765432"

        private fun defaultFolders() = listOf(
            ProcessFolder(
                id = "f1",
                title = "Portada del expediente",
                subtitle = "Ficha técnica y carátula digitalizada",
                iconType = "folder"
            ),
            ProcessFolder(
                id = "f2",
                title = "Demanda ordinaria laboral",
                subtitle = "Petición de reintegro y acreencias laborales",
                iconType = "document"
            ),
            ProcessFolder(
                id = "f3",
                title = "Auto admisorio y traslados",
                subtitle = "Notificación personal de mandamiento",
                iconType = "gavel"
            )
        )

        private fun defaultActuaciones() = listOf(
            ProcessActuacion(
                id = "act1",
                title = "Corre término de contestación",
                date = "23 oct, 2024",
                description = "Vence pronto. Notificado por estado electrónico."
            )
        )

        private fun defaultFiles() = listOf(
            ProcessFile(
                id = "file1",
                fileName = "Demanda_Laboral_Reintegro.pdf",
                metadata = "1.9 MB • Radicado el 14 feb. 2024 • 22 págs"
            )
        )

        // Base de datos simulada con días numéricos para evaluar vencimientos (1 a 3 días = Rojo)
        private val processesDb = mutableListOf(
            // --- PROCESOS VINCULADOS A LA CÉDULA 1098765432 ---
            LegalProcess(
                id = "proc_laboral_3",
                ownerCedula = "1098765432",
                radicado = "76001-31-05-008-2024-00120-00",
                category = "Laboral",
                courtName = "Juzgado 8 Laboral del Circuito",
                stageTag = "Admisión",
                status = "En trámite",
                daysRemaining = 1, // 1 día -> Está entre 1 y 3 días (Rojo / Cuenta en Términos hoy)
                city = "Cali - Valle del Cauca",
                parties = "Carlos Alberto Muñoz vs. Logística Colombiana S.A.S.",
                filingDate = "14 feb. 2024",
                lastActionTitle = "Contestación de demanda",
                lastActionDate = "23 oct, 2024",
                isInMyProcesses = true,
                folders = defaultFolders(),
                actuaciones = defaultActuaciones(),
                files = defaultFiles()
            ),
            LegalProcess(
                id = "proc_penal_1",
                ownerCedula = "1098765432",
                radicado = "11001-31-03-025-2023-00482-00",
                category = "Penal",
                courtName = "Juzgado 25 Penal del Circuito",
                stageTag = "Cuaderno 1",
                status = "En trámite",
                daysRemaining = 12, // 12 días -> No está en rojo
                city = "Bogotá D.C.",
                parties = "Fiscalía General vs. Carlos Alberto Muñoz",
                filingDate = "10 nov. 2023",
                lastActionTitle = "Audiencia de formulación de acusación",
                lastActionDate = "18 oct, 2024",
                isInMyProcesses = true,
                folders = defaultFolders(),
                actuaciones = defaultActuaciones(),
                files = defaultFiles()
            ),
            LegalProcess(
                id = "proc_civil_2",
                ownerCedula = "1098765432",
                radicado = "05001-31-03-012-2022-00915-00",
                category = "Civil",
                courtName = "Juzgado 12 Civil del Circuito",
                stageTag = "Fijación en lista",
                status = "En audiencia", // Cuenta automáticamente en "Audiencias"
                daysRemaining = 4, // 4 días -> No está en rojo
                city = "Medellín - Antioquia",
                parties = "Inversiones del Norte S.A. vs. Constructora Andina",
                filingDate = "05 ago. 2022",
                lastActionTitle = "Traslado de peritaje técnico",
                lastActionDate = "20 oct, 2024",
                isInMyProcesses = true,
                folders = defaultFolders(),
                actuaciones = defaultActuaciones(),
                files = defaultFiles()
            ),


            // --- PROCESOS EN BASE DE DATOS SIN VINCULAR AÚN (Para agregar con botón o por radicado) ---
            LegalProcess(
                id = "proc_db_6",
                ownerCedula = "",
                radicado = "76001-31-04-010-2024-00555-00",
                category = "Penal",
                courtName = "Juzgado 10 Penal del Circuito de Cali",
                stageTag = "Juicio Oral",
                status = "En audiencia", // Al agregarlo subirá el contador de Audiencias
                daysRemaining = 2, // 2 días -> Al agregarlo se verá en rojo y subirá Términos hoy
                city = "Cali - Valle del Cauca",
                parties = "Fiscalía 14 Seccional vs. Ricardo León Martínez",
                filingDate = "28 jul. 2024",
                lastActionTitle = "Solicitud de descubrimiento probatorio",
                lastActionDate = "21 oct, 2024",
                isInMyProcesses = false,
                folders = defaultFolders(),
                actuaciones = defaultActuaciones(),
                files = defaultFiles()
            ),
            LegalProcess(
                id = "proc_db_7",
                ownerCedula = "",
                radicado = "11001-31-03-018-2024-00777-00",
                category = "Civil",
                courtName = "Juzgado 18 Civil del Circuito de Bogotá",
                stageTag = "Contestación",
                status = "En trámite",
                daysRemaining = 3, // 3 días -> En rojo (1-3 días)
                city = "Bogotá D.C.",
                parties = " Inmobiliaria Central S.A.S. vs. Grupo Constructor",
                filingDate = "12 sep. 2024",
                lastActionTitle = "Notificación por estado",
                lastActionDate = "23 oct, 2024",
                isInMyProcesses = false,
                folders = defaultFolders(),
                actuaciones = defaultActuaciones(),
                files = defaultFiles()
            )
        )
    }

    fun setSessionCedula(cedula: String) {
        val clean = cedula.trim()
        activeCedula = clean

        // Si el usuario ingresa una cédula nueva que no tenía procesos de prueba aún,
        // le vinculamos los 3 procesos base para que pueda probar todas las funciones.
        val hasProcesses = processesDb.any { it.ownerCedula == clean && it.isInMyProcesses }
        if (!hasProcesses) {
            processesDb.take(3).forEachIndexed { index, proc ->
                processesDb[index] = proc.copy(ownerCedula = clean, isInMyProcesses = true)
            }
        }
    }

    fun getSessionCedula(): String = activeCedula

    // Procesos disponibles en la BD que aún no están vinculados a la cédula actual
    fun getUnlinkedProcessesFromDb(): List<LegalProcess> {
        return processesDb.filter { !(it.isInMyProcesses && it.ownerCedula == activeCedula) }
    }

    suspend fun fetchDashboard(): DashboardSummary = withContext(Dispatchers.IO) {
        if (!useMockMvp) {
            try {
                val response = withTimeoutOrNull(3000L) { api.getDashboardSummary() }
                if (response != null && response.isSuccessful && response.body() != null) {
                    return@withContext response.body()!!
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        delay(120)

        // Filtramos solo los procesos activos vinculados a la cédula actual
        val myLinkedProcesses = processesDb.filter {
            it.isInMyProcesses && it.ownerCedula == activeCedula
        }

        // 1. Activos: total de procesos vinculados en Inicio
        val activeCount = myLinkedProcesses.size

        // 2. Audiencias: procesos cuyo estado es "En audiencia"
        val hearingsCount = myLinkedProcesses.count {
            it.status.equals("En audiencia", ignoreCase = true)
        }

        // 3. Términos hoy: procesos en rojo (les faltan entre 1 y 3 días para vencer)
        val termsTodayCount = myLinkedProcesses.count {
            it.daysRemaining in 1..3
        }

        val totalAlerts = hearingsCount + termsTodayCount

        DashboardSummary(
            userCedula = activeCedula,
            planName = "C.C. $activeCedula",
            isSynced = true,
            currentDate = "Hoy, 24 Oct",
            alertTitle = "Tienes $totalAlerts alertas importantes",
            alertSubtitle = "$hearingsCount en audiencia y $termsTodayCount término(s) por vencer (1-3 días)",
            activeCount = activeCount,
            hearingsCount = hearingsCount,
            termsTodayCount = termsTodayCount,
            recentProcesses = myLinkedProcesses
        )
    }

    suspend fun fetchProcesses(): List<LegalProcess> = withContext(Dispatchers.IO) {
        delay(120)
        // Mostramos primero los vinculados a la cédula actual y luego los demás de la BD para poder agregarlos
        processesDb.map { proc ->
            proc.copy(isInMyProcesses = proc.isInMyProcesses && proc.ownerCedula == activeCedula)
        }.sortedByDescending { it.isInMyProcesses }
    }

    suspend fun fetchProcessById(processId: String): LegalProcess = withContext(Dispatchers.IO) {
        delay(100)
        val found = processesDb.find { it.id == processId } ?: processesDb.first()
        found.copy(isInMyProcesses = found.isInMyProcesses && found.ownerCedula == activeCedula)
    }

    // Vincula un proceso por su número de radicado a la cédula actual
    suspend fun linkProcessByRadicado(radicadoInput: String): LegalProcess = withContext(Dispatchers.IO) {
        delay(200)
        val cleanInput = radicadoInput.trim()

        val existingIndex = processesDb.indexOfFirst {
            it.radicado.equals(cleanInput, ignoreCase = true) ||
                    it.radicado.contains(cleanInput, ignoreCase = true)
        }

        if (existingIndex != -1) {
            val updated = processesDb[existingIndex].copy(
                ownerCedula = activeCedula,
                isInMyProcesses = true
            )
            processesDb.removeAt(existingIndex)
            processesDb.add(0, updated)
            return@withContext updated
        }

        // Si el radicado no estaba en la lista previa, lo recupera de la BD con 2 días restantes (en rojo)
        val autoLoadedFromDb = LegalProcess(
            id = "proc_db_${System.currentTimeMillis()}",
            ownerCedula = activeCedula,
            radicado = cleanInput,
            category = "Laboral",
            courtName = "Juzgado 8 Laboral del Circuito",
            stageTag = "Admisión",
            status = "En audiencia",
            daysRemaining = 2, // 2 días -> Entra en rango 1-3 días (Rojo)
            city = "Cali - Valle del Cauca",
            parties = "Usuario C.C. $activeCedula vs. Logística Colombiana S.A.S.",
            filingDate = "24 oct. 2024",
            lastActionTitle = "Auto que fija audiencia y corre traslado",
            lastActionDate = "24 oct, 2024",
            isInMyProcesses = true,
            folders = defaultFolders(),
            actuaciones = defaultActuaciones(),
            files = defaultFiles()
        )
        processesDb.add(0, autoLoadedFromDb)
        autoLoadedFromDb
    }

    // Agrega un proceso de la BD a la pantalla de Inicio de la cédula actual
    suspend fun addProcessToHome(processId: String): LegalProcess? = withContext(Dispatchers.IO) {
        delay(150)
        val index = processesDb.indexOfFirst { it.id == processId }
        if (index != -1) {
            val updated = processesDb[index].copy(
                ownerCedula = activeCedula,
                isInMyProcesses = true
            )
            processesDb.removeAt(index)
            processesDb.add(0, updated)
            return@withContext updated
        }
        null
    }

    suspend fun generateAiDraft(processId: String, actuationType: String): String = withContext(Dispatchers.IO) {
        delay(500)
        val process = processesDb.find { it.id == processId } ?: processesDb.first()
        """
SEÑOR
${process.courtName.uppercase()}
(REPARTO)
E. S. D.

REF.: Radicado Nº ${process.radicado}
PROCESO: ${process.parties}
CÉDULA VINCULADA: ${activeCedula}
ACTUACIÓN PROYECTADA: ${actuationType.uppercase()}

Respetado Señor Juez,

Habiendo procesado y vectorizado ${process.files.size} documento(s) PDF del expediente (${process.files.firstOrNull()?.fileName ?: "Expediente.pdf"}), me permito presentar el borrador de $actuationType...
        """.trimIndent()
    }
}