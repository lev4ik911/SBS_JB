package by.iba.sbs.library.service

import com.russhwolf.settings.*

class LocalSettings(settings: Settings) {
    var token by settings.string("pref_token")
    var language by settings.string("pref_language")
    var login by settings.string("pref_login")
    var keepLogin by settings.boolean("pref_remember")
}
