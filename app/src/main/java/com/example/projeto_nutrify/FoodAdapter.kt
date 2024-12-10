import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto_nutrify.Alimento
import com.example.projeto_nutrify.R

class AlimentoAdapter(
    private val alimentos: List<Alimento>,
    private val onAlimentoClick: (Alimento) -> Unit
) : RecyclerView.Adapter<AlimentoAdapter.AlimentoViewHolder>() {

    inner class AlimentoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nomeTextView: TextView = view.findViewById(R.id.tvNomeAlimento)
        private val macrosTextView: TextView = view.findViewById(R.id.tvMacrosAlimento)

        fun bind(alimento: Alimento) {
            nomeTextView.text = alimento.nome
            macrosTextView.text = "C: ${alimento.carboidratos}g, P: ${alimento.proteinas}g, G: ${alimento.gorduras}g"
            itemView.setOnClickListener { onAlimentoClick(alimento) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlimentoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alimento, parent, false)
        return AlimentoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlimentoViewHolder, position: Int) {
        holder.bind(alimentos[position])
    }

    override fun getItemCount(): Int = alimentos.size
}
