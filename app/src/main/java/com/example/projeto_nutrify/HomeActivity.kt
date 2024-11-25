package com.example.projeto_nutrify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        auth = FirebaseAuth.getInstance()

        val emailTextView = findViewById<TextView>(R.id.emailTextView)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        // Receber o e-mail passado pela MainActivity
        val userEmail = intent.getStringExtra("email")
        emailTextView.text = "Bem-vindo, $userEmail!"

        // Botão de logout
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
