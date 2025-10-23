package com.example.autoapply.ui

import androidx.annotation.StringRes
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.autoapply.R
import com.example.autoapply.data.Datasource
import com.example.autoapply.model.JobDetails
import com.example.autoapply.ui.theme.AutoApplyTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import com.example.autoapply.BuildConfig
import com.example.autoapply.model.User
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_PUBLIC_ANON_KEY
) {
    install(Postgrest)
}

object GlobalState {
    var user: User? = null

    fun updateUser(user: User?) {
        this.user = user
    }
    
    fun login(email: String, password: String){
        runBlocking {
            val users = supabase.from("users").select().decodeList<User>()
            for (user in users) {
                if (user.email == email && user.password == password) {
                    updateUser(user)
                }
            }
        }
    }
}

enum class AppScreen(@StringRes val title: Int){
    Start(title = R.string.app_name),
    Form(title = R.string.application_form),
    Summary(title = R.string.summary),
    Profile(title = R.string.profile),
    LogIn(title = R.string.log_in)
}

val jobs = Datasource().loadJobs()

@Composable
fun JobsApp(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel()
) {
    val context = LocalContext.current
    val credentialManager = CredentialManager.create(context)
    val appUiState by appViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    Scaffold (
        topBar = {
            TopAppBar()
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Start.name,
            modifier = modifier.padding(contentPadding)
        ) {
            composable(route = AppScreen.Start.name) {
                JobList(
                    appUiState,
                    apply = { index: Int ->
                        appViewModel.updateSelectedJob(index)
                        navController.navigate(AppScreen.Form.name)
                    },
                    modifier = modifier
                )
            }
            composable(route = AppScreen.Form.name){
                ApplicationForm(
                    questions = appUiState.applicationQuestions,
                    curAnswers = appUiState.applicationAnswers,
                    updateAnswer = { answer, index ->
                        appViewModel.updateAnswer(answer, index)
                    },
                    next = { navController.navigate(AppScreen.Summary.name) },
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
            composable(route = AppScreen.Summary.name){
                SummaryScreen(
                    questions =appUiState.applicationQuestions,
                    answers = appUiState.applicationAnswers,
                    submit = {
                        appViewModel.submitApplication(appUiState.selectedJobIndex)
                        navController.navigate(AppScreen.Start.name)
                    }
                )
            }
            composable(route = AppScreen.Profile.name){
                SupabaseTest(
                    login = { navController.navigate(AppScreen.LogIn.name) }
                )
            }
            composable(route = AppScreen.LogIn.name){
                LogInScreen(
                    submit = { email, password ->
                        GlobalState.login(email, password)
                        if(GlobalState.user != null) {
                            navController.navigate(AppScreen.Start.name)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun JobList(
    appUiState: AppUiState,
    apply: (Int) -> Unit,
    modifier: Modifier = Modifier,
){
    Scaffold (

    ) { it ->
        LazyColumn(contentPadding = it){
            itemsIndexed(jobs){ index, job ->
                JobCard(
                    job,
                    index = index,
                    uiState = appUiState,
                    expand = apply,
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
fun BottomNavigationBar(navController: NavHostController, modifier: Modifier = Modifier){
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        NavigationBarItem(
            selected = true,
            onClick = { navController.navigate(AppScreen.Start.name) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = null
                )
            },
            label = {
                Text(text = "Jobs")
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(AppScreen.Profile.name) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null
                )
            },
            label = {
                Text(text = "Profile")
            }
        )
    }
}

@Composable
fun JobCard(
    job: JobDetails,
    index: Int,
    uiState: AppUiState,
    expand: (Int) -> Unit,
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
                        onClick = { expand(index) },
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
fun ApplicationForm(questions: List<String>, curAnswers: List<String>, updateAnswer: (String, Int) -> Unit, next: () -> Unit, modifier: Modifier = Modifier){
    Scaffold (
        floatingActionButton = {
            Button(
                onClick = { next() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Next")
            }
        }
    ) {it ->
        LazyColumn (contentPadding = it){
            itemsIndexed(questions){ index, question ->
                TextField(
                    value = curAnswers[index],
                    onValueChange = { newValue ->
                        updateAnswer(newValue, index)
                    },
                    label = {Text(question)}
                )
            }
        }
    }

}

@Composable
fun SummaryScreen(questions: List<String>, answers: List<String>, submit: () -> Unit, modifier: Modifier = Modifier){
    Scaffold (
        floatingActionButton = {
            Button(
                onClick = { submit() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Submit")
            }
        }
    ) { it ->
        LazyColumn (contentPadding = it) {
            itemsIndexed(questions){ index, question ->
                Text("$question: ${answers[index]}")
            }
        }
    }
}

@Composable
fun ProfileScreen(){
    Text("Profile Screen")
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
            curAnswers =  listOf("Answer 1", "Answer 2", "Answer 3"),
            updateAnswer = { answer, index -> },
            next = {},
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SummaryPreview() {
    AutoApplyTheme {
        SummaryScreen(
            questions = listOf("Question 1", "Question 2", "Question 3"),
            answers =  listOf("Answer 1", "Answer 2", "Answer 3"),
            submit = { }
        )
    }
}

@Composable
fun SupabaseTest(login: () -> Unit){
    var users by remember { mutableStateOf<List<User>>(listOf()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
             users = supabase.from("users")
                .select().decodeList<User>()
        }
    }

    if(GlobalState.user == null){
        Column(){
            Text(
                "Please log in or sign up",
                modifier = Modifier.padding(8.dp)
            )
            Row {
                Button(onClick = { /*TODO*/ }) {
                    Text("Sign Up")
                }
                Button(onClick = { login() }) {
                    Text("Log In")
                }
            }
        }

    }else{
        Text("Welcome ${GlobalState.user!!.email}")
    }

}

@Composable
fun LogInScreen(submit: (String, String) -> Unit){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column{
        TextField(
            value = email,
            onValueChange = {email = it},
            label = {Text("Email")}
        )
        TextField(
            value = password,
            onValueChange = {password = it},
            label = {Text("Password")}
        )
        Button(
            modifier = Modifier.padding(8.dp),
            onClick = { submit(email, password) }
        )
        {
            Text("Log In")
        }
    }
}

@Preview
@Composable
fun DarkThemePreview(){
    AutoApplyTheme(darkTheme = true) {
        SupabaseTest(
            login = {}
        )
    }
}