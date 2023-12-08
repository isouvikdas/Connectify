package com.example.connectify.data

data class User(
    var uid : String = "",
    var email: String = "",
    var name: String = ""
) {
    var password: String = ""
}
