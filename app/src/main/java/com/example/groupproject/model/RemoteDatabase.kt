package com.example.groupproject.model

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Handler
import android.os.Looper

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
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun login(username: String, password: String, callback: (User?) -> Unit) {
        usersCollection.document(username).get().addOnSuccessListener { doc ->
            if(!doc.exists()){
                callback(null)
                return@addOnSuccessListener
            }
            val storedPassword = doc.getString("password")
            val longestRun = doc.getDouble("longestRun") ?: 0.0
            if(storedPassword == password){
                callback(User(username,password,longestRun))
            } else {
                callback(null)
            }
        }.addOnFailureListener { callback(null) }
    }

    fun createAccount(user: User, callback: (Boolean) -> Unit) {
        val userMap = mapOf("password" to user.password, "longestRun" to user.longestRun)
        usersCollection.document(user.username).set(userMap).addOnSuccessListener { callback(true) }.addOnFailureListener { callback(false) }
    }

    fun updateLongestRun(username: String, newDistance: Double) {
        usersCollection.document(username).update("longestRun", newDistance)
    }

    fun getAllUsers(callback: (List<User>) -> Unit) {
        // Placeholder: Return a dummy list for the leaderboard
        usersCollection.orderBy("longestRun", Query.Direction.DESCENDING).get().addOnSuccessListener { querySnapshot ->
            val list = mutableListOf<User>()
            for(doc in querySnapshot.documents){
                val username = doc.id
                val password = doc.getString("password") ?: ""
                val longestRun = doc.getDouble("longestRun") ?: 0.0

                list.add(User(username, password, longestRun))
            }
            callback(list)
        }.addOnFailureListener { callback(emptyList()) }
    }
}
