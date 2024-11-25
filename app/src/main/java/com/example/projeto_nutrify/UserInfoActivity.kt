package com.example.projeto_nutrify

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
                val user = hashMapOf(
                    "name" to name,
                    "dateOfBirth" to dateOfBirth,
                    "gender" to gender,
                    "weight" to weight,
                    "height" to height,
                    "email" to auth.currentUser?.email
                )

                // Salvar no Firestore
                firestore.collection("users").document(userId!!)
                    .set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java)) // Vai para a Home
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao salvar os dados: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
