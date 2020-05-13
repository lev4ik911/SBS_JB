package by.iba.sbs.library.service

import com.russhwolf.settings.Settings
import com.russhwolf.settings.boolean
import com.russhwolf.settings.string

class LocalSettings(settings: Settings) {
    var accessToken by settings.string("pref_token")
    var refreshToken by settings.string("pref_refresh_token")
    var language by settings.string("pref_language")
    var login by settings.string("pref_login")
    var keepLogin by settings.boolean("pref_remember")
}
