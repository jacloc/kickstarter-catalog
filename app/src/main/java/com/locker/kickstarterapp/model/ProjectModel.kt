package com.locker.kickstarterapp.model

import com.google.gson.annotations.SerializedName

data class ProjectResponse(
    @SerializedName("projects") val projects: List<Project>,
    @SerializedName("suggestion") val suggestion: String,
    @SerializedName("has_more") val hasMore: Boolean
) {
    companion object {
        val EMPTY = ProjectResponse(emptyList(), "", false)
    }
}

data class Project(
    @SerializedName("id") val id: Int,
    @SerializedName("photo") val photo: ProjectPhoto,
    @SerializedName("name") val name: String,
    @SerializedName("blurb") val blurb: String,
    @SerializedName("goal") val goalAmount: Float,
    @SerializedName("pledged") val pledgedAmount: Float,
    @SerializedName("backers_count") val backers: Int

)

data class ProjectPhoto(
    @SerializedName("key") val key: String,
    @SerializedName("full") val fullImageUrl: String,
    @SerializedName("little") val littleImageUrl: String,
    @SerializedName("small") val smallImageUrl: String,
    @SerializedName("1024x576") val image1024x576Url: String,
    @SerializedName("1536x864") val image1536x864Url: String
)

enum class ProjectSortOrder(private val serializedName: String) {
    NEWEST("newest");

    override fun toString(): String {
        return serializedName
    }
}