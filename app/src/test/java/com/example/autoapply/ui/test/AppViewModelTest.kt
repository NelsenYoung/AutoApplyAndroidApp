package com.example.autoapply.ui.test
import com.example.autoapply.R
import com.example.autoapply.ui.AppUiState
import org.junit.Test
import com.example.autoapply.ui.AppViewModel
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppViewModelTest {
    private val viewModel = AppViewModel()
    private val test_job_title_id = R.string.software_engineer

    @Test
    fun appViewModel_submitApplication_jobAddedToSubmittedApps(){
        var currentAppUiState = viewModel.uiState.value
        val correctSubmittedApps = currentAppUiState.submittedApplications + test_job_title_id
        viewModel.submitApplication(test_job_title_id)

        currentAppUiState = viewModel.uiState.value
        assertTrue { correctSubmittedApps == currentAppUiState.submittedApplications }
    }

    @Test
    fun appViewModel_cancelApplication_noResult(){
        var currentAppUiState = viewModel.uiState.value
        val expectedUiState = AppUiState(
            jobSelected = false,
            selectedJobId = 0,
            submittedApplications = currentAppUiState.submittedApplications
        )
        viewModel.cancelSelectedJob()
        currentAppUiState = viewModel.uiState.value
        assertEquals(expectedUiState, currentAppUiState)
    }

    @Test
    fun appViewModel_selectThenCancelApplication_selectedJobReset(){
        var currentAppUiState = viewModel.uiState.value
        val expectedUiState = AppUiState(
            jobSelected = false,
            selectedJobId = 0,
            submittedApplications = currentAppUiState.submittedApplications
        )
        // Select a job then cancel it
        viewModel.updateSelectedJob(test_job_title_id)
        viewModel.cancelSelectedJob()
        currentAppUiState = viewModel.uiState.value
        assertEquals(expectedUiState, currentAppUiState)
    }
}