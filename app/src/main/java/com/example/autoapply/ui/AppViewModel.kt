package com.example.autoapply.ui
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel : ViewModel(){
    // This is the Backing property and the .asStateFlow() sets the getter of uiState to _uiState
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init{
        resetApp()
    }

    fun resetApp(){
        _uiState.value = AppUiState(
            jobSelected = false,
            selectedJobId = 0,
            submittedApplications = setOf())
    }

    fun updateSelectedJob(jobTitleId: Int){
        _uiState.value = AppUiState(
            jobSelected = true,
            selectedJobId = jobTitleId,
            submittedApplications = _uiState.value.submittedApplications)
    }

    fun submitApplication(jobTitleId: Int){
        _uiState.value = AppUiState(
            _uiState.value.jobSelected,
            _uiState.value.selectedJobId,
            _uiState.value.submittedApplications + jobTitleId)
    }

    fun cancelSelectedJob(){
        _uiState.value = AppUiState(
            jobSelected = false,
            selectedJobId = 0,
            submittedApplications = _uiState.value.submittedApplications)
        }
}