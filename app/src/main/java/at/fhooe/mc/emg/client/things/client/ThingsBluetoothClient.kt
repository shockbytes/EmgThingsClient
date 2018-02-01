package at.fhooe.mc.emg.client.things.client

import android.content.Context
import android.widget.TextView
import at.fhooe.mc.emg.client.EmgClient
import at.fhooe.mc.emg.client.things.bluetooth.EmgBluetoothConnection
import at.fhooe.mc.emg.client.things.bluetooth.RxEmgBluetoothConnection
import at.fhooe.mc.emg.client.things.sensing.EmgSensor
import at.fhooe.mc.emg.messaging.EmgMessaging
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * @author Martin Macheiner
 * Date: 17.01.2018.
 *
 * Running on a Raspberry Pi 3 using an external ADC to read the EMG value and send it over
 * Bluetooth to the receiving application
 *
 */

class ThingsBluetoothClient(context: Context,
                            bluetoothName: String,
                            private val emgSensors: List<EmgSensor>,
                            initialPeriod: Long = 100) : EmgClient() {

    override val protocolVersion = EmgMessaging.ProtocolVersion.V1

    private val bluetoothConnection: EmgBluetoothConnection

    private var debugListener: ((String) -> Unit)? = null
    private var debugView: TextView? = null
    private var msgDisposable: Disposable? = null

    init {
        period = initialPeriod
        bluetoothConnection = RxEmgBluetoothConnection(context, bluetoothName)
    }

    override fun provideData(): List<Double> {
        return emgSensors.map { it.provideEmgValue() }
    }

    override fun send(data: String) {
        debugListener?.invoke(data)
        bluetoothConnection.sendMessage(data)
    }

    override fun setupTransmission() {
        emgSensors.forEach { it.setup() }
        bluetoothConnection.setup(Consumer {
            debugView?.append("Connected to: $it\n")
            startDataTransfer()
        }, Consumer {
            debugView?.append("Bluetooth connection error: $it\n")
        })
        // If connected request read access and integrate #handleMessage()
    }

    override fun tearDown() {
        emgSensors.forEach { it.tearDown() }
        bluetoothConnection.tearDown()
        msgDisposable?.dispose()
    }

    fun attachDebugView(textView: TextView) {
        this.debugView = textView
    }

    private fun startDataTransfer() {
        startTransmission()

        // If connected request read access and integrate #handleMessage()
        msgDisposable = bluetoothConnection.subscribeToIncomingMessages().subscribe({
            handleMessage(it)
        }, {
            it.printStackTrace()
            closeConnectionAfterDisconnect()
        })
    }

    private fun closeConnectionAfterDisconnect() {
        stopTransmission()
        bluetoothConnection.closeAfterDisconnect()
        debugView?.append("Disconnected from remote device\n")
        msgDisposable?.dispose()
    }

    // TODO Remove in production code
    // --------------------------------------------------
    fun setDebugListener(listener: (String) -> Unit) {
        this.debugListener = listener
    }
    // --------------------------------------------------

}