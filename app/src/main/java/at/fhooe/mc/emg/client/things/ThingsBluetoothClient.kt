package at.fhooe.mc.emg.client.things

import android.util.Log
import at.fhooe.mc.emg.client.EmgClient
import at.fhooe.mc.emg.messaging.EmgMessaging
import com.google.android.things.pio.PeripheralManagerService
import java.util.*


/**
 * @author Martin Macheiner
 * Date: 17.01.2018.
 *
 * Running on a Raspberry Pi 3 using an external ADC to read the EMG value
 *
 */

class ThingsBluetoothClient(private val peripheral: PeripheralManagerService) : EmgClient() {

    override val protocolVersion = EmgMessaging.ProtocolVersion.V1

    //private var adc: Adc? = null

    override fun provideData(): List<Double> {

        //val data = (adc?.readChannel(0) ?: -1).toDouble()
        val data = Random().nextInt(50).toDouble()
        return listOf(data)
    }

    override fun send(data: String) {
        Log.wtf(TAG, data)

        // TODO Write over bluetooth connection

    }

    override fun setupTransmission() {

        // TODO Setup bluetooth

        // Setup adc converter for reading analog signal
        /*
        adc = I2cAdc.builder()
                .address(0)
                .fourSingleEnded()
                .withConversionRate(100)
                .build()
        adc?.startConversions()
        */
    }

    override fun tearDown() {

        // TODO Teardown bluetooth connection

        // Teardown analog conversions
        /*
        adc?.stopConversions()
        adc?.close()
        */
    }

    companion object {

        private const val TAG = "EmgThings"
    }

}