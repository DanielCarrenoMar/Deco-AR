package com.app.homear.core.navigation
import kotlinx.serialization.Serializable
/*
* En este archivo se definen las claves que representan a cada pantalla
* */
@Serializable
object Intro

@Serializable
object Loading

@Serializable
object Login

@Serializable
object Register

@Serializable
object Tutorial

@Serializable
object Camera

@Serializable
object Catalog

@Serializable
object Profile

@Serializable
object Configuration

@Serializable
object Start

@Serializable
object Project

@Serializable
object CreateSpace

@Serializable
data class SpaceDetail(val spaceId: Int)

@Serializable
object EditProfile

@Serializable
object SpacesList

@Serializable
object CreateProject

@Serializable
data class ProjectDetail(val projectId: Int)

@Serializable
object AddProduct