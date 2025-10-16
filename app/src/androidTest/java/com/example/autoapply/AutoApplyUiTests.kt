package com.example.autoapply

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.autoapply.ui.JobsApp
import com.example.autoapply.ui.theme.AutoApplyTheme
import org.junit.Rule
import org.junit.Test

class AutoApplyUiTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun select_job_and_submit(){
        composeTestRule.setContent {
            AutoApplyTheme {
                JobsApp()
            }
        }
        composeTestRule.onNodeWithContentDescription("Button 1").performClick()
        composeTestRule.onNodeWithText("First Name").performTextInput("Nelsen")
        composeTestRule.onNodeWithText("Submit").performClick()
        composeTestRule.onNodeWithText("Submitted").assertExists("No node with this text was found")
    }

    @Test
    fun select_job_and_cancel(){
        composeTestRule.setContent {
            AutoApplyTheme {
                JobsApp()
            }
        }
        composeTestRule.onNodeWithContentDescription("Button 1").performClick()
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.onNodeWithContentDescription("Button 1").assertExists("No node with this text was found")
    }

    @Test
    fun select_job_then_select_different_job(){
        composeTestRule.setContent {
            AutoApplyTheme {
                JobsApp()
            }
        }
        composeTestRule.onNodeWithContentDescription("Button 1").performClick()
        composeTestRule.onNodeWithContentDescription("Button 0").performClick()
        composeTestRule.onNodeWithText("First Name").performTextInput("Nelsen")
        composeTestRule.onNodeWithContentDescription("Button 1").assertExists("No node with this text was found")
        composeTestRule.onNodeWithContentDescription("Button 0").assertDoesNotExist()
    }
}