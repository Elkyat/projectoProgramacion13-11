package com.example.juevesprogramacion.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.juevesprogramacion.R
import com.example.juevesprogramacion.data.local.UserSession

class RegisterActivity : AppCompatActivity() {

    private lateinit var session: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val logoRegister = findViewById<ImageView>(R.id.appLogoRegister)
        logoRegister.alpha = 0f
        logoRegister.scaleX = 0.8f
        logoRegister.scaleY = 0.8f
        logoRegister.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(800)
            .setStartDelay(200)
            .start()


        session = UserSession(this)

        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginButton = findViewById<TextView>(R.id.loginText)

        registerButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                session.saveUser(email, password)
                Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
