package com.example.projeto_nutrify

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ItemAlimentoActivity : BaseActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnCriarAlimento: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recyclerViewFood)
        btnCriarAlimento = findViewById(R.id.btnCriarAlimento)

        foodAdapter = FoodAdapter(this, emptyList()) { food ->
            // Handle add food button click
            addFoodToDinner(food)
        }
        recyclerView.adapter = foodAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchFoodFromFirestore()

        btnCriarAlimento.setOnClickListener {
            showFoodDialog()
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_item_alimento
    }

    private fun fetchFoodFromFirestore() {
        firestore.collection("food_entries")
            .get()
            .addOnSuccessListener { result ->
                val foodList = result.map { document ->
                    Food(
                        name = document.getString("nome") ?: "",
                        calories = document.getDouble("calorias") ?: 0.0,
                        carbs = document.getDouble("carboidratos") ?: 0.0,
                        fat = document.getDouble("gordura") ?: 0.0,
                        protein = document.getDouble("proteinas") ?: 0.0,
                        quantity = document.getDouble("quantidade") ?: 0.0,
                        card = document.getString("card") ?: "",
                        date = document.getString("date") ?: ""
                    )
                }
                foodAdapter.updateFoodList(foodList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar alimentos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showFoodDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_food_list)

        val recyclerViewDialog = dialog.findViewById<RecyclerView>(R.id.recyclerViewFoodDialog)
        val btnAddFood = dialog.findViewById<Button>(R.id.btnAddFood)

        val dialogAdapter = FoodAdapter(this, emptyList()) { food ->
            // Handle add food button click in dialog
            addFoodToDinner(food)
            dialog.dismiss()
        }
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
                        name = document.getString("nome") ?: "",
                        calories = document.getDouble("calorias") ?: 0.0,
                        carbs = document.getDouble("carboidratos") ?: 0.0,
                        fat = document.getDouble("gordura") ?: 0.0,
                        protein = document.getDouble("proteinas") ?: 0.0,
                        quantity = document.getDouble("quantidade") ?: 0.0,
                        card = document.getString("card") ?: "",
                        date = document.getString("date") ?: ""
                    )
                }
                adapter.updateFoodList(foodList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar alimentos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addFoodToDinner(food: Food) {
        // Implement the logic to add the selected food to the dinner list
        Toast.makeText(this, "${food.name} adicionado ao jantar", Toast.LENGTH_SHORT).show()
    }
}