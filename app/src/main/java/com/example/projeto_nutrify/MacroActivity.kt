package com.example.projeto_nutrify

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

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
            showFoodDialog("Café da Tarde")
        }

        addFoodLunchButton.setOnClickListener {
            showFoodDialog("Almoço")
        }

        addFoodDinnerButton.setOnClickListener {
            showFoodDialog("Janta")
        }

        loadFoods()
    }

    private fun loadFoods() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (userId != null) {
            firestore.collection("users").document(userId).collection("foods")
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val food = document.toObject(Food::class.java)
                        // Adicionar o alimento ao card correspondente
                        addFoodToCard(food)
                    }
                }
        }
    }

    private fun addFoodToCard(food: Food) {
        when (food.card) {
            "Café da Tarde" -> {
                findViewById<TextView>(R.id.afternoonFoodArray).append("${food.name}\n")
            }
            "Almoço" -> {
                findViewById<TextView>(R.id.lunchFoodArray).append("${food.name}\n")
            }
            "Janta" -> {
                findViewById<TextView>(R.id.dinnerFoodArray).append("${food.name}\n")
            }
        }
    }

    private fun showFoodDialog(selectedCard: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_food_list)

        val recyclerViewDialog = dialog.findViewById<RecyclerView>(R.id.recyclerViewFoodDialog)
        val btnAddFood = dialog.findViewById<Button>(R.id.btnAddFood)

        val dialogAdapter = FoodAdapter(this, emptyList(), onAddFoodClickListener = { food ->
            addFoodToCard(food.copy(card = selectedCard))
            dialog.dismiss()
        })
        recyclerViewDialog.adapter = dialogAdapter
        recyclerViewDialog.layoutManager = LinearLayoutManager(this)

        fetchFoodForDialog(dialogAdapter)

        btnAddFood.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            intent.putExtra("selectedCard", selectedCard)
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
                        protein = document.getDouble("proteinas") ?: 0.0
                    )
                }
                adapter.updateFoodList(foodList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar alimentos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}