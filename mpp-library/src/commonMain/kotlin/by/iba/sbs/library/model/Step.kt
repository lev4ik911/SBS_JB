package by.iba.sbs.library.model

data class Step(
    var stepId: String = "",
    var name: String = "",
    var description: String = "",
    var weight: Int = 0,
    var imagePath: String = "",
    var updateImageTimeSpan: Int = 0
) {
}