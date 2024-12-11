package com.example.projeto_nutrify

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MacroActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_macro)
        firestore = FirebaseFirestore.getInstance()

        val addFoodAfternoonButton = findViewById<Button>(R.id.addFoodAfternoonButton)
        val addFoodLunchButton = findViewById<Button>(R.id.addFoodLunchButton)
        val addFoodDinnerButton = findViewById<Button>(R.id.addFoodDinnerButton)

        addFoodAfternoonButton.setOnClickListener {
            showFoodDialog()
        }

        addFoodLunchButton.setOnClickListener {
            showFoodDialog()
        }

        addFoodDinnerButton.setOnClickListener {
            showFoodDialog()
        }

    }

    private fun showFoodDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_food_list)

        val recyclerViewDialog = dialog.findViewById<RecyclerView>(R.id.recyclerViewFoodDialog)
        val btnAddFood = dialog.findViewById<Button>(R.id.btnAddFood)

        val dialogAdapter = FoodAdapter(this, emptyList()) { food -> addFoodToDinner(food) }
        recyclerViewDialog.adapter = dialogAdapter

        recyclerViewDialog.layoutManager = LinearLayoutManager(this)

        fetchFoodForDialog(dialogAdapter)

        btnAddFood.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun fetchFoodForDialog(adapter: FoodAdapter) {
        firestore.collection("food_entries")
            .get()
            .addOnSuccessListener { result ->
                val foodList = result.map { document ->
                    Food(
                        nome = document.getString("nome") ?: "",
                        calorias = (document.getDouble("calorias") ?: 0.0).toInt(),
                        carboidratos = (document.getDouble("carboidratos") ?: 0.0).toInt(),
                        gordura = (document.getDouble("gordura") ?: 0.0).toInt(),
                        proteinas = (document.getDouble("proteinas") ?: 0.0).toInt()
                    )
                }
                adapter.updateFoodList(foodList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar alimentos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addFoodToDinner(food: Food) {
        // Implementação da função para adicionar comida ao jantar
        Toast.makeText(this, "Comida adicionada ao jantar: ${food.nome}", Toast.LENGTH_SHORT).show()
    }


}