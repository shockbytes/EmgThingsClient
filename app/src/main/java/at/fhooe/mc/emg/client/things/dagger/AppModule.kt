package at.fhooe.mc.emg.client.things.dagger

import android.content.Context
import at.fhooe.mc.emg.client.things.R
import at.fhooe.mc.emg.client.things.core.DeviceConfig
import at.fhooe.mc.emg.client.things.update.EmgUpdateManager
import at.fhooe.mc.emg.client.things.update.PhoneEmgUpdateManager
import at.fhooe.mc.emg.client.things.update.ThingsEmgUpdateManager
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author Martin Macheiner
 * Date: 31.01.2018.
 */

@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    @Named("bt_name")
    fun provideBluetoothDeviceName(): String {
        return if (DeviceConfig.useThingsConfig) {
            context.getString(R.string.bluetooth_device_name)
        } else {
            context.getString(R.string.bluetooth_device_name_phone)
        }
    }

    @Provides
    @Singleton
    fun provideEmgUpdateManager(): EmgUpdateManager {
        return if (DeviceConfig.useThingsConfig) {
            ThingsEmgUpdateManager()
        } else {
            PhoneEmgUpdateManager()
        }
    }

}