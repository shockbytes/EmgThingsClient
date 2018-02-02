package at.fhooe.mc.emg.client.things.dagger

import android.content.Context
import at.fhooe.mc.emg.client.things.BuildConfig
import at.fhooe.mc.emg.client.things.client.ThingsBluetoothClient
import at.fhooe.mc.emg.client.things.sensing.DummyEmgSensor
import at.fhooe.mc.emg.client.things.sensing.EmgSensor
import at.fhooe.mc.emg.client.things.sensing.I2cAdcEmgSensor
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
    fun provideThingsBluetoothClient(sensor: EmgSensor,
                                     @Named("bt_name") bluetoothName: String): ThingsBluetoothClient {
        return ThingsBluetoothClient(context, bluetoothName, sensor)
    }

    @Provides
    @Singleton
    fun provideEmgSensors(): EmgSensor {
        return if (BuildConfig.BUILD_TYPE == "things") {
            I2cAdcEmgSensor()
        } else {
            DummyEmgSensor()
        }
    }

}