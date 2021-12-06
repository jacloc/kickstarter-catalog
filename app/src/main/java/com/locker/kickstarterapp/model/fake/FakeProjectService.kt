package com.locker.kickstarterapp.model.fake

import android.content.Context
import com.locker.kickstarterapp.model.IProjectService
import com.locker.kickstarterapp.model.ProjectResponse
import kotlinx.coroutines.delay

class FakeProjectService(private val context: Context) : IProjectService {
    override suspend fun fetchProjects(term: String, sortBy: String, page: Int): ProjectResponse {
        delay(1000)
        if (page < 0 || page >= 5) {
            return ProjectResponse.EMPTY
        }
        return context.loadJsonFromAssets("projects$page.json", ProjectResponse::class.java) ?: ProjectResponse.EMPTY
    }
}