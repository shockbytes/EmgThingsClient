package at.fhooe.mc.emg.client.things.client

import android.content.Context
import android.widget.TextView
import at.fhooe.mc.emg.client.EmgClient
import at.fhooe.mc.emg.client.things.bluetooth.EmgBluetoothConnection
import at.fhooe.mc.emg.client.things.bluetooth.RxEmgBluetoothConnection
import at.fhooe.mc.emg.client.things.sensing.EmgSensor
import at.fhooe.mc.emg.messaging.EmgMessaging
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import java.io.IOException


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
                            private val emgSensor: EmgSensor,
                            initialPeriod: Long = 100) : EmgClient() {

    override val protocolVersion = EmgMessaging.ProtocolVersion.V1

    var debugLogView: TextView? = null
    var debugDataSubject: PublishSubject<String> = PublishSubject.create()

    private var msgDisposable: Disposable? = null
    private var gpioLed: Gpio? = null
    private val bluetoothConnection: EmgBluetoothConnection

    init {
        period = initialPeriod
        bluetoothConnection = RxEmgBluetoothConnection(context, bluetoothName)
    }

    override fun provideData(): List<Double> {
        return emgSensor.provideEmgValues()
    }

    override fun send(data: String) {
        debugDataSubject.onNext(data)
        bluetoothConnection.sendMessage(data)
    }

    override fun setupTransmission() {
        emgSensor.setup()
        setupLed()
        bluetoothConnection.setup(Consumer {
            debugLogView?.append("Connected to: $it\n")
            gpioLed?.value = true
            startDataTransfer()
        }, Consumer {
            debugLogView?.append("Bluetooth connection error: $it\n")
            gpioLed?.value = false
        })
        // If connected request read access and integrate #handleMessage()
    }

    override fun tearDown() {
        emgSensor.tearDown()
        bluetoothConnection.tearDown()
        msgDisposable?.dispose()
        gpioLed?.close()
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
        debugLogView?.append("Remote device disconnected\n")
        msgDisposable?.dispose()
        gpioLed?.value = false
    }

    private fun setupLed() {

        try {
            val manager = PeripheralManagerService()
            gpioLed = manager.openGpio("BCM7")
            gpioLed?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            gpioLed?.setActiveType(Gpio.ACTIVE_HIGH)
        } catch (e: IOException) {
            e.printStackTrace()
            debugLogView?.append("Unable to open LED port")
        }
    }

}