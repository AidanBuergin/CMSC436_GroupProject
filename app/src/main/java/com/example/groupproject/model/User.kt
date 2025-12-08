package com.example.groupproject.model

data class User(
    val username: String = "",
    val password: String = "",
    var longestRun: Double = 0.0
)