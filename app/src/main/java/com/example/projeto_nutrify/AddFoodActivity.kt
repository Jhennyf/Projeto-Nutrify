package com.example.projeto_nutrify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddFoodActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    // Interface
    private lateinit var etNome: EditText
    private lateinit var etCalorias: EditText
    private lateinit var etCarboidratos: EditText
    private lateinit var etProteinas: EditText
    private lateinit var etGordura: EditText
    private lateinit var etQuantidade: EditText
    private lateinit var btnSalvar: Button
    private lateinit var lvAlimentos: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        // Inicializar elementos
        etNome = findViewById(R.id.etNome)
        etCalorias = findViewById(R.id.etCalorias)
        etCarboidratos = findViewById(R.id.etCarboidratos)
        etProteinas = findViewById(R.id.etProteinas)
        etGordura = findViewById(R.id.etGordura)
        etQuantidade = findViewById(R.id.etQuantidade)
        btnSalvar = findViewById(R.id.btnSalvar)
        lvAlimentos = findViewById(R.id.lvAlimentos)

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance().getReference("alimentos")

        // Configurar botão de salvar
        btnSalvar.setOnClickListener {
            salvarAlimento()
        }

        // Listar alimentos
        listarAlimentos()
    }

    private fun salvarAlimento() {
        val nome = etNome.text.toString()
        val calorias = etCalorias.text.toString().toIntOrNull() ?: 0
        val carboidratos = etCarboidratos.text.toString().toDoubleOrNull() ?: 0.0
        val proteinas = etProteinas.text.toString().toDoubleOrNull() ?: 0.0
        val gordura = etGordura.text.toString().toDoubleOrNull() ?: 0.0
        val quantidade = etQuantidade.text.toString().toIntOrNull() ?: 0

        if (nome.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o nome do alimento.", Toast.LENGTH_SHORT).show()
            return
        }

        // Criar objeto do alimento
        val alimento = mapOf(
            "nome" to nome,
            "calorias" to calorias,
            "carboidratos" to carboidratos,
            "proteinas" to proteinas,
            "gordura" to gordura,
            "quantidade" to quantidade
        )

        // Salvar no Firebase
        val id = database.push().key!! // Gerar um ID único
        database.child(id).setValue(alimento)
            .addOnSuccessListener {
                Toast.makeText(this, "Alimento salvo com sucesso!", Toast.LENGTH_SHORT).show()
                limparCampos()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar o alimento.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun limparCampos() {
        etNome.text.clear()
        etCalorias.text.clear()
        etCarboidratos.text.clear()
        etProteinas.text.clear()
        etGordura.text.clear()
        etQuantidade.text.clear()
    }

    private fun listarAlimentos() {
        database.orderByChild("nome").get().addOnSuccessListener { dataSnapshot ->
            val alimentos = mutableListOf<String>()
            for (child in dataSnapshot.children) {
                val nome = child.child("nome").value.toString()
                val quantidade = child.child("quantidade").value.toString()
                alimentos.add("$nome - $quantidade g")
            }

            // Exibir os alimentos na ListView
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, alimentos)
            lvAlimentos.adapter = adapter
        }.addOnFailureListener {
            Toast.makeText(this, "Erro ao carregar alimentos.", Toast.LENGTH_SHORT).show()
        }
    }
}