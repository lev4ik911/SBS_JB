package by.iba.sbs.library.service

import platform.Foundation.NSString
import platform.Foundation.localizedStringWithFormat
import platform.Foundation.stringWithFormat

actual class Utils {
    actual companion object {
        actual fun formatString(source: String, vararg args: Any): String {
            var startIdx = 0
            var result = ""
            var next = ""
            var tmpSource = source
            for (arg in args) {
                startIdx = 0
                var idx = tmpSource.indexOf("%s")
                var ts = tmpSource.substring(startIdx, idx) + arg
                startIdx = idx + 2
                next = tmpSource.substring(startIdx)
                tmpSource = next
                result += ts
            }
            if (next.length>0) result+=next
            return result
        }
    }
}