package com.example.projeto_nutrify

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MacroActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var lunchRecyclerView: RecyclerView
    private lateinit var dinnerRecyclerView: RecyclerView
    private lateinit var afternoonRecyclerView: RecyclerView

    private lateinit var lunchAdapter: MacroAdapter
    private lateinit var dinnerAdapter: MacroAdapter
    private lateinit var afternoonAdapter: MacroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_macro)

        firestore = FirebaseFirestore.getInstance()

        lunchRecyclerView = findViewById(R.id.lunchFoodRecyclerView)
        dinnerRecyclerView = findViewById(R.id.dinnerFoodRecyclerView)
        afternoonRecyclerView = findViewById(R.id.afternoonFoodRecyclerView)

        lunchAdapter = MacroAdapter(this, emptyList()) { /* Implementação do onFoodSelected */ }
        dinnerAdapter = MacroAdapter(this, emptyList()) { /* Implementação do onFoodSelected */ }
        afternoonAdapter = MacroAdapter(this, emptyList()) { /* Implementação do onFoodSelected */ }

        lunchRecyclerView.layoutManager = LinearLayoutManager(this)
        dinnerRecyclerView.layoutManager = LinearLayoutManager(this)
        afternoonRecyclerView.layoutManager = LinearLayoutManager(this)

        lunchRecyclerView.adapter = lunchAdapter
        dinnerRecyclerView.adapter = dinnerAdapter
        afternoonRecyclerView.adapter = afternoonAdapter

        findViewById<Button>(R.id.addFoodLunchButton).setOnClickListener {
            addFoodToMeal("lunch")
        }

        findViewById<Button>(R.id.addFoodDinnerButton).setOnClickListener {
            addFoodToMeal("dinner")
        }

        findViewById<Button>(R.id.addFoodAfternoonButton).setOnClickListener {
            addFoodToMeal("afternoon")
        }

        fetchFoods()
    }

    private fun addFoodToMeal(meal: String) {
        val dialog = FoodDialog(this) { food ->
            saveFoodToFirestore(meal, food)
        }
        dialog.show()
    }

    private fun saveFoodToFirestore(meal: String, food: Food) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection(meal).add(food)
            .addOnSuccessListener {
                fetchFoods()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao adicionar alimento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchFoods() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        fetchFoodsForMeal(userId, "lunch", lunchAdapter)
        fetchFoodsForMeal(userId, "dinner", dinnerAdapter)
        fetchFoodsForMeal(userId, "afternoon", afternoonAdapter)
    }

    private fun fetchFoodsForMeal(userId: String, meal: String, adapter: MacroAdapter) {
        firestore.collection("users").document(userId).collection(meal).get()
            .addOnSuccessListener { result ->
                val foodList = result.map { document ->
                    document.toObject(Food::class.java)
                }
                adapter.updateFoodList(foodList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar alimentos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}