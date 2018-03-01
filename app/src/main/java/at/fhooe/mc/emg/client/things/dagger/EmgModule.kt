package at.fhooe.mc.emg.client.things.dagger

import android.content.Context
import at.fhooe.mc.emg.client.sensing.EmgSensor
import at.fhooe.mc.emg.client.sensing.heart.HeartRateProvider
import at.fhooe.mc.emg.client.things.BuildConfig
import at.fhooe.mc.emg.client.things.client.ThingsBluetoothClient
import at.fhooe.mc.emg.client.things.sensing.BleHeartRateProvider
import at.fhooe.mc.emg.client.things.sensing.DummyEmgSensor
import at.fhooe.mc.emg.client.things.sensing.I2cAdcEmgSensor
import com.polidea.rxandroidble.RxBleClient
import com.polidea.rxandroidble.internal.RxBleLog
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
    fun provideBleClient(): RxBleClient {
        val bleClient = RxBleClient.create(context)
        RxBleClient.setLogLevel(RxBleLog.VERBOSE)
        return bleClient
    }

    @Provides
    @Singleton
    fun provideHeartRateProvider(bleClient: RxBleClient): HeartRateProvider {
        return BleHeartRateProvider(context, bleClient)
    }

    @Provides
    @Singleton
    fun provideThingsBluetoothClient(sensor: EmgSensor,
                                     heartRateProvider: HeartRateProvider,
                                     @Named("bt_name") bluetoothName: String): ThingsBluetoothClient {
        return ThingsBluetoothClient(context, bluetoothName, sensor, heartRateProvider)
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