package com.example.projeto_nutrify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val usernameTextView = findViewById<TextView>(R.id.username)
        val programDescriptionTextView = findViewById<TextView>(R.id.programDescription)
        val heightTextView = findViewById<TextView>(R.id.height) // Corrected ID
        val weightTextView = findViewById<TextView>(R.id.weight) // Corrected ID
        val ageTextView = findViewById<TextView>(R.id.age) // Corrected ID

        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "N/A"
                        val height = document.getDouble("height")?.toString() ?: "N/A"
                        val weight = document.getDouble("weight")?.toString() ?: "N/A"
                        val dateOfBirth = document.getString("dateOfBirth") ?: "N/A"
                        val age = calculateAge(dateOfBirth).toString()

                        usernameTextView.text = name
                        heightTextView.text = "$height cm\nAltura"
                        weightTextView.text = "$weight kg\nPeso"
                        ageTextView.text = "$age anos\nIdade"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("PerfilActivity", "Erro ao carregar dados: ${e.message}")
                }
        } else {
            Log.e("PerfilActivity", "User ID is null")
        }

        val imcTextView = findViewById<TextView>(R.id.imcTextView)
        imcTextView.setOnClickListener {
            val intent = Intent(this, InfosActivity::class.java)
            startActivity(intent)
        }

        val metabolicRateTextView = findViewById<TextView>(R.id.metabolicRateTextView)
        metabolicRateTextView.setOnClickListener {
            val intent = Intent(this, InfosActivity::class.java)
            startActivity(intent)
        }

        val editButton = findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            showEditProfileDialog()
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_perfil
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Editar Perfil")

        val alertDialog = dialogBuilder.show()

        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editTextWeight = dialogView.findViewById<EditText>(R.id.editTextWeight)
        val editTextHeight = dialogView.findViewById<EditText>(R.id.editTextHeight)
        val editTextDateOfBirth = dialogView.findViewById<EditText>(R.id.editTextDateOfBirth)
        val spinnerGender = dialogView.findViewById<Spinner>(R.id.spinnerGender)
        val spinnerGoal = dialogView.findViewById<Spinner>(R.id.spinnerGoal)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        // Preencher os spinners com as opções
        val genderOptions = arrayOf("Masculino", "Feminino")
        val goalOptions = arrayOf("Emagrecer", "Ganhar Massa", "Manter Peso")
        spinnerGender.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderOptions)
        spinnerGoal.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, goalOptions)

        // Preencher os campos com os dados atuais do usuário
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        editTextName.setText(document.getString("name") ?: "")
                        editTextWeight.setText(document.getDouble("weight")?.toString() ?: "")
                        editTextHeight.setText(document.getDouble("height")?.toString() ?: "")
                        editTextDateOfBirth.setText(document.getString("dateOfBirth") ?: "")
                        val gender = document.getString("gender") ?: ""
                        val goal = document.getString("goal") ?: ""
                        spinnerGender.setSelection(genderOptions.indexOf(gender))
                        spinnerGoal.setSelection(goalOptions.indexOf(goal))
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("PerfilActivity", "Erro ao carregar dados: ${e.message}")
                }
        } else {
            Log.e("PerfilActivity", "User ID is null")
        }

        btnSave.setOnClickListener {
            val name = editTextName.text.toString()
            val weight = editTextWeight.text.toString().toDoubleOrNull()
            val height = editTextHeight.text.toString().toDoubleOrNull()
            val dateOfBirth = editTextDateOfBirth.text.toString()
            val gender = spinnerGender.selectedItem.toString()
            val goal = spinnerGoal.selectedItem.toString()

            if (name.isEmpty() || weight == null || height == null || dateOfBirth.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calcular TMB
            val tmb = if (gender == "Masculino") {
                88.36 + (13.4 * weight) + (4.8 * height) - (5.7 * calculateAge(dateOfBirth))
            } else {
                447.6 + (9.2 * weight) + (3.1 * height) - (4.3 * calculateAge(dateOfBirth))
            }

            // Calcular meta de calorias com base no objetivo
            val calorieGoal = when (goal) {
                "Emagrecer" -> tmb - 300
                "Ganhar Massa" -> tmb + 300
                "Manter Peso" -> tmb
                else -> tmb
            }

            // Calcular macronutrientes
            val carbsCalories = calorieGoal * 0.50
            val proteinCalories = calorieGoal * 0.20
            val fatCalories = calorieGoal * 0.30

            val carbsGrams = carbsCalories / 4
            val proteinGrams = proteinCalories / 4
            val fatGrams = fatCalories / 9

            val user = hashMapOf(
                "name" to name,
                "weight" to weight,
                "height" to height,
                "dateOfBirth" to dateOfBirth,
                "gender" to gender,
                "goal" to goal,
                "tmb" to tmb,
                "calorieGoal" to calorieGoal,
                "carbsGrams" to carbsGrams,
                "proteinGrams" to proteinGrams,
                "fatGrams" to fatGrams,
                "imc" to weight / ((height / 100) * (height / 100))
            )

            userId?.let { id ->
                firestore.collection("users").document(id).set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Log.e("PerfilActivity", "Erro ao atualizar dados: ${e.message}")
                        Toast.makeText(this, "Erro ao atualizar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } ?: run {
                Log.e("PerfilActivity", "User ID is null")
                Toast.makeText(this, "Erro: User ID is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateAge(dateOfBirth: String): Int {
        val parts = dateOfBirth.split("/")
        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()

        val today = java.util.Calendar.getInstance()
        var age = today.get(java.util.Calendar.YEAR) - year
        if (today.get(java.util.Calendar.MONTH) + 1 < month ||
            (today.get(java.util.Calendar.MONTH) + 1 == month && today.get(java.util.Calendar.DAY_OF_MONTH) < day)) {
            age--
        }
        return age
    }
}