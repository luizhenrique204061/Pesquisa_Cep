package com.luiz.cep

import android.os.Parcel
import android.os.Parcelable

class ListaEndereco (
    val cep: String? = null,
    val logradouro: String? = null,
    val bairro: String? = null,
    val localidade: String? = null,
    val uf: String? = null,
    val ddd: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cep)
        parcel.writeString(logradouro)
        parcel.writeString(bairro)
        parcel.writeString(localidade)
        parcel.writeString(ddd)
        parcel.writeString(uf)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ListaEndereco> {
        override fun createFromParcel(parcel: Parcel): ListaEndereco {
            return ListaEndereco(parcel)
        }

        override fun newArray(size: Int): Array<ListaEndereco?> {
            return arrayOfNulls(size)
        }
    }
}