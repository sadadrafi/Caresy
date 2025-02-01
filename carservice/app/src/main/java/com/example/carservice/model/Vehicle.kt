package com.example.carservice.model

import android.os.Parcel
import android.os.Parcelable

data class Vehicle(
    val id: String = "",
    val brand: String = "",
    val model: String = "",
    val year: String = "",
    val licensePlate: String = ""
) : Parcelable {
    // Write the object to the Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(brand)
        parcel.writeString(model)
        parcel.writeString(year)
        parcel.writeString(licensePlate)
    }

    // Constructor to read the object from the Parcel
    private constructor(parcel: Parcel) : this(
        brand = parcel.readString() ?: "",
        model = parcel.readString() ?: "",
        year = parcel.readString() ?: "",
        licensePlate = parcel.readString() ?: ""
    )

    // Describe the contents of the Parcelable object
    override fun describeContents(): Int {
        return 0
    }

    // Parcelable.Creator to create instances of Vehicle from a Parcel
    companion object CREATOR : Parcelable.Creator<Vehicle> {
        override fun createFromParcel(parcel: Parcel): Vehicle {
            return Vehicle(parcel)
        }

        override fun newArray(size: Int): Array<Vehicle?> {
            return arrayOfNulls(size)
        }
    }
}
