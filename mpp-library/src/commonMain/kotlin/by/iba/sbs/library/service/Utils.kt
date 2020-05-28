package by.iba.sbs.library.service

expect class Utils {
    companion object {
        fun formatString(source: String, vararg args: Any): String

    }
}