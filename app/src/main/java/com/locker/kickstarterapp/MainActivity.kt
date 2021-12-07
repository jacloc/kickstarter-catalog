package com.locker.kickstarterapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.locker.kickstarterapp.model.Project
import com.locker.kickstarterapp.ui.theme.KickstarterAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val projectsViewModel by viewModels<ProjectsViewModel>()

    @ExperimentalAnimationApi
    @ExperimentalCoilApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KickstarterAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ProjectsScreen(viewModel = projectsViewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalFoundationApi
@Composable
fun ProjectsScreen(viewModel: ProjectsViewModel) {
    Column {
        SearchBar(viewModel)

        val projects = viewModel.projectsStateFlow.collectAsLazyPagingItems()
        ProjectsScreen(projects)
    }
}

@Composable
fun SearchBar(viewModel: ProjectsViewModel) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    TextField(
        value = text,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
        ),
        leadingIcon = { Icon(Icons.Filled.Search, "search") },
        trailingIcon = {
            IconButton(onClick = { focusRequester.requestFocus().also { text = "" } }) {
                Icon(Icons.Filled.Clear, "clear")
            }
        },
        label = { Text("Search Projects") },
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth()
            .padding(PaddingValues(top = 8.dp, start = 8.dp, end = 8.dp)),
        onValueChange = { text = it },
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }, onSearch = {
            viewModel.searchProjects(text).also {
                focusManager.clearFocus()
            }
        }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}

@ExperimentalCoilApi
@ExperimentalFoundationApi
@Composable
fun ProjectsScreen(projects: LazyPagingItems<Project>) {
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalContext.current as? Activity
    val lazyListState = rememberLazyListState()

    BackHandler {
        if (lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0) {
            activity?.finish()
        }
        coroutineScope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }

    LazyColumn(state = lazyListState, modifier = Modifier.padding(PaddingValues(4.dp))) {
        items(
            projects.itemCount + 1,
            { if (it >= 0 && it < projects.itemCount) projects[it]!!.id else it }) {
            if (it >= 0 && it < projects.itemCount) {
                ProjectListItem(project = projects[it]!!)
            } else if (it >= projects.itemCount) {
                LoadStateListItem(projects.loadState)
            }
        }
    }
}

@Composable
fun ProjectCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        content = content
    )
}

@ExperimentalCoilApi
@Composable
fun ProjectListItem(project: Project) {
    ProjectCard {
        Column {
//            ProjectImage(imageUrl = project.photo.fullImageUrl,
//                modifier = Modifier.fillMaxWidth()
//                .padding(PaddingValues(bottom = 16.dp)))
            val painter = rememberImagePainter(project.photo.image1536x864Url, builder = {
                //transformations(CircleCropTransformation())
                //size(PixelSize(1536, 864))
                scale(Scale.FIT)
            })

            Image(
                painter = painter,
                contentDescription = "Project Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(LocalDensity.current) { 576.toDp() }),
                contentScale = ContentScale.FillWidth
            )
            Text(
                text = project.name,
                modifier = Modifier.padding(PaddingValues(8.dp)),
                style = MaterialTheme.typography.h6,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Text(
                text = project.blurb,
                modifier = Modifier.padding(PaddingValues(8.dp)),
                style = MaterialTheme.typography.body1,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }
    }
}

@Composable
fun LoadStateListItem(combinedLoadStates: CombinedLoadStates) {
    if (combinedLoadStates.append is LoadState.Loading ||
        combinedLoadStates.refresh is LoadState.Loading
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { 576.toDp() }),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KickstarterAppTheme {
        Greeting("Android")
    }
}