package com.example.projeto_nutrify

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class UserInfoActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val dateOfBirthEditText = findViewById<EditText>(R.id.dateOfBirthEditText)
        val genderRadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)
        val weightEditText = findViewById<EditText>(R.id.weightEditText)
        val heightEditText = findViewById<EditText>(R.id.heightEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val dateOfBirth = dateOfBirthEditText.text.toString()
            val weight = weightEditText.text.toString().toFloatOrNull()
            val height = heightEditText.text.toString().toFloatOrNull()
            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val gender = findViewById<RadioButton>(selectedGenderId)?.text.toString()

            if (name.isNotEmpty() && dateOfBirth.isNotEmpty() && weight != null && height != null && gender.isNotEmpty()) {
                val userId = auth.currentUser?.uid
                val email = auth.currentUser?.email
                val age = calculateAge(dateOfBirth)

                if (age != null) {
                    // Calcular IMC e TMB
                    val imc = calculateIMC(weight, height)
                    val tmb = calculateTMB(gender, weight, height, age)

                    val user = hashMapOf(
                        "name" to name,
                        "dateOfBirth" to dateOfBirth,
                        "gender" to gender,
                        "weight" to weight,
                        "height" to height,
                        "email" to email,
                        "imc" to imc,
                        "tmb" to tmb
                    )

                    // Salvar no Firestore
                    firestore.collection("users").document(userId!!)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao salvar os dados: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Data de nascimento inválida!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateIMC(weight: Float, height: Float): Float {
        val heightInMeters = height / 100 // Converter altura para metros
        return weight / (heightInMeters * heightInMeters)
    }

    private fun calculateTMB(gender: String, weight: Float, height: Float, age: Int): Double {
        return if (gender == "Masculino") {
            88.36 + (13.4 * weight) + (4.8 * height) - (5.7 * age)
        } else {
            447.6 + (9.2 * weight) + (3.1 * height) - (4.3 * age)
        }
    }

    private fun calculateAge(dateOfBirth: String): Int? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val birthDate = sdf.parse(dateOfBirth)
            val today = Calendar.getInstance()
            val birth = Calendar.getInstance()
            birth.time = birthDate!!

            var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age
        } catch (e: Exception) {
            null
        }
    }
}

