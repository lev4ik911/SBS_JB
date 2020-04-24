package by.iba.sbs.tools

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import by.iba.sbs.library.service.SystemInformation

class SystemInfo(private val context: Context):SystemInformation {
    override fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model
        } else "$manufacturer $model"
    }

    @SuppressLint("HardwareIds")
    override fun getDeviceID(): String
        = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)


    override fun getAppVersion(): String  =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName

}