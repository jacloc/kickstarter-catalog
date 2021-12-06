package com.locker.kickstarterapp

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.coroutineScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import coil.size.PixelSize
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.locker.kickstarterapp.model.Project
import com.locker.kickstarterapp.ui.theme.KickstarterAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val projectsViewModel by viewModels<ProjectsViewModel>()

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

@ExperimentalCoilApi
@ExperimentalFoundationApi
@Composable
fun ProjectsScreen(viewModel: ProjectsViewModel) {
    val projects = viewModel.projectsStateFlow.collectAsLazyPagingItems()
    ProjectsScreen(projects)
}

@ExperimentalCoilApi
@ExperimentalFoundationApi
@Composable
fun ProjectsScreen(projects: LazyPagingItems<Project>) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalContext.current as? Activity
    BackHandler {
        if (lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0) {
            activity?.finish()
        }
        coroutineScope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }
    if (projects.itemCount > 0) {
        LazyColumn(state = lazyListState, modifier = Modifier.padding(PaddingValues(4.dp))) {
            items(
                projects.itemCount + 1,
                { if (it >= 0 && it < projects.itemCount) projects[it]!!.id else it }) {
                if (it >= 0 && it < projects.itemCount) {
                    ProjectListItem(project = projects[it]!!)
                } else if (it >= projects.itemCount) {
                    AppendLoadStateListItem(loadState = projects.loadState.append)
                }
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
fun AppendLoadStateListItem(loadState: LoadState) {
    if (loadState is LoadState.Loading) {
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