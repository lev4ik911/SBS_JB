package by.iba.sbs.library.repository

import android.content.Context
import by.iba.sbs.SBSDB
import com.squareup.sqldelight.android.AndroidSqliteDriver

lateinit var appContext: Context

actual fun createDb(): SBSDB {
    val driver = AndroidSqliteDriver(SBSDB.Schema, appContext, "sbs.db")
    return SBSDB(driver)
}