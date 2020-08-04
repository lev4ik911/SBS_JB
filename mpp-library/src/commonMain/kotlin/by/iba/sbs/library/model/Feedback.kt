package by.iba.sbs.library.model

data class Feedback(
    var id: String = "00000000-0000-0000-0000-000000000000",
    var rating: Int = 0,
    var comment: String? = null,
    var author: String = "",
    var authorId: String = ""
)