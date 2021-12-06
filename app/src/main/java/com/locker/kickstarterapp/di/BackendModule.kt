package com.locker.kickstarterapp.di

import android.content.Context
import com.locker.kickstarterapp.model.IProjectRepository
import com.locker.kickstarterapp.model.IProjectService
import com.locker.kickstarterapp.model.ProjectRepository
import com.locker.kickstarterapp.model.fake.FakeProjectService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object BackendModule {
    @Provides
    fun provideKickstarterService(): IProjectService = Retrofit.Builder()
        .baseUrl("https://www.kickstarter.com/projects/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build())
        .build()
        .create(IProjectService::class.java)

//    @Provides
//    fun provideFakeProjectService(@ApplicationContext context: Context): IProjectService = FakeProjectService(context)

    @Provides
    fun provideProjectRepository(projectService: IProjectService): IProjectRepository = ProjectRepository(projectService)
}