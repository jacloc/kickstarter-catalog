package com.locker.kickstarterapp.model

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProjectRepository @Inject constructor(private val projectService: IProjectService) : IProjectRepository {

    override fun getProjects(searchTerm: String, sortBy: ProjectSortOrder): Flow<PagingData<Project>> =
        Pager(PagingConfig(pageSize = 2)) {
            ProjectsPagedSource(projectService, searchTerm)
        }.flow
}