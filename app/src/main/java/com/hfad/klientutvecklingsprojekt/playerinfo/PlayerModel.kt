package com.hfad.klientutvecklingsprojekt.playerinfo




data class PlayerModel(
    var status : Progress,
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