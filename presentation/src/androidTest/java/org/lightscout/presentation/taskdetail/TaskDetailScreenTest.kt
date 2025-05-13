package org.lightscout.presentation.taskdetail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.lightscout.domain.model.Task
import org.lightscout.presentation.di.PresentationModule

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(PresentationModule::class)
class TaskDetailScreenTest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1) val composeRule = createComposeRule()

    @BindValue lateinit var viewModel: TaskDetailViewModel

    private lateinit var stateFlow: MutableStateFlow<TaskDetailState>
    private lateinit var effectFlow: MutableSharedFlow<TaskDetailEffect>
    private val mockTask =
            Task(
                    id = "123",
                    title = "Test Task",
                    description = "Test Description",
                    isCompleted = false,
                    createdAt = 1234567890,
                    updatedAt = 1234567890
            )
    private val onNavigateBack: () -> Unit = mockk(relaxed = true)

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
        stateFlow = MutableStateFlow(TaskDetailState())
        effectFlow = MutableSharedFlow()

        every { viewModel.state } returns stateFlow
        every { viewModel.effect } returns effectFlow
    }

    @Test
    fun givenTaskDetail_whenScreenIsDisplayed_thenTaskDetailsAreRendered() {
        // Given
        stateFlow.value = TaskDetailState(task = mockTask)

        // When
        setScreen()

        // Then
        composeRule.onNodeWithText("Task Details").assertIsDisplayed()
        composeRule.onNodeWithText("Test Task").assertIsDisplayed()
        composeRule.onNodeWithText("Test Description").assertIsDisplayed()
        composeRule.onNodeWithTag(TaskDetailTestTags.EDIT_BUTTON).assertIsDisplayed()
        composeRule.onNodeWithTag(TaskDetailTestTags.DELETE_BUTTON).assertIsDisplayed()
        composeRule.onNodeWithTag(TaskDetailTestTags.COMPLETION_CHECKBOX).assertIsDisplayed()
    }

    @Test
    fun givenLoadingState_whenScreenIsDisplayed_thenLoadingIndicatorIsShown() {
        // Given
        stateFlow.value = TaskDetailState(isLoading = true)

        // When
        setScreen()

        // Then
        composeRule.onNodeWithTag(TaskDetailTestTags.LOADING_INDICATOR).assertIsDisplayed()
    }

    @Test
    fun givenTaskDetail_whenEditButtonClicked_thenEditFormIsDisplayed() {
        // Given
        stateFlow.value = TaskDetailState(task = mockTask)

        // When
        setScreen()
        composeRule.onNodeWithTag(TaskDetailTestTags.EDIT_BUTTON).performClick()

        // Then verify the event was triggered
        verify { viewModel.onEvent(TaskDetailEvent.EditTask) }

        // Update state to reflect edit mode
        stateFlow.value = stateFlow.value.copy(isEditing = true)

        // Then verify UI shows edit form
        composeRule.onNodeWithText("Edit Task").assertIsDisplayed()
        composeRule.onNodeWithTag(TaskDetailTestTags.TITLE_FIELD).assertIsDisplayed()
        composeRule.onNodeWithTag(TaskDetailTestTags.DESCRIPTION_FIELD).assertIsDisplayed()
        composeRule.onNodeWithTag(TaskDetailTestTags.SAVE_BUTTON).assertIsDisplayed()
        composeRule.onNodeWithTag(TaskDetailTestTags.CANCEL_BUTTON).assertIsDisplayed()
    }

    @Test
    fun givenTaskInEditMode_whenSaveButtonClicked_thenViewModelUpdatesTask() {
        // Given
        stateFlow.value = TaskDetailState(task = mockTask, isEditing = true)

        // When
        setScreen()
        composeRule.onNodeWithTag(TaskDetailTestTags.TITLE_FIELD).performTextInput(" Updated")
        composeRule.onNodeWithTag(TaskDetailTestTags.DESCRIPTION_FIELD).performTextInput(" Updated")
        composeRule.onNodeWithTag(TaskDetailTestTags.SAVE_BUTTON).performClick()

        // Then
        verify { viewModel.onEvent(any<TaskDetailEvent.UpdateTask>()) }
    }

    @Test
    fun givenTaskInEditMode_whenCancelButtonClicked_thenEditModeIsCancelled() {
        // Given
        stateFlow.value = TaskDetailState(task = mockTask, isEditing = true)

        // When
        setScreen()
        composeRule.onNodeWithTag(TaskDetailTestTags.CANCEL_BUTTON).performClick()

        // Then
        verify { viewModel.onEvent(TaskDetailEvent.CancelEdit) }
    }

    @Test
    fun givenTaskDetail_whenCompletionToggleClicked_thenCompletionStateIsToggled() {
        // Given
        stateFlow.value = TaskDetailState(task = mockTask)

        // When
        setScreen()
        composeRule.onNodeWithTag(TaskDetailTestTags.COMPLETION_CHECKBOX).performClick()

        // Then
        verify { viewModel.onEvent(TaskDetailEvent.ToggleTaskCompletion(mockTask.id)) }
    }

    @Test
    fun givenTaskDetail_whenDeleteButtonClicked_thenTaskIsDeleted() {
        // Given
        stateFlow.value = TaskDetailState(task = mockTask)

        // When
        setScreen()
        composeRule.onNodeWithTag(TaskDetailTestTags.DELETE_BUTTON).performClick()

        // Then
        verify { viewModel.onEvent(TaskDetailEvent.DeleteTask(mockTask.id)) }
    }

    @Test
    fun givenTaskDetail_whenBackButtonPressed_thenNavigateBackIsCalled() {
        // Given
        stateFlow.value = TaskDetailState(task = mockTask)

        // When
        setScreen()
        composeRule.onNodeWithContentDescription("Back").performClick()

        // Then
        verify { onNavigateBack() }
    }

    @Test
    fun givenErrorState_whenScreenIsDisplayed_thenErrorStateIsHandled() {
        // Given
        val errorMessage = "Failed to load task"
        stateFlow.value = TaskDetailState(error = errorMessage)

        // When
        setScreen()

        // Then
        // Verify that the screen shows content rather than crashing
        composeRule.onNodeWithTag(TaskDetailTestTags.SCREEN).assertExists()
    }

    @Test
    fun givenCompletedTask_whenScreenIsDisplayed_thenCompletedStateIsShown() {
        // Given
        val completedTask = mockTask.copy(isCompleted = true)
        stateFlow.value = TaskDetailState(task = completedTask)

        // When
        setScreen()

        // Then
        composeRule.onNodeWithText("Completed").assertIsDisplayed()
    }

    private fun setScreen() {
        composeRule.setContent {
            TaskDetailScreen(
                    taskId = mockTask.id,
                    onNavigateBack = onNavigateBack,
                    viewModel = viewModel
            )
        }
    }
}
