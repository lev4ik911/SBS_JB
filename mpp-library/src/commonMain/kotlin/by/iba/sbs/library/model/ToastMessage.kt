package by.iba.sbs.library.model


data class ToastMessage(
    val message: String,
    val type: MessageType = MessageType.DEFAULT,
    val log: String = ""
) {
    fun getLogMessage(): String = if (log.isEmpty()) message else log

}