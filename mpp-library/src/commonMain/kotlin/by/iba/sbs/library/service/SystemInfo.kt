package by.iba.sbs.library.service

interface SystemInformation {
     fun getDeviceName(): String
     fun getDeviceID(): String
     fun getAppVersion():String
}