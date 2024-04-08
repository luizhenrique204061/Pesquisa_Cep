package com.luiz.cep.api

import com.luiz.cep.Endereco
import com.luiz.cep.ListaEndereco
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiBuscaPeloEndereco {

    @GET("ws/{uf}/{localidade}/{logradouro}/json/")

    fun buscarEndereco(
        @Path("uf") estado: String,
        @Path("localidade") cidade: String,
        @Path("logradouro") rua: String
    ) : Call<List<ListaEndereco>>
}