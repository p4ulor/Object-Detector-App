package p4ulor.mediapipe.android

import android.app.Application
import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import p4ulor.mediapipe.DependencyInjectionScanner
import p4ulor.mediapipe.data.sources.local.database.AppDatabase

class MyApplication : Application() {

    val appDb by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "AppDatabase"
        ).build()
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication) // alt to applicationContext
            modules(DependencyInjectionScanner().module)
        }
    }
}