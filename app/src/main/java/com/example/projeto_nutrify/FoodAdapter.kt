package com.example.projeto_nutrify

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class FoodAdapter(
    private val context: Context,
    private var foodList: List<Food>,
    private val onAddFoodClickListener: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val calories: TextView = itemView.findViewById(R.id.tvCalories)
        val carbs: TextView = itemView.findViewById(R.id.tvCarbs)
        val fat: TextView = itemView.findViewById(R.id.tvFat)
        val protein: TextView = itemView.findViewById(R.id.tvProtein)
        val addButton: Button = itemView.findViewById(R.id.btnAddFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        holder.foodName.text = food.name
        holder.calories.text = "Calorias: ${food.calories} kcal"
        holder.carbs.text = "Carboidratos: ${food.carbs} g"
        holder.fat.text = "Gordura: ${food.fat} g"
        holder.protein.text = "Proteínas: ${food.protein} g"

        holder.addButton.setOnClickListener {
            onAddFoodClickListener(food)
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun updateFoodList(newFoodList: List<Food>) {
        foodList = newFoodList
        notifyDataSetChanged()
    }
}