package org.lightscout.presentation.tasklist

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import io.mockk.mockk
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
class TaskListScreenTest {

    @get:Rule val hiltRule = HiltAndroidRule(this)

    @get:Rule val composeRule = createComposeRule()

    @BindValue var viewModel: TaskListViewModel = mockk(relaxed = true)

    private lateinit var stateFlow: MutableStateFlow<TaskListState>
    private lateinit var effectFlow: MutableSharedFlow<TaskListEffect>

    @Before
    fun setup() {

        stateFlow = MutableStateFlow(TaskListState())
        effectFlow = MutableSharedFlow()

        every { viewModel.state } returns (stateFlow)
        every { viewModel.effect } returns (effectFlow)
    }

    @Test
    fun givenTaskList_whenScreenIsDisplayed_thenTasksAreRenderedCorrectly() {
        // Given
        val tasks =
                listOf(
                        Task("1", "Task 1", "Description 1", false, 1234567890, 1234567890),
                        Task("2", "Task 2", "Description 2", true, 1234567890, 1234567890)
                )

        stateFlow.value = TaskListState(tasks = tasks)

        // When
        setScreen()

        // Then
        composeRule.onNodeWithText("Task 1").assertIsDisplayed()
        composeRule.onNodeWithText("Description 1").assertIsDisplayed()
        composeRule.onNodeWithText("Task 2").assertIsDisplayed()
        composeRule.onNodeWithText("Description 2").assertIsDisplayed()
        composeRule.onAllNodesWithContentDescription(TestTags.CHECKBOX)[0].assertIsOff()
        composeRule.onAllNodesWithContentDescription(TestTags.CHECKBOX)[1].assertIsOn()
    }

    @Test
    fun givenEmptyTaskList_whenScreenIsDisplayed_thenEmptyStateIsShown() {
        // Given
        stateFlow.value = TaskListState(tasks = emptyList())

        // When
        setScreen()

        // Then
        composeRule.onNodeWithTag(TestTags.TASK_LIST).onChildren().assertCountEquals(0)
    }

    @Test
    fun givenLoadingState_whenScreenIsDisplayed_thenLoadingIndicatorIsShown() {
        // Given
        stateFlow.value = TaskListState(isLoading = true)

        // When
        setScreen()

        // Then
        composeRule.onNodeWithTag(TestTags.LOADING_INDICATOR).assertIsDisplayed()
    }

    @Test
    fun givenTaskListScreen_whenAddButtonClicked_thenCreateTaskDialogIsDisplayed() {
        // Given
        stateFlow.value = TaskListState(tasks = emptyList())

        // When
        setScreen()
        composeRule.onNodeWithTag(TestTags.ADD_TASK_BUTTON).performClick()

        // Then
        composeRule.onNodeWithText("Create New Task").assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.TASK_TITLE_INPUT).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.TASK_DESCRIPTION_INPUT).assertIsDisplayed()
    }

    private fun setScreen() {
        composeRule.setContent { TaskListScreen({}, viewModel) }
    }
}
