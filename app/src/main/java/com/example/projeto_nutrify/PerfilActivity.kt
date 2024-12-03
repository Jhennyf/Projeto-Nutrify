package com.example.projeto_nutrify

import android.content.Intent
import android.os.Bundle
import android.widget.TextView

class PerfilActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_perfil
    }
}