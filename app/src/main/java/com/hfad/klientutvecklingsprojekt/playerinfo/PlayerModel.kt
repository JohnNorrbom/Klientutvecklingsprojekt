package com.hfad.klientutvecklingsprojekt.playerinfo




data class PlayerModel(
    var gameID : String = "",
    var status : Progress? = null,
    var takenPosition: MutableList<Pair<String,CharacterStatus>> = mutableListOf()

)
enum class Progress{
    INPROGRESS,
    FINISHED
}
enum class CharacterStatus{
    FREE,
    TAKEN,
}