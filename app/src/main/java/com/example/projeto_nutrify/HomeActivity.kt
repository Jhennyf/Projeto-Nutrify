package com.example.projeto_nutrify

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class HomeActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val totalCaloriesTextView = findViewById<TextView>(R.id.totalCaloriesTextView)
        val remainingCaloriesValueTextView = findViewById<TextView>(R.id.remainingCaloriesValueTextView)
        val proteinValue = findViewById<TextView>(R.id.proteinValue)
        val proteinCalories = findViewById<TextView>(R.id.proteinCalories)
        val fatValue = findViewById<TextView>(R.id.fatValue)
        val fatCalories = findViewById<TextView>(R.id.fatCalories)
        val carbsValue = findViewById<TextView>(R.id.carbsValue)
        val carbsCalories = findViewById<TextView>(R.id.carbsCalories)

        val distribuicaoRefeicoesTextView = findViewById<TextView>(R.id.distribuicaoRefeicoesTextView)
        distribuicaoRefeicoesTextView.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            startActivity(intent)
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val calorieGoal = document.getDouble("calorieGoal") ?: 0.0
                        val carbsGrams = document.getDouble("carbsGrams") ?: 0.0
                        val proteinGrams = document.getDouble("proteinGrams") ?: 0.0
                        val fatGrams = document.getDouble("fatGrams") ?: 0.0

                        val carbsCaloriesValue = carbsGrams * 4
                        val proteinCaloriesValue = proteinGrams * 4
                        val fatCaloriesValue = fatGrams * 9

                        val decimalFormat = DecimalFormat("#")

                        totalCaloriesTextView.text = "${calorieGoal.toInt()} Kcal"
                        carbsValue.text = "${decimalFormat.format(carbsGrams)} g"
                        carbsCalories.text = "${decimalFormat.format(carbsCaloriesValue)} Kcal"
                        proteinValue.text = "${decimalFormat.format(proteinGrams)} g"
                        proteinCalories.text = "${decimalFormat.format(proteinCaloriesValue)} Kcal"
                        fatValue.text = "${decimalFormat.format(fatGrams)} g"
                        fatCalories.text = "${decimalFormat.format(fatCaloriesValue)} Kcal"

                        firestore.collection("food_entries").whereEqualTo("userId", userId).get()
                            .addOnSuccessListener { foodEntries ->
                                var totalCaloriesConsumed = 0.0
                                for (entry in foodEntries) {
                                    val calories = entry.getDouble("calorias") ?: 0.0
                                    totalCaloriesConsumed += calories
                                }
                                val remainingCalories = calorieGoal - totalCaloriesConsumed
                                remainingCaloriesValueTextView.text = "${decimalFormat.format(remainingCalories)} Kcal"
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                            }

                        Toast.makeText(this, "Dados carregados com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.home_activity
    }
}