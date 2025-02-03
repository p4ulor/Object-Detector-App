package p4ulor.mediapipe.android

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import p4ulor.mediapipe.DependencyInjectionScanner
import org.koin.ksp.generated.*

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(DependencyInjectionScanner().module)
        }
    }
}