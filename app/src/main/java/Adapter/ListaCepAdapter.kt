package Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luiz.cep.Endereco
import com.luiz.cep.databinding.RespostaApiItemBinding

class ListaCepAdapter(
    val context: Context,
    val listaEndereco: MutableList<Endereco> = mutableListOf()
): RecyclerView.Adapter<ListaCepAdapter.ViewHolder>() {

    class ViewHolder(private val binding: RespostaApiItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun vincula(item: Endereco) {

            val cep = binding.respostaCep
            cep.text = "Cep: ${item.cep}"

            val logradouro = binding.respostaNomeRua
            logradouro.text = "Logradouro: ${item.logradouro}"

            val bairro = binding.respostaBairro
            bairro.text = "Bairro: ${item.bairro}"

            val cidade = binding.respostaCidade
            cidade.text = "Cidade: ${item.localidade}"

            val estado = binding.respostaEstado
            estado.text = "Estado: ${item.uf}"

            val ddd = binding.respostaDdd
            ddd.text = "DDD: ${item.ddd}"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = RespostaApiItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(item)
    }

    override fun getItemCount(): Int {
        return listaEndereco.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itens = listaEndereco[position]
        holder.vincula(itens)
    }

}