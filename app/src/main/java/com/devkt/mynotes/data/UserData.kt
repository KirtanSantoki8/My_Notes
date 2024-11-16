package com.devkt.mynotes.data

data class UserData(
    val name: String = "",
    val email: String = "",
){
constructor():this("", "")
}
