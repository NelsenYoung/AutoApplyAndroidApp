package com.example.autoapply.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autoapply.R
import com.example.autoapply.data.Datasource
import com.example.autoapply.model.JobDetails
import com.example.autoapply.ui.theme.AutoApplyTheme
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.navigation.NavController

enum class AppScreen(){
    Start,
    Form,
    Summary
}

val jobs = Datasource().loadJobs()

@Composable
fun JobsApp(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel()
) {
    val appUiState by appViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreen.Start.name,
        modifier = modifier
    ) {
        composable(route = AppScreen.Start.name) {
            JobList(
                appUiState,
                appViewModel,
                navController,
                modifier = modifier
            )
        }
        composable(route = AppScreen.Form.name){
            val context = LocalContext.current
            val answers = appUiState.applicationAnswers
            TopAppBar()
            ApplicationForm(
                questions = appUiState.applicationQuestions,
                answers = answers,
                updateAnswers = { newAnswers -> appViewModel.updateAnswers(newAnswers) },
                updateAnswer = { newAnswer, index -> appViewModel.updateAnswer(newAnswer, index) },
                modifier = Modifier
                    .padding(8.dp)
            )
        }
        composable(route = AppScreen.Summary.name){
            SummaryScreen(
                job = jobs[appUiState.selectedJobIndex],
                questions =appUiState.applicationQuestions,
                answers = appUiState.applicationAnswers
            )
        }
    }
}

@Composable
fun JobList(
    appUiState: AppUiState,
    appViewModel: AppViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
){
    Scaffold (
        topBar = {
            TopAppBar()
        }
    ) { it ->
        LazyColumn(contentPadding = it){
            itemsIndexed(jobs){ index, job ->
                val questions = stringArrayResource(id = job.applicationQuestionsId).toList()
                val answers = MutableList(questions.size) { "" }
                JobCard(
                    job,
                    index = index,
                    uiState = appUiState,
                    expand = {
                                navController.navigate(AppScreen.Form.name)
                                appViewModel.updateQuestions(questions)
                                appViewModel.updateAnswers(answers)
                             },
                    modifier = modifier.padding(8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(modifier: Modifier = Modifier){
    CenterAlignedTopAppBar(
        title = {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                Image(
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .padding(16.dp),
                    painter = painterResource(R.drawable.google_logo),
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
fun JobCard(
    job: JobDetails,
    index: Int,
    uiState: AppUiState,
    expand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val expanded = uiState.selectedJobIndex == index && uiState.jobSelected
    val submitted = index in uiState.submittedApplications
    val color = animateColorAsState(
        targetValue = if (submitted) MaterialTheme.colorScheme.tertiaryContainer
        else if (uiState.selectedJobIndex == index) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.secondaryContainer
    )
    Card(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
    ) {
        Column (
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                .background(color = color.value)
        ) {
            Row{
                Image(
                    painter = painterResource(job.companyIconResourceId),
                    contentDescription = stringResource(job.companyNameResourceId),
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .padding(8.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = LocalContext.current.getString(job.jobTitleResourceId),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Row {
                Column {
                    Text(
                        text = LocalContext.current.getString(job.companyNameResourceId),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column {
                    Text(
                        text = LocalContext.current.getString(job.payDetailsResourceId),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Row(
                modifier = Modifier.padding(8.dp)
            ){
                Text(
                    text = LocalContext.current.getString(job.locationResourceId),
                    //modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.weight(1f))
                if (submitted) {
                    Text("Submitted")
                }else if(!expanded) {
                    ApplyButton(
                        onClick = { expand() },
                        index = index,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun ApplyButton(onClick: () -> Unit, index: Int, modifier: Modifier = Modifier) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.semantics{
            contentDescription = "Button $index"
        }
    ) {
        Text("Apply")
    }
}

@Composable
fun ApplicationForm(questions: List<String>, answers: List<String>, updateAnswers: (MutableList<String>) -> Unit, updateAnswer: (String, Int) -> Unit, modifier: Modifier = Modifier){
    LazyColumn{
        itemsIndexed(questions){ index, question ->
            TextField(
                value = answers[index],
                onValueChange = {
                                    updateAnswer(it, index)
                                },
                label = {Text(question)}
            )
        }
    }
}

@Composable
fun SummaryScreen(job: JobDetails, questions: List<String>, answers: List<String>, modifier: Modifier = Modifier){
    Text(text = LocalContext.current.getString(job.jobTitleResourceId))
    LazyColumn {
        itemsIndexed(questions){ index, question ->
            Text("$question: ${answers[index]}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JobCardPreview() {
    AutoApplyTheme {
        JobsApp()
    }
}

@Preview(showBackground = true)
@Composable
fun ApplicationFormPreview() {
    AutoApplyTheme {
        ApplicationForm(
            questions = listOf("Question 1", "Question 2", "Question 3"),
            answers = MutableList(3) { "" },
            updateAnswers = { newAnswers -> },
            updateAnswer = { newAnswer, index -> },
            modifier = Modifier.padding(8.dp)
        )
    }
}

//@Preview
//@Composable
//fun DarkThemePreview(){
//    AutoApplyTheme(darkTheme = true) {
//        JobsApp()
//    }
//}