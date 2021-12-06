package com.locker.kickstarterapp

import com.locker.kickstarterapp.di.BackendModule
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ProjectServiceTest {

    @Test
    fun `check project service search`() {
        val projectService = BackendModule.provideKickstarterService()
        runBlocking {
            val response = projectService.fetchProjects("chess")
            assertNotNull(response)
            assertTrue(response.hasMore)
            assertTrue(response.projects.isNotEmpty())
        }
    }
}