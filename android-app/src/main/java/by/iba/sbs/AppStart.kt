package by.iba.sbs

import android.app.Application
import android.content.Intent
import by.iba.sbs.di.serviceModule
import by.iba.sbs.di.viewModelModule
import by.iba.sbs.library.repository.appContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin

class AppStart : Application() {

    override fun onCreate() {
        super.onCreate()
        //   AndroidThreeTen.init(this)
        appContext = this
        startKoin {
            fragmentFactory()
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
        // if (Utils.isAppOnForeground(applicationContext)) {
        val intent =
            Intent(applicationContext, by.iba.sbs.ui.walkthrough.WalkthroughActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
        //        }
    }

}