package at.fhooe.mc.emg.client.things.client

import android.widget.TextView
import at.fhooe.mc.emg.client.EmgClient
import at.fhooe.mc.emg.client.things.bluetooth.EmgBluetoothConnection
import at.fhooe.mc.emg.client.things.sensing.EmgSensor
import at.fhooe.mc.emg.messaging.EmgMessaging
import io.reactivex.functions.Consumer

/**
 * @author Martin Macheiner
 * Date: 17.01.2018.
 *
 * Running on a Raspberry Pi 3 using an external ADC to read the EMG value and send it over
 * Bluetooth to the receiving application
 *
 */

class ThingsBluetoothClient(private val emgSensor: EmgSensor,
                            private val bluetoothConnection: EmgBluetoothConnection,
                            initialPeriod: Long = 100) : EmgClient() {

    override val protocolVersion = EmgMessaging.ProtocolVersion.V1

    private var debugListener: ((String) -> Unit)? = null
    private var debugView: TextView? = null

    init {
        period = initialPeriod
    }

    override fun provideData(): List<Double> {
        return listOf(emgSensor.provideEmgValue())
    }

    override fun send(data: String) {
        debugListener?.invoke(data)
        bluetoothConnection.sendMessage(data)
    }

    override fun setupTransmission() {
        emgSensor.setup()
        bluetoothConnection.setup(Consumer {
            debugView?.append("Connected to: $it")
        }, Consumer {
            debugView?.append("Connection error: $it")
        })
        // If connected request read access and integrate #handleMessage()
    }

    override fun tearDown() {
        emgSensor.tearDown()
        bluetoothConnection.tearDown()
    }

    fun attachDebugView(textView: TextView) {
        this.debugView = textView
    }

    // TODO Remove in production code
    // --------------------------------------------------
    fun setDebugListener(listener: (String) -> Unit) {
        this.debugListener = listener
    }
    // --------------------------------------------------

}