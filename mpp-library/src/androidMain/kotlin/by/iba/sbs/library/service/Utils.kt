package by.iba.sbs.library.service

actual class Utils {
    actual companion object {
        actual fun formatString(source: String, vararg args: Any): String =
            String.format(source, *args)
    }
}