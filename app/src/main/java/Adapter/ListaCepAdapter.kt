package Adapter

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.luiz.cep.Endereco
import com.luiz.cep.databinding.RespostaApiItemBinding

class ListaCepAdapter(
    val context: Context,
    val listaEndereco: MutableList<Endereco> = mutableListOf()
) : RecyclerView.Adapter<ListaCepAdapter.ViewHolder>() {

    class ViewHolder(private val binding: RespostaApiItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

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

            binding.localizacao.setOnClickListener {

                // Crie a URI para abrir o Google Maps com o CEP
                val googleMapsUri = Uri.parse("geo:0,0?q=Cep: ${item.cep}")
                val googleMapsIntent = Intent(Intent.ACTION_VIEW, googleMapsUri)
                googleMapsIntent.setPackage("com.google.android.apps.maps")

                // Crie a URI para abrir o Waze com base no endereço
                val wazeUri = Uri.parse("https://waze.com/ul?q=Cep: ${item.cep}")
                val wazeIntent = Intent(Intent.ACTION_VIEW, wazeUri)
                wazeIntent.setPackage("com.waze")

                // Crie um intent chooser para apresentar ambas as opções ao usuário
                val escolherAbertura = Intent.createChooser(googleMapsIntent, "Abrir com")

                // Adicione o Waze como uma opção adicional no intent chooser
                escolherAbertura.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(wazeIntent))

                // Verifique se há pelo menos um aplicativo de mapa instalado
                if (escolherAbertura.resolveActivity(binding.root.context.packageManager) != null) {
                    // Verifica se o contexto é uma instância de Activity
                    if (binding.root.context is Activity) {
                        binding.root.context.startActivity(escolherAbertura)
                    } else {
                        // Se o contexto não for uma instância de Activity, adicione a flag FLAG_ACTIVITY_NEW_TASK
                        escolherAbertura.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        binding.root.context.startActivity(escolherAbertura)
                    }
                } else {
                    // Se nenhum aplicativo de mapa estiver instalado, mostre uma mensagem de erro
                    Toast.makeText(
                        binding.root.context.applicationContext,
                        "Nenhum aplicativo de mapas encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                //Código que abre o Google Maps para buscar o endereço
                /*
                val googleUri = Uri.parse("geo:0,0?q=Cep: ${item.cep}")
                Log.i("Maps", item.cep.toString())
                val mapsIntent = Intent(Intent.ACTION_VIEW, googleUri)
                mapsIntent.setPackage("com.google.android.apps.maps") // Iniciar a abertura no Google Maps

                if (mapsIntent.resolveActivity(binding.root.context.packageManager) != null) {
                    // Verifica se o contexto é uma instância de Activity
                    if (binding.root.context is Activity) {
                        binding.root.context.startActivity(mapsIntent)
                    } else {
                        // Se o contexto não for uma instância de Activity, adicione a flag FLAG_ACTIVITY_NEW_TASK
                        mapsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        binding.root.context.startActivity(mapsIntent)
                    }
                } else {
                    Toast.makeText(
                        binding.root.context.applicationContext,
                        "Não foi possível abrir o Google Maps",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                 */
            }


            binding.copiar.setOnClickListener {
                val clipboard =
                    it.context.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val copiar = ClipData.newPlainText(
                    "Endereco",
                    "${cep.text}\n${logradouro.text}\n${bairro.text}\n${cidade.text}\n${estado.text}\n${ddd.text}"
                )
                clipboard.setPrimaryClip(copiar)
                Toast.makeText(
                    it.context.applicationContext,
                    "Todos os campos foram copiados com sucesso",
                    Toast.LENGTH_SHORT
                ).show()

            }
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

        /*
        holder.itemView.setOnClickListener {
            val endereco = listaEndereco[position]
            val googleUri = Uri.parse("geo:0,0?q=${endereco.cep}")
            Log.i("Maps", endereco.cep.toString())
            val mapsIntent = Intent(Intent.ACTION_VIEW, googleUri)
            mapsIntent.setPackage("com.google.android.apps.maps") // Iniciar a abertura no Google Maps

            if (mapsIntent.resolveActivity(context.packageManager) != null) {
                // Verifica se o contexto é uma instância de Activity
                if (context is Activity) {
                    context.startActivity(mapsIntent)
                } else {
                    // Se o contexto não for uma instância de Activity, adicione a flag FLAG_ACTIVITY_NEW_TASK
                    mapsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(mapsIntent)
                }
            } else {
                Toast.makeText(
                    context.applicationContext,
                    "Não foi possível abrir o Google Maps",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

         */
    }

}