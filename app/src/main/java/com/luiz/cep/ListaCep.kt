package com.luiz.cep

import Adapter.ListaCepAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.luiz.cep.databinding.ActivityListaCepBinding

class ListaCep : AppCompatActivity() {
    private lateinit var adapter: ListaCepAdapter
    private lateinit var binding: ActivityListaCepBinding
    val listaEndereco: MutableList<Endereco> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityListaCepBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val recylcerView = binding.recyclerViewCep
        adapter = ListaCepAdapter(this, listaEndereco)

        recylcerView.adapter = adapter


        //O código abaixo usa o Pacelable, que é uma forma mais complexa de Serailizar
       // val enderecos = intent.getParcelableArrayListExtra<ListaEndereco>("enderecos")
        val enderecos = intent.getSerializableExtra("enderecos") as? Array<ListaEndereco>
        if (enderecos != null && enderecos.isNotEmpty()) {
            for (endereco in enderecos) {
                val lista = Endereco(
                    endereco.cep,
                    endereco.logradouro,
                    endereco.bairro,
                    endereco.localidade,
                    endereco.uf,
                    endereco.ddd)
                listaEndereco.add(lista)
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
        }
    }
}