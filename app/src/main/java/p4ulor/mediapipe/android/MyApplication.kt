package p4ulor.mediapipe.android

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import p4ulor.mediapipe.DependencyInjectionScanner

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(DependencyInjectionScanner().module)
        }
    }
}