package by.iba.sbs.library.model

data class Category(
    val name: String,
    val descr: String,
    var isActive: Boolean,
    val iconColorHex: String
) {
    fun onClick() {
        isActive = !isActive
    }
}