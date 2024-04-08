package com.luiz.cep

import Fragmento.BuscarPeloCepFragmento
import Fragmento.BuscarPeloEnderecoFragmento
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.luiz.cep.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        renderizarFragmento(R.id.container_fragmento_buscar_pelo_cep, BuscarPeloCepFragmento())

        teste()
        binding.buscarPeloCep.isChecked = true

        binding.buscarPeloEndereco.setOnClickListener {
            if (binding.buscarPeloEndereco.isChecked) {
                binding.containerFragmentoBuscarPeloCep.visibility = View.INVISIBLE
                binding.containerFragmentoBuscarPeloEndereco.visibility = View.VISIBLE
                renderizarFragmento(R.id.container_fragmento_buscar_pelo_endereco, BuscarPeloEnderecoFragmento())
            }
        }

        binding.buscarPeloCep.setOnClickListener {
            if (binding.buscarPeloCep.isChecked) {
                binding.containerFragmentoBuscarPeloEndereco.visibility = View.INVISIBLE
                binding.containerFragmentoBuscarPeloCep.visibility = View.VISIBLE
                renderizarFragmento(R.id.container_fragmento_buscar_pelo_cep, BuscarPeloCepFragmento())
            }
        }

    }

    private fun renderizarFragmento(containerId: Int, fragmento: Fragment) {
        val gerenciadorFragmento = supportFragmentManager
        val transicaoFragmento = gerenciadorFragmento.beginTransaction()
        transicaoFragmento.replace(containerId, fragmento)
        transicaoFragmento.commit()
    }

    fun teste() {

    }
}