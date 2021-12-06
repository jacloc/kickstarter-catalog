package com.locker.kickstarterapp.model

import retrofit2.http.GET
import retrofit2.http.Query

interface IProjectService {
    @GET("search.json?search=&")
    suspend fun fetchProjects(@Query("term") term: String,
                              @Query("sort") sortBy: String = "newest",
                              @Query("page") page: Int = 0) : ProjectResponse
}