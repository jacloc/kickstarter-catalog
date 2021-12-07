package com.locker.kickstarterapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.locker.kickstarterapp.model.IProjectRepository
import com.locker.kickstarterapp.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectRepository: IProjectRepository
) : ViewModel() {
    companion object {
        private val TAG = ProjectsViewModel::class.java.simpleName
    }

    private var projectsJob: Job? = null

    private val _projectsStateFlow: MutableStateFlow<PagingData<Project>> =
        MutableStateFlow(PagingData.empty())
    val projectsStateFlow
        get() = _projectsStateFlow.asStateFlow()

    init {
        searchProjects("chess")
    }

    fun searchProjects(term: String) {
        _projectsStateFlow.value = PagingData.empty()
        projectsJob?.cancel()
        projectsJob = viewModelScope.launch(Dispatchers.IO) {
            projectRepository.getProjects(term).cachedIn(this).collect {
                _projectsStateFlow.value = it
            }
        }
    }
}