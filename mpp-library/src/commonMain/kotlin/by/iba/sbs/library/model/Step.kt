package by.iba.sbs.library.model

data class Step(
    var stepId: String = "",
    var name: String = "",
    var descr: String = "",
    var weight: Int = 0,
    var imagePath: String = "",
    var remoteImageId: String = "",
    var updateImageDateTime: String = ""
) {
    val isEmptyPreview get() = (remoteImageId.isEmpty() && updateImageDateTime.isEmpty())
    val isImageNotDownloaded get() = (!isEmptyPreview && imagePath.isEmpty())
    val isImageNotUploaded get() = (updateImageDateTime.isEmpty() && imagePath.isNotEmpty())
}