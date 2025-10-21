package com.example.autoapply.ui

data class AppUiState(
    val jobSelected: Boolean = false,
    val selectedJobIndex: Int = 0,
    val submittedApplications: Set<Int> = setOf(),
    val applicationQuestions: MutableList<String> = mutableListOf(),
    val applicationAnswers: MutableList<String> = mutableListOf()
)

