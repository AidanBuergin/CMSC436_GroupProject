package com.example.groupproject.model

/**
 * This class handles all communication with the remote database (Firebase).
 * The current implementation contains placeholder logic.
 *
 * TODO: Replace the placeholder logic with actual Firebase Firestore calls.
 * 1.  Initialize FirebaseFirestore.
 * 2.  Implement the 'login' function to authenticate users.
 * 3.  Implement the 'createAccount' function to add new users.
 * 4.  Implement the 'updateLongestRun' function to update user data.
 * 5.  Implement the 'getAllUsers' function to fetch leaderboard data.
 */
class RemoteDatabase {

    fun login(username: String, password: String, callback: (User?) -> Unit) {
        // Placeholder: Simulate a network call and successful login
        // In the real implementation, you would query Firestore
        if (username.isNotEmpty() && password.isNotEmpty()) {
            // Simulate finding a user.
            callback(User(username, password, 5.0))
        } else {
            callback(null)
        }
    }

    fun createAccount(user: User, callback: (Boolean) -> Unit) {
        // Placeholder: Simulate creating an account
        // In the real implementation, you would add a new document to Firestore
        callback(true)
    }

    fun updateLongestRun(username: String, newDistance: Double) {
        // Placeholder: This would update a user's record in Firestore
    }

    fun getAllUsers(callback: (List<User>) -> Unit) {
        // Placeholder: Return a dummy list for the leaderboard
        // In the real implementation, you would query the 'users' collection
        val dummyUsers = listOf(
            User("runner1", "", 12.5),
            User("fast_feet", "", 11.2),
            User("marathoner", "", 10.8)
        )
        callback(dummyUsers)
    }
}
