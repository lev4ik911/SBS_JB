package responce

data class Guideline(
    val name: String,
    val description: String,
    val category: Category,
    val steps:Step,
    val tags: List<Tag>,
    val creator: Creator,
    val createDate: String,
    val updateDate: String
) {
}