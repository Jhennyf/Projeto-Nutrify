package com.example.projeto_nutrify

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class FoodDialog(context: Context, private val onFoodSelected: (Food) -> Unit) : Dialog(context) {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_food_list)

        recyclerView = findViewById(R.id.recyclerViewFoodDialog)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val dialogAdapter = MacroAdapter(context, emptyList()) { food ->
            onFoodSelected(food)
            dismiss()
        }
        recyclerView.adapter = dialogAdapter

        val btnAddFood = findViewById<Button>(R.id.btnAddFood)
        btnAddFood.setOnClickListener {
            val intent = Intent(context, AddFoodActivity::class.java)
            context.startActivity(intent)
            dismiss()
        }

        fetchFoodForDialog(dialogAdapter)
    }

    private fun fetchFoodForDialog(adapter: MacroAdapter) {
        firestore.collection("foods")
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
                Toast.makeText(context, "Erro ao carregar alimentos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}