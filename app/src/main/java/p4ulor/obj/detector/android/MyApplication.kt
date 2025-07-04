package p4ulor.obj.detector.android

import android.app.Application
import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import p4ulor.obj.detector.DependencyInjectionScanner
import p4ulor.obj.detector.data.sources.local.database.AppDatabase
import p4ulor.obj.detector.data.sources.local.database.MIGRATION_V1_V2

class MyApplication : Application() {

    /**
     * The Room Database for the whole application.
     * Could be initialized with Koin, but I want to do it the old way for historical/demo purposes
     * just like I do with [HomeViewModel]
     */
    val appDb by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "AppDatabase"
        ).addMigrations(MIGRATION_V1_V2)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication) // alt to applicationContext
            modules(DependencyInjectionScanner().module)
        }
    }
}
