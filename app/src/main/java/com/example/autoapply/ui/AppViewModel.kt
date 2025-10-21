package com.example.autoapply.ui
import androidx.lifecycle.ViewModel
import com.example.autoapply.model.JobDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.compose.ui.res.stringArrayResource
import com.example.autoapply.R

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
            selectedJobIndex = 0,
            submittedApplications = setOf())
    }

    fun updateSelectedJob(jobIndex: Int){
        _uiState.value = AppUiState(
            jobSelected = true,
            selectedJobIndex = jobIndex,
            submittedApplications = _uiState.value.submittedApplications,
            applicationQuestions = _uiState.value.applicationQuestions,
            applicationAnswers = _uiState.value.applicationAnswers
        )
    }

    fun submitApplication(jobIndex: Int){
        _uiState.value = AppUiState(
            _uiState.value.jobSelected,
            _uiState.value.selectedJobIndex,
            _uiState.value.submittedApplications + jobIndex)
    }

    fun cancelSelectedJob(){
        _uiState.value = AppUiState(
            jobSelected = false,
            selectedJobIndex = 0,
            submittedApplications = _uiState.value.submittedApplications
        )
    }

    fun updateQuestions(questions: List<String>){
        _uiState.value = _uiState.value.copy(
            applicationQuestions = questions.toMutableList()
        )
    }

    fun updateAnswers(answers: MutableList<String>){
        _uiState.value = _uiState.value.copy(
            applicationAnswers = answers
        )
    }

    fun updateAnswer(answer: String, index: Int){
        _uiState.value = _uiState.value.copy(
            applicationAnswers = _uiState.value.applicationAnswers.apply {
                this[index] += answer
            }
        )
    }
}