package Fragmento

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.luiz.cep.Endereco
import com.luiz.cep.ListaCep
import com.luiz.cep.ListaEndereco
import com.luiz.cep.api.ApiBuscaPeloEndereco
import com.luiz.cep.databinding.FragmentBuscarPeloEnderecoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class BuscarPeloEnderecoFragmento : Fragment() {
    private lateinit var binding: FragmentBuscarPeloEnderecoBinding
    lateinit var mAdview: AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBuscarPeloEnderecoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        MobileAds.initialize(requireContext())

        mAdview = binding.adview
        val adRequest = AdRequest.Builder().build()
        Log.i("Meu App", "Antes de carregar o anúncio")
        mAdview.loadAd(adRequest)



        binding.botaoLimpar.setOnClickListener {
            binding.logradouro.setText("")
            binding.cidade.setText("")
            binding.uf.setText("")
        }

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://viacep.com.br/")
            .build()
            .create(ApiBuscaPeloEndereco::class.java)

        binding.botaoBuscarCep.setOnClickListener {
            val logradouro = binding.logradouro.text.toString()
            val cidade = binding.cidade.text.toString()
            val uf = binding.uf.text.toString()

            if (logradouro.isEmpty() && cidade.isEmpty() && uf.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()

            } else {
                recolherTeclado()

                binding.view.visibility = View.VISIBLE
                binding.progressbar.visibility = View.VISIBLE
                binding.buscando.visibility = View.VISIBLE

                retrofit.buscarEndereco(uf, cidade, logradouro)
                    .enqueue(object : Callback<List<ListaEndereco>> {
                        override fun onResponse(
                            call: Call<List<ListaEndereco>>,
                            response: Response<List<ListaEndereco>>
                        ) {
                            if (response.isSuccessful) {
                                binding.view.visibility = View.GONE
                                binding.progressbar.visibility = View.GONE
                                binding.buscando.visibility = View.GONE
                                val enderecos = response.body()
                                if (enderecos != null && enderecos.isNotEmpty()) {
                                    // Aqui você pode lidar com a lista de endereços
                                    for (endereco in enderecos) {
                                     //   Log.d("Resposta da API", "Endereço: $endereco")
                                        Log.d("Resposta da API", "CEP: ${endereco.cep}")
                                        Log.d("Resposta da API", "Logradouro: ${endereco.logradouro}")
                                        Log.d("Resposta da API", "Bairro: ${endereco.bairro}")
                                        Log.d("Resposta da API", "Localidade: ${endereco.localidade}")
                                        Log.d("Resposta da API", "UF: ${endereco.uf}")
                                        Log.d("Resposta da API", "DDD: ${endereco.ddd}")
                                        Intent(requireContext(), ListaCep::class.java).apply {
                                            putParcelableArrayListExtra("enderecos", ArrayList(enderecos))
                                            startActivity(this)
                                        }
                                    }
                                } else {
                                    binding.view.visibility = View.GONE
                                    binding.progressbar.visibility = View.GONE
                                    binding.buscando.visibility = View.GONE
                                    Log.d("Resposta da API", "Nenhum endereço encontrado")
                                    Toast.makeText(requireContext(), "Nenhum endereço encontrado", Toast.LENGTH_SHORT).show()
                                }

                            } else {
                                Log.e("Resposta da API", "Erro ao buscar endereços: ${response.code()}")
                                binding.view.visibility = View.GONE
                                binding.progressbar.visibility = View.GONE
                                binding.buscando.visibility = View.GONE
                                Toast.makeText(requireContext(), "Erro ao buscar endereços", Toast.LENGTH_SHORT).show()
                            }
                        }


                        override fun onFailure(call: Call<List<ListaEndereco>>, t: Throwable) {
                            Log.e("Resposta da API", "Erro inesperado: ${t.message}")
                            binding.view.visibility = View.GONE
                            binding.progressbar.visibility = View.GONE
                            binding.buscando.visibility = View.GONE
                            Toast.makeText(requireContext(), "Erro inesperado", Toast.LENGTH_SHORT).show()                        }

                    })
            }
        }

    }
    private fun recolherTeclado() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}