package com.example.juevesprogramacion.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.juevesprogramacion.R
import com.example.juevesprogramacion.data.local.UserSession
import com.example.juevesprogramacion.ui.news.NewsActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var session: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val logo = findViewById<ImageView>(R.id.appLogo)
        logo.alpha = 0f
        logo.scaleX = 0.8f
        logo.scaleY = 0.8f
        logo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(800)
            .setStartDelay(200)
            .start()



        session = UserSession(this)

        if (session.isLoggedIn()) {
            startActivity(Intent(this, NewsActivity::class.java))
            finish()
            return
        }

        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<TextView>(R.id.registerText)
        val guestButton = findViewById<Button>(R.id.guestButton)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val savedEmail = session.getEmail()
            val savedPassword = session.getPassword()

            if (email == savedEmail && password == savedPassword) {
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                session.setLoggedIn(true)
                startActivity(Intent(this, NewsActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        guestButton.setOnClickListener {
            session.setGuestMode()
            Toast.makeText(this, "Entraste como invitado", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, NewsActivity::class.java))
            finish()
        }
    }
}
