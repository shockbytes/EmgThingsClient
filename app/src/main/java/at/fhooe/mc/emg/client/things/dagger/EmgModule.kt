package at.fhooe.mc.emg.client.things.dagger

import android.content.Context
import at.fhooe.mc.emg.client.things.client.ThingsBluetoothClient
import at.fhooe.mc.emg.client.things.core.DeviceConfig
import at.fhooe.mc.emg.client.things.sensing.AdcEmgSensor
import at.fhooe.mc.emg.client.things.sensing.DummyEmgSensor
import at.fhooe.mc.emg.client.things.sensing.EmgSensor
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author Martin Macheiner
 * Date: 31.01.2018.
 */

@Module
class EmgModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideThingsBluetoothClient(sensors: List<EmgSensor>,
                                     @Named("bt_name") bluetoothName: String): ThingsBluetoothClient {
        return ThingsBluetoothClient(context, bluetoothName, sensors, 1000)
    }

    @Provides
    @Singleton
    fun provideEmgSensors(): List<EmgSensor> {
        return if (DeviceConfig.useThingsConfig) {
            listOf(AdcEmgSensor(context))
        } else {
            listOf(DummyEmgSensor())
        }
    }

}