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
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.register)

        userDAO = UserDAO(this)

        val usernameInput: EditText = findViewById(R.id.usernameInput)
        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val repeatPasswordInput: EditText = findViewById(R.id.repeatPasswordInput)
        val registerButton: Button = findViewById(R.id.registerButton)
        val loginLink: TextView = findViewById(R.id.loginLink)

        // Navigate to LogInActivity
        loginLink.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }

        // Handle register button click
        registerButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val repeatPassword = repeatPasswordInput.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
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
                bio = "",
                createdAt = currentDate
            )

            userDAO.addUser(user)
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        )
        return emailPattern.matcher(email).matches()
    }
}
