package by.iba.sbs.library.repository

import by.iba.sbs.db.SBSDB
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual fun createDb(): SBSDB {
    val driver = NativeSqliteDriver(SBSDB.Schema, "sbs.db")
    return SBSDB(driver)
}