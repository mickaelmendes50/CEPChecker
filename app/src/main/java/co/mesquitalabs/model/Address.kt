package co.mesquitalabs.model

data class Address(
    val cep: String,
    var state: String,
    var city: String,
    var neighborhood: String,
    var street: String,
    var latitude: String?,
    var longitude: String?
)
