package by.iba.sbs.library.model

data class Author(
    val name: String,
    val rating: Int = 0,
    val instructionsCount: Int = 0,
    val subscribersCount: Int = 0,
    val avatar: String = ""
) {
}