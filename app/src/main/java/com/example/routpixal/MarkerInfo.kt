package com.example.routpixal

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

class MarkerInfo() : Parcelable {
    val listaZdjec: MutableList<Uri> = mutableListOf()

    constructor(parcel: Parcel) : this() {
        parcel.readTypedList(listaZdjec, Uri.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(listaZdjec)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MarkerInfo> {
        override fun createFromParcel(parcel: Parcel): MarkerInfo {
            return MarkerInfo(parcel)
        }

        override fun newArray(size: Int): Array<MarkerInfo?> {
            return arrayOfNulls(size)
        }
    }
}
