package at.fhooe.mc.emg.client.things.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import at.fhooe.mc.emg.client.things.R
import at.fhooe.mc.emg.client.things.bluetooth.RxEmgBluetoothConnection
import at.fhooe.mc.emg.client.things.client.ThingsBluetoothClient
import at.fhooe.mc.emg.client.things.sensing.DummyEmgSensor
import at.fhooe.mc.emg.client.things.util.ThingsUtils
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
class MainActivity : Activity() {

    private val txtDataOutput: TextView by bindView(R.id.txtDataOutput)
    private val txtLogging: TextView by bindView(R.id.txtLogging)
    private val txtConnectionInfo: TextView by bindView(R.id.txtConnection)
    private val btnReload: Button by bindView(R.id.btnReload)

    private lateinit var btClient: ThingsBluetoothClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        log("Setup EMG client with version ${getString(R.string.client_emg_version)}")
        btClient = ThingsBluetoothClient(DummyEmgSensor(),
                RxEmgBluetoothConnection(this, "AndroidThingsBluetoothClient"),
                1000)
        setupConnectionInfo()

        // TODO Remove in production mode
        // ---------------------------------------------------------
        btClient.setDebugListener {
            Single.fromCallable {
                txtDataOutput.text = getString(R.string.data_provided, it)
            }.subscribeOn(AndroidSchedulers.mainThread()).subscribe()
        }
        btClient.attachDebugView(txtLogging)
        btnReload.setOnClickListener {
            setupConnectionInfo()
            Toast.makeText(this, "Connection info reloaded!", Toast.LENGTH_SHORT).show()
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

    private fun log(s: String) {
        Log.d(TAG, s)
        txtLogging.append("$s\n")
    }

    private fun setupConnectionInfo() {

        val macWifi = ThingsUtils.getMACAddress("wlan0")
        val macEth = ThingsUtils.getMACAddress("eth0")
        val btMac = ThingsUtils.getBluetoothMacAddress(this)
        val ipv4 = ThingsUtils.getIPAddress(true)
        val ipv6 = ThingsUtils.getIPAddress(false)

        val out = "Mac BT: $btMac\nMac Wifi: $macWifi\nMac Eth: $macEth\nIPv4: $ipv4\nIPv6: $ipv6"
        txtConnectionInfo.text = out
    }

    companion object {

        private const val TAG = "EmgThings"
    }
}
