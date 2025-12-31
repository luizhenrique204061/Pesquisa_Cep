package com.luiz.cep

import Fragmento.BuscarPeloCepFragmento
import Fragmento.BuscarPeloEnderecoFragmento
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.luiz.cep.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        renderizarFragmento(R.id.container_fragmento_buscar_pelo_cep, BuscarPeloCepFragmento())
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
}