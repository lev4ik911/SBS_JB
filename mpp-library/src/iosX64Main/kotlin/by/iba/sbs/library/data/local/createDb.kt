package by.iba.sbs.library.data.local

import by.iba.sbs.SBSDB
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual fun createDb(): SBSDB {
    val driver = NativeSqliteDriver(SBSDB.Schema, "sbs.db")
    return SBSDB(driver)
}