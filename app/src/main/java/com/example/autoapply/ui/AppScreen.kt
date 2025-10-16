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
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun JobsApp(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel()
){
    val appUiState by appViewModel.uiState.collectAsState()
    Scaffold (
        topBar = {
            TopAppBar()
        }
    ) { it ->
        LazyColumn(contentPadding = it){
            itemsIndexed(Datasource().loadJobs()){ index, job ->
                JobCard(
                    job,
                    index = index,
                    {appViewModel.updateSelectedJob(job.jobTitleResourceId)},
                    {appViewModel.cancelSelectedJob()},
                    {appViewModel.submitApplication(job.jobTitleResourceId)},
                    modifier = modifier.padding(8.dp),
                    uiState = appUiState
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
    expand: () -> Unit,
    cancel: () -> Unit,
    submit: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: AppUiState
) {
    val expanded = uiState.selectedJobId == job.jobTitleResourceId && uiState.jobSelected
    val submitted = job.jobTitleResourceId in uiState.submittedApplications
    val color = animateColorAsState(
        targetValue = if (submitted) MaterialTheme.colorScheme.tertiaryContainer
        else if (uiState.selectedJobId == job.jobTitleResourceId) MaterialTheme.colorScheme.primaryContainer
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
            if(expanded && !submitted) {
                ApplicationForm(
                    index = index,
                    submit = { submit() },
                    cancel = { cancel() },
                    modifier = Modifier
                        .padding(8.dp)
                )
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
fun ApplicationForm(index: Int, submit: () -> Unit, cancel: () -> Unit, modifier: Modifier = Modifier){
    var text by remember { mutableStateOf("") }
    Row(
        modifier = modifier.semantics{
            contentDescription = "Form $index"
        }
    ){
        TextField(
            value = text,
            onValueChange = { text = it },
            label = {Text("First Name")},
            modifier = modifier
                .weight(1f)
                .clip(MaterialTheme.shapes.small)
        )
        Button(
            onClick = submit,
            modifier = modifier.weight(1f)
        ) {
            Text("Submit")
        }
        OutlinedButton(
            onClick = cancel,
            modifier = modifier.weight(1f)
        ) {
            Text("Cancel")
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

//@Preview
//@Composable
//fun DarkThemePreview(){
//    AutoApplyTheme(darkTheme = true) {
//        JobsApp()
//    }
//}