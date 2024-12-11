package com.example.projeto_nutrify

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddFoodActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        firestore = FirebaseFirestore.getInstance()

        val etNome = findViewById<EditText>(R.id.etNome)
        val etCalorias = findViewById<EditText>(R.id.etCalorias)
        val etCarboidratos = findViewById<EditText>(R.id.etCarboidratos)
        val etProteinas = findViewById<EditText>(R.id.etProteinas)
        val etGordura = findViewById<EditText>(R.id.etGordura)
        val etQuantidade = findViewById<EditText>(R.id.etQuantidade)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)

        btnSalvar.setOnClickListener {
            val nome = etNome.text.toString()
            val calorias = etCalorias.text.toString().toDoubleOrNull()
            val carboidratos = etCarboidratos.text.toString().toDoubleOrNull()
            val proteinas = etProteinas.text.toString().toDoubleOrNull()
            val gordura = etGordura.text.toString().toDoubleOrNull()
            val quantidade = etQuantidade.text.toString().toDoubleOrNull()

            if (nome.isEmpty() || calorias == null || carboidratos == null || proteinas == null || gordura == null || quantidade == null) {
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val food = hashMapOf(
                "nome" to nome,
                "calorias" to calorias,
                "carboidratos" to carboidratos,
                "proteinas" to proteinas,
                "gordura" to gordura,
                "quantidade" to quantidade
            )

            firestore.collection("foods").add(food)
                .addOnSuccessListener {
                    Toast.makeText(this, "Alimento salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao salvar alimento: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}