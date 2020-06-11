package by.iba.sbs.library.model

enum class MessageType(val index: Int) {
    SUCCESS(1),
    WARNING(2),
    ERROR(3),
    INFO(4),
    DEFAULT(5),
    CONFUSING(6)
}