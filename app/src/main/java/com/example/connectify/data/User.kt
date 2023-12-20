package com.example.connectify.data

data class User(
    var uid : String = "",
    var email: String = "",
    var name: String = ""
) {
    var password: String = ""
    var userProfileURL: String = ""
    var userBio: String = ""
    var userPronouns: String = ""
}
