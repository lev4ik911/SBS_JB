package by.iba.sbs.library.repository

import android.content.Context
import by.iba.sbs.db.SBSDB
import com.squareup.sqldelight.android.AndroidSqliteDriver

lateinit var appContext: Context

actual fun createDb(): SBSDB {
    val driver = AndroidSqliteDriver(SBSDB.Schema, appContext, "galwaybus.db")
    return SBSDB(driver)
}