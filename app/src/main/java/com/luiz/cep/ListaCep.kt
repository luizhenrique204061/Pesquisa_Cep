package com.luiz.cep

import Adapter.ListaCepAdapter
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.luiz.cep.databinding.ActivityListaCepBinding

class ListaCep : AppCompatActivity() {
    private lateinit var adapter: ListaCepAdapter
    private lateinit var binding: ActivityListaCepBinding
    val listaEndereco: MutableList<Endereco> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityListaCepBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // --- CONFIGURAÇÃO MANUAL DAS MARGENS E BARRA ---

        // 1. Configura a janela para Edge-to-Edge total
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // 2. Ouve as dimensões das barras do sistema (status bar, nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // A. Ajusta a altura da view preta para ser idêntica à barra de status
            val params = binding.viewStatusBar.layoutParams
            params.height = insets.top
            binding.viewStatusBar.layoutParams = params

            // B. Aplica margens (padding) nas laterais e em baixo (Barra de navegação)
            // Isso impede que o conteúdo seja cortado em baixo ou nos lados
            view.setPadding(insets.left, 0, insets.right, insets.bottom)

            WindowInsetsCompat.CONSUMED
        }

        // 3. Garante ícones brancos na barra (pois o fundo da view é verde)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false

        // ------------------------------------------------

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