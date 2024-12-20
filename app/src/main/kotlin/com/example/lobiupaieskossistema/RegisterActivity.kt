package com.example.lobiupaieskossistema

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.dao.UserDAO
import com.example.lobiupaieskossistema.models.User
import com.example.lobiupaieskossistema.utils.EncryptionUtils
import com.example.lobiupaieskossistema.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var userDAO: UserDAO
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.register)

        userDAO = UserDAO(this)

        sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        val usernameInput: EditText = findViewById(R.id.usernameInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val emailInput: EditText = findViewById(R.id.emailInput)
        val registerButton: Button = findViewById(R.id.registerButton)
        val loginLink: TextView = findViewById(R.id.loginLink)
        loginLink.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
        registerButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val email = emailInput.text.toString()

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val existingUser = userDAO.findUserByUsername(username)
            if (existingUser != null) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hashedPassword = EncryptionUtils.hashPassword(password)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val user = User(
                username = username,
                hashedPassword = hashedPassword,
                email = email,
                createdAt = currentDate
            )
            userDAO.addUser(user)
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}