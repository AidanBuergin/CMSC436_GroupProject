package com.example.groupproject.model

import android.content.Context

class Repository constructor(context: Context) {

    private val remote = RemoteDatabase()
    val local = LocalStorage(context)
    var user: User? = null
        private set

    fun init() {
        val loggedInUser = local.getLoggedInUser()
        if (loggedInUser != null) {
            login(loggedInUser, "") { user ->
                // User is already logged in, so we don't need to do anything here
            }
        }
    }

    fun login(username: String, password: String, callback: (User?) -> Unit) {
        remote.login(username, password) {
            user = it
            if (it != null) {
                local.saveLoggedInUser(it.username)
            }
            callback(it)
        }
    }

    fun createAccount(user: User, callback: (Boolean) -> Unit) {
        remote.createAccount(user, callback)
    }

    fun updateUser(user: User) {
        this.user = user
    }

    fun updateLongestRun(username: String, distance: Double) {
        if (distance > (user?.longestRun ?: 0.0)) {
            remote.updateLongestRun(username, distance)
        }
    }

    fun getAllUsers(callback: (List<User>) -> Unit) {
        remote.getAllUsers(callback)
    }

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(context: Context): Repository {
            return INSTANCE ?: synchronized(this) {
                val instance = Repository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}