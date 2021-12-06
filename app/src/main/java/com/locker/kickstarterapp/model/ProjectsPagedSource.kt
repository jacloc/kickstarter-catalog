package com.locker.kickstarterapp.model

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay

class ProjectsPagedSource(private val projectsService: IProjectService, private val searchTerm: String) : PagingSource<Int, Project>() {
    companion object {
        private val TAG = ProjectsPagedSource::class.java.simpleName
        private const val RATE_LIMIT_PER_PAGE = 3000
    }

    private var lastResponseTime = 0L
    override fun getRefreshKey(state: PagingState<Int, Project>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Project> {
        val page = params.key ?: 1

        return try {
            if (System.currentTimeMillis() - lastResponseTime < RATE_LIMIT_PER_PAGE) {
                delay(RATE_LIMIT_PER_PAGE - (System.currentTimeMillis() - lastResponseTime))
            }

            val response = projectsService.fetchProjects(searchTerm, page = page)
            lastResponseTime = System.currentTimeMillis()
            LoadResult.Page(
                data = response.projects,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (response.hasMore) page + 1 else null
            )
        } catch (throwable: Throwable) {
            Log.e(TAG, "load: $throwable")
            LoadResult.Error(throwable)
        }
    }
}