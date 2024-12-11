package com.example.projeto_nutrify

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddFoodActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etCalorias: EditText
    private lateinit var etCarboidratos: EditText
    private lateinit var etProteinas: EditText
    private lateinit var etGordura: EditText
    private lateinit var etQuantidade: EditText
    private lateinit var btnSalvar: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var selectedCard: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        selectedCard = intent.getStringExtra("selectedCard")

        etNome = findViewById(R.id.etNome)
        etCalorias = findViewById(R.id.etCalorias)
        etCarboidratos = findViewById(R.id.etCarboidratos)
        etProteinas = findViewById(R.id.etProteinas)
        etGordura = findViewById(R.id.etGordura)
        etQuantidade = findViewById(R.id.etQuantidade)
        btnSalvar = findViewById(R.id.btnSalvar)

        btnSalvar.setOnClickListener {
            saveFood()
        }
    }

    private fun saveFood() {
        val foodName = findViewById<EditText>(R.id.etNome).text.toString()
        val calories = findViewById<EditText>(R.id.etCalorias).text.toString().toDoubleOrNull()
        val carbs = findViewById<EditText>(R.id.etCarboidratos).text.toString().toDoubleOrNull()
        val protein = findViewById<EditText>(R.id.etProteinas).text.toString().toDoubleOrNull()
        val fat = findViewById<EditText>(R.id.etGordura).text.toString().toDoubleOrNull()
        val quantity = findViewById<EditText>(R.id.etQuantidade).text.toString().toDoubleOrNull()

        if (foodName.isEmpty() || calories == null || carbs == null || protein == null || fat == null || quantity == null) {
            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (userId != null && selectedCard != null) {
            val food = hashMapOf(
                "name" to foodName,
                "calories" to calories,
                "carbs" to carbs,
                "protein" to protein,
                "fat" to fat,
                "quantity" to quantity,
                "card" to selectedCard,
                "date" to date
            )

            firestore.collection("users").document(userId).collection("foods").add(food)
                .addOnSuccessListener {
                    Toast.makeText(this, "Alimento adicionado com sucesso!", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Erro ao adicionar alimento: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}