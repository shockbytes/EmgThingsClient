package at.fhooe.mc.emg.client.things.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import at.fhooe.mc.emg.client.things.R
import at.fhooe.mc.emg.client.things.client.ThingsBluetoothClient
import at.fhooe.mc.emg.client.things.sensing.DummyEmgSensor
import at.fhooe.mc.emg.client.things.sensing.EmgSensor
import at.fhooe.mc.emg.client.things.sensing.EmgSensorProvider
import at.fhooe.mc.emg.client.things.update.EmgUpdateManager
import at.fhooe.mc.emg.client.things.update.PhoneEmgUpdateManager
import com.google.android.things.device.DeviceManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotterknife.bindView
import java.util.*


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
class MainActivity : Activity(), EmgSensorProvider {

    private val txtDataOutput: TextView by bindView(R.id.txtDataOutput)

    private val txtLogging: TextView by bindView(R.id.txtLogging)
    private val btnDiscoverable: Button by bindView(R.id.btnDiscoverable)
    private val btnReboot: Button by bindView(R.id.btnReboot)
    private val btnUpdates: Button by bindView(R.id.btnUpdates)

    private lateinit var btClient: ThingsBluetoothClient
    private lateinit var updateManager: EmgUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        log("Setup EMG client with version ${getString(R.string.client_emg_version)}")

        btClient = ThingsBluetoothClient(this, "EmgBluetooth",
                provideEmgSensor(), 1000)
        btClient.attachDebugView(txtLogging)

        updateManager = PhoneEmgUpdateManager()
        updateManager.setPolicy(true, 24)

        btnReboot.setOnClickListener { confirmReboot() }
        btnDiscoverable.setOnClickListener { makeDiscoverable() }
        btnUpdates.setOnClickListener { checkForUpdates() }

        // TODO Remove in production mode
        // ---------------------------------------------------------
        btClient.setDebugListener {
            Single.fromCallable {
                txtDataOutput.text = getString(R.string.data_provided, it)
            }.subscribeOn(AndroidSchedulers.mainThread()).subscribe()
        }
        // ---------------------------------------------------------
    }

    override fun onStart() {
        super.onStart()
        btClient.start()

        val d = Date(System.currentTimeMillis())
        log("EmgClient started at $d")
    }

    override fun onStop() {
        super.onStop()
        btClient.stop()
    }

    override fun provideEmgSensor(): EmgSensor {
        return DummyEmgSensor()
    }

    private fun log(s: String) {
        Log.d(TAG, s)
        txtLogging.append("$s\n")
    }

    private fun makeDiscoverable() {

        val discoverableSeconds = 60 * 5 // 5 minutes
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discoverableSeconds)
        startActivity(discoverableIntent)
    }

    private fun confirmReboot() {
        ConfirmRebootDialogFragment.newInstance()
                .setOnConfirmClickListener {
                    DeviceManager().reboot()
                }
                .show(fragmentManager, "show-confirm-reboot-dialog")
    }

    private fun checkForUpdates() {
        updateManager.checkForUpdates()
    }

    companion object {

        private const val TAG = "EmgThings"
    }
}
