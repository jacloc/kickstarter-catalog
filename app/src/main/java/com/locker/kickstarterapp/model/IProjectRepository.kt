package com.locker.kickstarterapp.model

import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface IProjectRepository {
    fun getProjects(searchTerm: String, sortBy: ProjectSortOrder = ProjectSortOrder.NEWEST) : Flow<PagingData<Project>>
}