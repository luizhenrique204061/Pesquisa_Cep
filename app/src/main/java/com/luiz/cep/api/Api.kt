package com.luiz.cep.api

import com.luiz.cep.Endereco
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    @GET("ws/{cep}/json/")

    fun buscarPeloCep(@Path("cep") cep: String): Call<Endereco>


}