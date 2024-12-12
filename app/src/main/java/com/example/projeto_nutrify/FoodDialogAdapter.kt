package com.example.projeto_nutrify

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodDialogAdapter(
    private val context: Context,
    private var foodList: List<Food>,
    private val onFoodClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodDialogAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        holder.bind(food)
        holder.itemView.setOnClickListener { onFoodClick(food) }
    }

    override fun getItemCount(): Int = foodList.size

    fun updateFoodList(newFoodList: List<Food>) {
        foodList = newFoodList
        notifyDataSetChanged()
    }

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        private val tvCalories: TextView = itemView.findViewById(R.id.tvCalories)
        private val tvCarbs: TextView = itemView.findViewById(R.id.tvCarbs)
        private val tvFat: TextView = itemView.findViewById(R.id.tvFat)
        private val tvProtein: TextView = itemView.findViewById(R.id.tvProtein)

        fun bind(food: Food) {
            tvFoodName.text = food.nome
            tvCalories.text = "Calorias: ${food.calorias}"
            tvCarbs.text = "Carboidratos: ${food.carboidratos}"
            tvFat.text = "Gordura: ${food.gordura}"
            tvProtein.text = "Proteínas: ${food.proteinas}"
        }
    }
}