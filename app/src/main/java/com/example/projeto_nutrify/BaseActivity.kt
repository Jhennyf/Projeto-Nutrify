package com.example.projeto_nutrify

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_base)

                val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
                layoutInflater.inflate(getLayoutResourceId(), contentFrame, true)

                val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
                bottomNavigationView.setOnItemSelectedListener { item ->
                        when (item.itemId) {
                                R.id.nav_home -> {
                                        startActivity(Intent(this, HomeActivity::class.java))
                                        true
                                }
                                R.id.nav_profile -> {
                                        startActivity(Intent(this, PerfilActivity::class.java))
                                        true
                                }
                                R.id.nav_add_food -> {
                                        startActivity(Intent(this, AddFoodActivity::class.java))
                                        true
                                }
                                else -> false
                        }
                }
        }

        abstract fun getLayoutResourceId(): Int
}