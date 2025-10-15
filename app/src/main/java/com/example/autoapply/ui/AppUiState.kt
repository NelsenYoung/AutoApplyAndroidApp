package com.example.autoapply.ui
import kotlin.collections.MutableSet

data class AppUiState(
    val jobSelected: Boolean = false,
    val selectedJobId: Int = 0,
    val submittedApplications: Set<Int> = setOf(),
)

