package at.fhooe.mc.emg.client.things

import android.app.Activity
import android.os.Bundle
import android.util.Log
import at.fhooe.mc.emg.messaging.EmgMessaging
import com.google.android.things.pio.PeripheralManagerService

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    private lateinit var btClient: ThingsBluetoothClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Setup EMG client with version ${getString(R.string.client_emg_version)}")
        btClient = ThingsBluetoothClient(PeripheralManagerService())

        // Ugly way to set the message interval to 1 second - TODO remove later
        btClient.handleMessage(EmgMessaging.buildFrequencyMessage(1.toDouble()))
    }

    override fun onStart() {
        super.onStart()
        btClient.start()
        Log.d(TAG, "EmgClient started")
    }

    override fun onStop() {
        super.onStop()
        btClient.stop()
        Log.d(TAG, "Shutdown EmgClient")
    }

    companion object {

        private const val TAG = "EmgThings"
    }
}
