package at.fhooe.mc.emg.client.things.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import at.fhooe.mc.emg.client.things.R
import at.fhooe.mc.emg.client.things.client.ThingsBluetoothClient
import at.fhooe.mc.emg.client.things.core.EmgThingsApp
import at.fhooe.mc.emg.client.things.update.EmgUpdateManager
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.google.android.things.device.DeviceManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotterknife.bindView
import java.util.*
import javax.inject.Inject

class MainActivity : Activity() {

    private val txtDataOutput: TextView by bindView(R.id.txtDataOutput)
    private val txtLogging: TextView by bindView(R.id.txtLogging)

    private var unbinder: Unbinder? = null

    @Inject
    protected lateinit var btClient: ThingsBluetoothClient

    @Inject
    protected lateinit var updateManager: EmgUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as EmgThingsApp).appComponent.inject(this)
        setContentView(R.layout.activity_main)
        unbinder = ButterKnife.bind(this)

        log("Setup EMG client with version ${getString(R.string.client_emg_version)}")
        log("EmgClient started at ${Date(System.currentTimeMillis())}")

        btClient.attachDebugView(txtLogging)
        updateManager.setPolicy(true, 24)

        // Remove after testing ADC sensing
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
    }

    override fun onStop() {
        super.onStop()
        btClient.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder?.unbind()
    }

    @OnClick(R.id.btnDiscoverable)
    protected fun onClickMakeDiscoverable() {

        val discoverableSeconds = 60 * 5 // 5 minutes
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discoverableSeconds)
        startActivity(discoverableIntent)
    }

    @OnClick(R.id.btnReboot)
    protected fun onClickAskForReboot() {
        ConfirmRebootDialogFragment.newInstance()
                .setOnConfirmClickListener {
                    DeviceManager().reboot()
                }
                .show(fragmentManager, "show-confirm-reboot-dialog")
    }

    @OnClick(R.id.btnUpdates)
    protected fun onClickCheckForUpdates() {
        updateManager.checkForUpdates()
    }

    private fun log(s: String) {
        Log.d("EmgThings", s)
        txtLogging.append("$s\n")
    }

}
