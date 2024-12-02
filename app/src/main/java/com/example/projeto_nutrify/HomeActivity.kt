package com.example.projeto_nutrify

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        // Referências para os elementos do layout
        val totalCaloriesTextView = findViewById<TextView>(R.id.totalCaloriesTextView)
        val remainingCaloriesValueTextView = findViewById<TextView>(R.id.remainingCaloriesValueTextView)
        val proteinValue = findViewById<TextView>(R.id.proteinValue)
        val proteinCalories = findViewById<TextView>(R.id.proteinCalories)
        val fatValue = findViewById<TextView>(R.id.fatValue)
        val fatCalories = findViewById<TextView>(R.id.fatCalories)
        val carbsValue = findViewById<TextView>(R.id.carbsValue)
        val carbsCalories = findViewById<TextView>(R.id.carbsCalories)

        // Verifica se o usuário está logado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Carregar dados do Firestore
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Recuperar dados do Firestore
                        val calorieGoal = document.getDouble("calorieGoal") ?: 0.0
                        val carbsGrams = document.getDouble("carbsGrams") ?: 0.0
                        val proteinGrams = document.getDouble("proteinGrams") ?: 0.0
                        val fatGrams = document.getDouble("fatGrams") ?: 0.0

                        val carbsCaloriesValue = carbsGrams * 4
                        val proteinCaloriesValue = proteinGrams * 4
                        val fatCaloriesValue = fatGrams * 9

                        val decimalFormat = DecimalFormat("#")

                        // Atualizar layout com os dados do Firebase
                        totalCaloriesTextView.text = "${calorieGoal.toInt()} Kcal"
                        remainingCaloriesValueTextView.text = "..." // ainda tem que implementar o calculo

                        carbsValue.text = "${decimalFormat.format(carbsGrams)} g"
                        carbsCalories.text = "${decimalFormat.format(carbsCaloriesValue)} Kcal"

                        proteinValue.text = "${decimalFormat.format(proteinGrams)} g"
                        proteinCalories.text = "${decimalFormat.format(proteinCaloriesValue)} Kcal"

                        fatValue.text = "${decimalFormat.format(fatGrams)} g"
                        fatCalories.text = "${decimalFormat.format(fatCaloriesValue)} Kcal"

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
