package by.iba.sbs.library.service

import platform.Foundation.NSString
import platform.Foundation.localizedStringWithFormat
import platform.Foundation.stringWithFormat

actual class Utils {
    actual companion object {
        actual fun formatString(source: String, vararg args: Any): String {
            var startIdx = 0
            var result = ""
            for (arg in args){
                var idx = source.indexOf("%s")
                var ts = source.substring(startIdx, idx) + arg
                startIdx = idx + 2
                result += ts
            }
            result += source.substring(startIdx)
            return result
            //return NSString.localizedStringWithFormat(source.replace("%s","%@",false),args)

            //return NSString.stringWithFormat(source, args.map { it.toString() }.toTypedArray())
            //return "Not yet implemented"
        }
    }
}