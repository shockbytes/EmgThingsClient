package at.fhooe.mc.emg.client.things.dagger

import at.fhooe.mc.emg.client.things.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * @author Martin Macheiner
 * Date: 31.01.2018.
 */

@Singleton
@Component(modules = [AppModule::class, EmgModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

}