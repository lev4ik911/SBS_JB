package by.iba.sbs

import android.app.Application
import by.iba.sbs.di.serviceModule
import by.iba.sbs.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppStart : Application() {



    override fun onCreate() {
        super.onCreate()
        //   AndroidThreeTen.init(this)
        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()
            // use the Android context given there
            androidContext(applicationContext)
            // load properties from assets/koin.properties file
            androidFileProperties()
            // module list
            modules(
                listOf(
                    viewModelModule,
                    serviceModule
                )
            )
        }
    }

}