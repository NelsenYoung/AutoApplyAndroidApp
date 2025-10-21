package com.example.autoapply.ui
import androidx.lifecycle.ViewModel
import com.example.autoapply.model.JobDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.compose.ui.res.stringArrayResource
import com.example.autoapply.R
import com.example.autoapply.data.Datasource
import android.content.res.Resources

class AppViewModel(private val resources: Resources) : ViewModel(){
    // This is the Backing property and the .asStateFlow() sets the getter of uiState to _uiState
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()
    val jobs = Datasource().loadJobs()

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
        val questions = resources.getStringArray(jobs[jobIndex].applicationQuestionsId).toList()
        val answers = MutableList(questions.size) { "" }
        _uiState.value = AppUiState(
            jobSelected = true,
            selectedJobIndex = jobIndex,
            submittedApplications = _uiState.value.submittedApplications,
            applicationQuestions = questions,
            applicationAnswers = answers
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

    fun updateAnswer(answer: String, index: Int) {
        // 1. Get the current list of answers and create a new mutable copy.
        val newAnswers = _uiState.value.applicationAnswers.toMutableList()

        // 2. Safely update the value at the specified index.
        //    We use '=' to replace the string, not '+='.
        if (index in newAnswers.indices) {
            newAnswers[index] = answer
        }

        // 3. Use `update` and `.copy()` to create a brand new AppUiState instance.
        //    This is the key to triggering recomposition.
        _uiState.update { currentState ->
            currentState.copy(applicationAnswers = newAnswers)
        }
    }
}