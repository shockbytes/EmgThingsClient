package at.fhooe.mc.emg.client.things.core

import android.app.Application
import at.fhooe.mc.emg.client.things.dagger.AppComponent
import at.fhooe.mc.emg.client.things.dagger.AppModule
import at.fhooe.mc.emg.client.things.dagger.DaggerAppComponent
import at.fhooe.mc.emg.client.things.dagger.EmgModule

/**
 * @author Martin Macheiner
 * Date: 31.01.2018.
 */

class EmgThingsApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .emgModule(EmgModule(this))
                .build()
    }

}