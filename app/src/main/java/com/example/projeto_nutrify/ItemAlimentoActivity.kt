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
            addFoodToDinner(food)
        }
        recyclerView.adapter = foodAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchFoods()

        btnCriarAlimento.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            startActivity(intent)
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_item_alimento
    }

    private fun showFoodDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_food_list)

        val recyclerViewDialog = dialog.findViewById<RecyclerView>(R.id.recyclerViewFoodDialog)
        val btnAddFood = dialog.findViewById<Button>(R.id.btnAddFood)

        val dialogAdapter = FoodDialogAdapter(this, emptyList()) { food ->
            addFoodToDinner(food)
            dialog.dismiss()
        }
        recyclerViewDialog.adapter = dialogAdapter
        recyclerViewDialog.layoutManager = LinearLayoutManager(this)

        fetchFoodsForDialog(dialogAdapter)

        btnAddFood.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun fetchFoodsForDialog(adapter: FoodDialogAdapter) {
        firestore.collection("foods")
            .get()
            .addOnSuccessListener { result ->
                val foodList = result.toObjects(Food::class.java)
                adapter.updateFoodList(foodList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar alimentos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchFoods() {
        firestore.collection("foods")
            .get()
            .addOnSuccessListener { result ->
                val foodList = result.toObjects(Food::class.java)
                foodAdapter.updateFoodList(foodList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar alimentos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addFoodToDinner(food: Food) {
        Toast.makeText(this, "${food.nome} adicionado ao jantar", Toast.LENGTH_SHORT).show()
    }
}