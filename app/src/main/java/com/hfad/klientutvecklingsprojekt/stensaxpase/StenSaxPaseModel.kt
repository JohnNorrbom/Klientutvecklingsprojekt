package com.hfad.klientutvecklingsprojekt.stensaxpase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.IgnoreExtraProperties
import com.hfad.klientutvecklingsprojekt.playerinfo.PlayerModel

@IgnoreExtraProperties
data class StenSaxPaseModel
    (
    val gameID : String? = null,
    var status : Boolean? = null,
    var players: MutableMap<String, MutableMap<String,String>> = mutableMapOf()
    )