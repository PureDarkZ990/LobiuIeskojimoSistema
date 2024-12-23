package com.example.lobiupaieskossistema

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.dao.UserDAO
import com.example.lobiupaieskossistema.data.UserData
import com.example.lobiupaieskossistema.utils.EncryptionUtils
import com.example.lobiupaieskossistema.utils.SessionManager

class LogInActivity : AppCompatActivity() {

    private lateinit var userDAO: UserDAO
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.login)

        UserData.initialize(this)
        userDAO = UserDAO(this)
        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        val usernameInput: EditText = findViewById(R.id.usernameInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val loginButton: Button = findViewById(R.id.loginButton)
        val registerOption: TextView = findViewById(R.id.registerOption)

        registerOption.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = userDAO.findUserByUsername(username)
            if (user == null || user.hashedPassword != EncryptionUtils.hashPassword(password)) {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            user.roleId?.let { roleId ->
                sessionManager.createLoginSession(user.username, user.id, roleId)
            }

            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
