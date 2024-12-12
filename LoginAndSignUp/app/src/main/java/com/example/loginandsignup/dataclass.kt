package com.example.loginandsignup

import android.provider.ContactsContract

data class dataclass(
    val `data`: ContactsContract.Contacts.Data,
    val id: String,
    val name: String
)