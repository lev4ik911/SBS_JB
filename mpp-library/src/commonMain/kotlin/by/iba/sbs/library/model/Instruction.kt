package by.iba.sbs.library.model

class Instruction(
    val id: Int,
    var name: String = "",
    var author: String = "",
    var description: String = "",
    var isFavorite: Boolean = false
//    var steps: MutableList<Step> = mutableListOf(),
//    var feedback: MutableList<Feedback> = mutableListOf()
) {
}