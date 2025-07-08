package com.app.homear.data.database.dao

import androidx.room.*
import com.app.homear.data.database.entity.ProjectEntity

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects")
    suspend fun getAllProjects(): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Int): ProjectEntity?

    @Query("SELECT * FROM projects WHERE idUser = :userId")
    suspend fun getProjectsByUserId(userId: String): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE spaceId = :spaceId")
    suspend fun getProjectsBySpaceId(spaceId: Int): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE isCompleted = :isCompleted")
    suspend fun getProjectsByCompletionStatus(isCompleted: Boolean): List<ProjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectList(projectList: List<ProjectEntity>)

    @Update
    suspend fun updateProject(project: ProjectEntity): Int

    @Delete
    suspend fun deleteProject(project: ProjectEntity): Int

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Int): Int

    @Query("DELETE FROM projects WHERE idUser = :userId")
    suspend fun deleteProjectsByUserId(userId: String): Int

    @Query("DELETE FROM projects")
    suspend fun deleteAllProjects(): Int

    @Query("UPDATE projects SET name = :name, description = :description, lastModified = :lastModified WHERE id = :id")
    suspend fun updateProjectDetails(
        id: Int,
        name: String,
        description: String,
        lastModified: String
    ): Int

    @Query("UPDATE projects SET isCompleted = :isCompleted, lastModified = :lastModified WHERE id = :id")
    suspend fun updateProjectCompletionStatus(
        id: Int,
        isCompleted: Boolean,
        lastModified: String
    ): Int

    @Query("UPDATE projects SET furnitureIds = :furnitureIds, lastModified = :lastModified WHERE id = :id")
    suspend fun updateProjectFurniture(id: Int, furnitureIds: String, lastModified: String): Int
}