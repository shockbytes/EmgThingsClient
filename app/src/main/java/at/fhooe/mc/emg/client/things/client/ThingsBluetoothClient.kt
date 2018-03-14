package at.fhooe.mc.emg.client.things.client

import android.content.Context
import android.widget.TextView
import at.fhooe.mc.emg.client.EmgClient
import at.fhooe.mc.emg.client.connection.EmgConnection
import at.fhooe.mc.emg.client.sensing.EmgSensor
import at.fhooe.mc.emg.client.sensing.heart.HeartRateProvider
import at.fhooe.mc.emg.client.things.connection.EmgBluetoothConnection
import at.fhooe.mc.emg.messaging.EmgMessageParser
import at.fhooe.mc.emg.messaging.MessageParser
import at.fhooe.mc.emg.messaging.model.EmgPacket


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
                            override val emgSensor: EmgSensor,
                            override val heartRateProvider: HeartRateProvider,
                            initialPeriod: Long = 100) : EmgClient() {

    override val connection: EmgConnection

    override var msgParser: MessageParser<EmgPacket> = EmgMessageParser(MessageParser.ProtocolVersion.V3)

    var debugLogView: TextView? = null

    init {
        period = initialPeriod
        connection = EmgBluetoothConnection(context, bluetoothName)
    }

    override fun cleanup() {
        heartRateProvider.unsubscribeToConnectionChanges()
    }

    override fun cleanupAfterDisconnect() {
        debugLogView?.append("Remote device disconnected\n")
    }

    override fun onConnected(device: String) {
        debugLogView?.append("Connected to: $device\n")
    }

    override fun onConnectionFailed(t: Throwable) {
        debugLogView?.append("Bluetooth connection error: ${t.localizedMessage}\n")
    }

    override fun setup() {
        heartRateProvider.subscribeToConnectionChanges {
            debugLogView?.append("Heart rate provider: ${it.name}\n")
        }
    }

}