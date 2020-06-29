package by.iba.sbs.library.service

import com.russhwolf.settings.*

//https://github.com/russhwolf/multiplatform-settings

class LocalSettings(settings: Settings) {
    var accessToken by settings.string("pref_access_token")
    var refreshToken by settings.string("pref_refresh_token")
    var language by settings.string("pref_language")
    var login by settings.string("pref_login")
    var lastUpdate by settings.long("last_update", defaultValue = 0L)
    var keepLogin by settings.boolean("pref_remember_login")
    var showRecommended by settings.boolean("pref_show_recommended", defaultValue = true)
    var showFavorites by settings.boolean("pref_show_favorites", defaultValue = true)
    var searchHistoryJson by settings.string("pref_search_history_json")
    var searchHistoryCount by settings.int("pref_search_history_count", defaultValue = 5)
}
