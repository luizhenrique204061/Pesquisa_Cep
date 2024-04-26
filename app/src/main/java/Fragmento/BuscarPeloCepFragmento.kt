package Fragmento

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.luiz.cep.Endereco
import com.luiz.cep.R
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
    private lateinit var appUpdateManager: AppUpdateManager

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

        // Inicialização do AppUpdateManager
        appUpdateManager = AppUpdateManagerFactory.create(requireContext())

        // Verificação de atualização
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Iniciar o processo de atualização
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    requireActivity(),
                    REQUEST_CODE_UPDATE
                )
            }
        }

        val desenhavel = ContextCompat.getDrawable(requireContext(), R.drawable.ic_localizacao)

        val temaEscuro =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES

        //Definir cor do do ícone
        if (temaEscuro) {
            desenhavel?.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            desenhavel?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
        }

        binding.localizacao.setCompoundDrawablesWithIntrinsicBounds(null, null, desenhavel, null)
        carregarAnuncios()


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
            binding.localizacao.visibility = View.GONE
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
                        if (response.isSuccessful) {
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
                                binding.localizacao.visibility = View.VISIBLE
                                buscarNoGoogleMaps()
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

    private fun carregarAnuncios() {
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
    }

    private fun buscarNoGoogleMaps() {
        binding.localizacao.setOnClickListener {



            // Crie a URI para abrir o Google Maps com o CEP
            val googleMapsUri = Uri.parse("geo:0,0?q=Cep: ${binding.editCep.text.toString()}, ${binding.editLogradouro.text.toString()}, ${binding.editBairro.text.toString()}, ${binding.editCidade.text.toString()}, ${binding.editEstado.text.toString()}")
            val googleMapsIntent = Intent(Intent.ACTION_VIEW, googleMapsUri)
            googleMapsIntent.setPackage("com.google.android.apps.maps")

            // Crie a URI para abrir o Waze com base no endereço
            val wazeUri = Uri.parse("https://waze.com/ul?q=Cep: ${binding.editCep.text.toString()}, ${binding.editLogradouro.text.toString()}, ${binding.editBairro.text.toString()}, ${binding.editCidade.text.toString()}, ${binding.editEstado.text.toString()}")
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


            /*
            val googleUri = Uri.parse("geo:0,0?q=Cep: ${binding.editCep.text.toString()}, ${binding.editLogradouro.text.toString()}, ${binding.editBairro.text.toString()}, ${binding.editCidade.text.toString()}, ${binding.editEstado.text.toString()}")
            Log.i("Maps", binding.editCep.toString())
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
    }

    private fun copiarCampos() {
        val cepCopiar = binding.editCep.text.toString()
        val logradouroCopiar = binding.editLogradouro.text.toString()
        val bairroCopiar = binding.editBairro.text.toString()
        val localidadeCopiar = binding.editCidade.text.toString()
        val ufCopiar = binding.editEstado.text.toString()
        val dddCopiar = binding.editDdd.text.toString()

        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copiar = ClipData.newPlainText(
            "Endereço",
            "Cep: ${cepCopiar}\nLogradouro: $logradouroCopiar\nBairro: $bairroCopiar\nCidade: $localidadeCopiar\nEstado: $ufCopiar\nDDD: $dddCopiar"
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

    companion object {
        private const val REQUEST_CODE_UPDATE = 100
    }

}
