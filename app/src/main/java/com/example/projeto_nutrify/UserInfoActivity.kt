package com.example.projeto_nutrify

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserInfoActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val editTextName = findViewById<EditText>(R.id.nameEditText)
        val editTextWeight = findViewById<EditText>(R.id.weightEditText)
        val editTextHeight = findViewById<EditText>(R.id.heightEditText)
        val editTextDateOfBirth = findViewById<EditText>(R.id.dateOfBirthEditText)
        val spinnerGender = findViewById<Spinner>(R.id.spinnerGender)
        val spinnerGoal = findViewById<Spinner>(R.id.spinnerGoal)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = editTextName.text.toString()
            val weight = editTextWeight.text.toString().toDoubleOrNull()
            val height = editTextHeight.text.toString().toDoubleOrNull()
            val dateOfBirth = editTextDateOfBirth.text.toString()
            val gender = spinnerGender.selectedItem.toString()
            val goal = spinnerGoal.selectedItem.toString()

            if (name.isEmpty() || weight == null || height == null || dateOfBirth.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tmb = if (gender == "Masculino") {
                88.36 + (13.4 * weight) + (4.8 * height) - (5.7 * calculateAge(dateOfBirth))
            } else {
                447.6 + (9.2 * weight) + (3.1 * height) - (4.3 * calculateAge(dateOfBirth))
            }

            // Calcular meta de calorias com base no objetivo
            val calorieGoal = when (goal) {
                "Emagrecer" -> tmb - 300
                "Ganhar Massa" -> tmb + 300
                "Manter Peso" -> tmb
                else -> tmb
            }

            val userId = auth.currentUser?.uid
            if (userId != null) {
                val user = hashMapOf(
                    "name" to name,
                    "weight" to weight,
                    "height" to height,
                    "dateOfBirth" to dateOfBirth,
                    "gender" to gender,
                    "tmb" to tmb,
                    "imc" to weight / ((height / 100) * (height / 100)),
                    "goal" to goal,
                    "calorieGoal" to calorieGoal
                )

                firestore.collection("users").document(userId).set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun calculateAge(dateOfBirth: String): Int {
        val parts = dateOfBirth.split("/")
        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()

        val today = java.util.Calendar.getInstance()
        var age = today.get(java.util.Calendar.YEAR) - year
        if (today.get(java.util.Calendar.MONTH) + 1 < month ||
            (today.get(java.util.Calendar.MONTH) + 1 == month && today.get(java.util.Calendar.DAY_OF_MONTH) < day)) {
            age--
        }
        return age
    }
}
