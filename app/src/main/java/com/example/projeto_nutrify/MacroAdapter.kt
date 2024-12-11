package com.example.projeto_nutrify

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MacroAdapter(
    private val context: Context,
    private var foodList: List<Food>,
    private val onFoodSelected: (Food) -> Unit
) : RecyclerView.Adapter<MacroAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val tvCalories: TextView = itemView.findViewById(R.id.tvCalories)
        val tvCarbs: TextView = itemView.findViewById(R.id.tvCarbs)
        val tvFat: TextView = itemView.findViewById(R.id.tvFat)
        val tvProtein: TextView = itemView.findViewById(R.id.tvProtein)

        fun bind(food: Food) {
            tvFoodName.text = food.nome
            tvCalories.text = "Calorias: ${food.calorias} kcal"
            tvCarbs.text = "Carboidratos: ${food.carboidratos} g"
            tvFat.text = "Gordura: ${food.gordura} g"
            tvProtein.text = "Proteínas: ${food.proteinas} g"

            itemView.setOnClickListener {
                onFoodSelected(food)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position])
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun updateFoodList(newFoodList: List<Food>) {
        foodList = newFoodList
        notifyDataSetChanged()
    }
}