package com.example.groupproject.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.R
import com.example.groupproject.model.Repository
import com.example.groupproject.model.User

class LoginActivity : AppCompatActivity() {

    private lateinit var repo: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        repo = Repository.getInstance(this)
        repo.init()

        val userField = findViewById<EditText>(R.id.usernameField)
        val passField = findViewById<EditText>(R.id.passwordField)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        val createBtn = findViewById<Button>(R.id.createButton)

        loginBtn.setOnClickListener {
            val username = userField.text.toString()
            val password = passField.text.toString()

            repo.login(username, password) { user ->
                if (user != null) {
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        createBtn.setOnClickListener {
            val newUser = User(
                username = userField.text.toString(),
                password = passField.text.toString(),
                longestRun = 0.0
            )
            repo.createAccount(newUser) { success ->
                if (success) {
                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}