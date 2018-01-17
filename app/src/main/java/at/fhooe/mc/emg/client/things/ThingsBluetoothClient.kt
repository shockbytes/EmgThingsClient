package at.fhooe.mc.emg.client.things

import at.fhooe.mc.emg.client.EmgClient
import at.fhooe.mc.emg.messaging.EmgMessaging
import com.google.android.things.pio.PeripheralManagerService

/**
 * @author Martin Macheiner
 * Date: 17.01.2018.
 */

class ThingsBluetoothClient(private val peripheral: PeripheralManagerService) : EmgClient() {

    override val protocolVersion = EmgMessaging.ProtocolVersion.V2

    override fun provideData(): List<Double> {
        // TODO
        return listOf(0.toDouble())
    }

    override fun send(data: String) {
        // TODO
    }

    override fun setupTransmission() {
        // TODO
    }

    override fun tearDown() {
        // TODO
    }

}