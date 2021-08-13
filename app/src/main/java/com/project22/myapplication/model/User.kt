package com.project22.myapplication.model

data class User(val firstName: String,
                val lastName: String,
                val profilePicturePath: String?,
                val registrationTokens: MutableList<String>) {

    constructor(): this("", "", null, mutableListOf())
}
