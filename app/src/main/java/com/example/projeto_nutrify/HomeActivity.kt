package com.example.projeto_nutrify

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        val infoTextView = findViewById<TextView>(R.id.infoTextView)

        // Verifica se o usuário está logado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Carregar dados do Firestore
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        val dateOfBirth = document.getString("dateOfBirth")
                        val imc = document.getDouble("imc")
                        val tmb = document.getDouble("tmb")
                        val weight = document.getDouble("weight")
                        val height = document.getDouble("height")
                        val goal = document.getString("goal")
                        val calorieGoal = document.getDouble("calorieGoal")
                        val carbsGrams = document.getDouble("carbsGrams")
                        val proteinGrams = document.getDouble("proteinGrams")
                        val fatGrams = document.getDouble("fatGrams")

                        val decimalFormat = DecimalFormat("#")

                        val carbsFormatted = decimalFormat.format(carbsGrams)
                        val proteinFormatted = decimalFormat.format(proteinGrams)
                        val fatFormatted = decimalFormat.format(fatGrams)

                        // Exibir os dados na tela
                        welcomeTextView.text = "Bem-vindo, $name!"
                        infoTextView.text = """
                        Nome: $name
                        Data de Nascimento: $dateOfBirth
                        Peso: $weight kg
                        Altura: $height cm
                        IMC: ${imc?.toString()?.take(5)}
                        TMB: ${tmb?.toString()?.take(6)}
                        Objetivo: $goal
                        Meta de Calorias: ${calorieGoal?.toString()?.take(6)}
                        Carboidratos: ${carbsFormatted?.toString()?.take(6)} g
                        Proteínas: ${proteinFormatted?.toString()?.take(6)} g
                        Gorduras: ${fatFormatted?.toString()?.take(6)} g
                        """.trimIndent()

                    } else {
                        Toast.makeText(this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
        }
    }
}

