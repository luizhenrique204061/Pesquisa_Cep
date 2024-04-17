package Fragmento

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.luiz.cep.Endereco
import com.luiz.cep.api.Api
import com.luiz.cep.databinding.FragmentBuscarPeloCepFragmentoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BuscarPeloCepFragmento : Fragment() {
    private lateinit var binding: FragmentBuscarPeloCepFragmentoBinding
    lateinit var mAdview: AdView
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBuscarPeloCepFragmentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        MobileAds.initialize(requireContext())

        // Id de teste ca-app-pub-2053981007263513~6280399869
        //Anúncio do Tipo Banner
        mAdview = binding.adview
        val adRequest = AdRequest.Builder().build()
        Log.i("Meu App", "Antes de carregar o anúncio")
        mAdview.loadAd(adRequest)

        InterstitialAd.load(
            requireContext(),
            "ca-app-pub-2053981007263513/9501469318",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("MainActivity", adError?.toString()!!)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("MainActivity", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.show(requireActivity())
                }
            })


        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d("MainActivity", "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d("MainActivity", "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e("MainActivity", "Ad failed to show fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d("MainActivity", "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d("MainActivity", "Ad showed fullscreen content.")
            }
        }

        mAdview.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.i("Meu App", "Falha ao carregar o anúncio: ${adError.message}")
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d("Meu App", "Anúncio carregado com sucesso")

            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }


        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://viacep.com.br/")
            .build()
            .create(Api::class.java)

        binding.botaoLimpar.setOnClickListener {
            binding.editCep.setText("")
            binding.editLogradouro.setText("")
            binding.editCidade.setText("")
            binding.editEstado.setText("")
            binding.editBairro.setText("")
            binding.editDdd.setText("")
            binding.copiar.visibility = View.GONE
        }

        binding.copiar.visibility = View.GONE

        binding.botaoBuscarCep.setOnClickListener {
            val cep = binding.editCep.text.toString()

            recolherTeclado()

            if (cep.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha o cep!", Toast.LENGTH_SHORT).show()
            } else {
                binding.view.visibility = View.VISIBLE
                binding.progressbar.visibility = View.VISIBLE
                binding.buscando.visibility = View.VISIBLE
                retrofit.buscarPeloCep(cep).enqueue(object : Callback<Endereco> {
                    override fun onResponse(call: Call<Endereco>, response: Response<Endereco>) {
                        if (response.code() == 200) {
                            binding.view.visibility = View.GONE
                            binding.progressbar.visibility = View.GONE
                            binding.buscando.visibility = View.GONE
                            val logradouro = response.body()?.logradouro.toString()
                            val bairro = response.body()?.bairro.toString()
                            val localidade = response.body()?.localidade.toString()
                            val uf = response.body()?.uf.toString()
                            val ddd = response.body()?.ddd.toString()

                            if (logradouro.equals("null") && bairro.equals("null") && localidade.equals(
                                    "null"
                                ) && uf.equals("null") && ddd.equals("null")
                            ) {
                                Toast.makeText(requireContext(), "Cep Inválido", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                setFormularios(logradouro, bairro, localidade, uf, ddd)
                                binding.copiar.visibility = View.VISIBLE
                            }

                        } else {
                            Toast.makeText(requireContext(), "Cep inválido", Toast.LENGTH_SHORT)
                                .show()
                            binding.view.visibility = View.GONE
                            binding.progressbar.visibility = View.GONE
                            binding.buscando.visibility = View.GONE
                        }
                    }

                    override fun onFailure(call: Call<Endereco>, t: Throwable) {
                        binding.view.visibility = View.GONE
                        binding.progressbar.visibility = View.GONE
                        binding.buscando.visibility = View.GONE
                        Toast.makeText(requireContext(), "Erro inesperado", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
            binding.copiar.setOnClickListener {
                copiarCampos()
            }
        }
    }

    private fun copiarCampos() {
        val cepCopiar = binding.editCep.text.toString()
        val logradouroCopiar = binding.editLogradouro.text.toString()
        val bairroCopiar = binding.editBairro.text.toString()
        val localidadeCopiar = binding.editCidade.text.toString()
        val ufCopiar = binding.editEstado.text.toString()
        val dddCopiar = binding.editDdd.text.toString()

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copiar = ClipData.newPlainText(
            "Endereço",
            "Cep: ${cepCopiar}Logradouro: $logradouroCopiar\nBairro: $bairroCopiar\nCidade: $localidadeCopiar\nEstado: $ufCopiar\nDDD: $dddCopiar"
        )
        clipboard.setPrimaryClip(copiar)
        Toast.makeText(requireContext(), "Campos copiados com sucesso", Toast.LENGTH_SHORT).show()
    }

    private fun setFormularios(
        logradouro: String,
        bairro: String,
        localidade: String,
        uf: String,
        ddd: String
    ) {
        binding.editLogradouro.hint = ""
        binding.editBairro.hint = ""
        binding.editCidade.hint = ""
        binding.editEstado.hint = ""
        binding.editDdd.hint = ""

        binding.editLogradouro.setText(logradouro)
        binding.editBairro.setText(bairro)
        binding.editCidade.setText(localidade)
        binding.editEstado.setText(uf)
        binding.editDdd.setText(ddd)
    }


    private fun recolherTeclado() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}
