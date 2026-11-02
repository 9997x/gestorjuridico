package com.example.gestorjuridico.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestorjuridico.model.*
import com.example.gestorjuridico.repository.LegalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LegalViewModel(
    private val repository: LegalRepository = LegalRepository()
) : ViewModel() {

    private val _currentCedula = MutableStateFlow<String?>(null)
    val currentCedula: StateFlow<String?> = _currentCedula.asStateFlow()

    private val _dashboardState = MutableStateFlow<UiState<DashboardSummary>>(UiState.Loading)
    val dashboardState: StateFlow<UiState<DashboardSummary>> = _dashboardState.asStateFlow()

    private val _processesState = MutableStateFlow<UiState<List<LegalProcess>>>(UiState.Loading)
    val processesState: StateFlow<UiState<List<LegalProcess>>> = _processesState.asStateFlow()

    private val _selectedProcessState = MutableStateFlow<UiState<LegalProcess>>(UiState.Loading)
    val selectedProcessState: StateFlow<UiState<LegalProcess>> = _selectedProcessState.asStateFlow()

    private val _unlinkedDbProcesses = MutableStateFlow<List<LegalProcess>>(emptyList())
    val unlinkedDbProcesses: StateFlow<List<LegalProcess>> = _unlinkedDbProcesses.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _generatedDraft = MutableStateFlow<String?>(null)
    val generatedDraft: StateFlow<String?> = _generatedDraft.asStateFlow()

    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi: StateFlow<Boolean> = _isGeneratingAi.asStateFlow()

    fun loginWithCedula(cedula: String, onLoaded: () -> Unit) {
        viewModelScope.launch {
            _dashboardState.value = UiState.Loading
            _processesState.value = UiState.Loading
            repository.setSessionCedula(cedula)
            _currentCedula.value = repository.getSessionCedula()
            refreshAllStates()
            onLoaded()
        }
    }

    private suspend fun refreshAllStates() {
        val dashboard = repository.fetchDashboard()
        val processes = repository.fetchProcesses()
        _unlinkedDbProcesses.value = repository.getUnlinkedProcessesFromDb()
        _dashboardState.value = UiState.Success(dashboard)
        _processesState.value = UiState.Success(processes)
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadProcessDetail(processId: String) {
        viewModelScope.launch {
            _selectedProcessState.value = UiState.Loading
            val process = repository.fetchProcessById(processId)
            _selectedProcessState.value = UiState.Success(process)
            _generatedDraft.value = process.aiConfig.generatedDraftSample
        }
    }

    fun generateDraftWithAi(processId: String, actuationType: String) {
        viewModelScope.launch {
            _isGeneratingAi.value = true
            val result = repository.generateAiDraft(processId, actuationType)
            _generatedDraft.value = result
            _isGeneratingAi.value = false
        }
    }

    fun vincularPorRadicado(radicado: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.linkProcessByRadicado(radicado)
            refreshAllStates()
            onComplete()
        }
    }

    fun agregarProcesoAInicio(processId: String) {
        viewModelScope.launch {
            val updated = repository.addProcessToHome(processId)
            refreshAllStates()
            if (updated != null && _selectedProcessState.value is UiState.Success) {
                val currentDetail = (_selectedProcessState.value as UiState.Success).data
                if (currentDetail.id == processId) {
                    _selectedProcessState.value = UiState.Success(updated)
                }
            }
        }
    }
}