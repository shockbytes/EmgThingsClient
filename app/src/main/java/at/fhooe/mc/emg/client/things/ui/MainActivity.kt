package at.fhooe.mc.emg.client.things.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import at.fhooe.mc.emg.client.things.R
import at.fhooe.mc.emg.client.things.client.ThingsBluetoothClient
import at.fhooe.mc.emg.client.things.core.EmgThingsApp
import at.fhooe.mc.emg.client.things.update.EmgUpdateManager
import at.fhooe.mc.emg.client.things.util.ThingsUtils
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.google.android.things.device.DeviceManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotterknife.bindView
import java.util.*
import javax.inject.Inject

class MainActivity : Activity() {

    private val txtLogging: TextView by bindView(R.id.txtLogging)
    private val txtDataOutput: TextView by bindView(R.id.txtData)
    private val scrollViewLogging: ScrollView by bindView(R.id.scrollViewLogging)

    private var unbinder: Unbinder? = null
    private var debugDataDisposable: Disposable? = null

    @Inject
    protected lateinit var btClient: ThingsBluetoothClient

    @Inject
    protected lateinit var updateManager: EmgUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as EmgThingsApp).appComponent.inject(this)
        setContentView(R.layout.activity_main)
        unbinder = ButterKnife.bind(this)
        startupLog()
        setupDebugComponents()
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
        debugDataDisposable?.dispose()
    }

    @OnClick(R.id.btnDiscoverable)
    protected fun onClickMakeDiscoverable() {

        val discoverableSeconds = 60 * 5 // 5 minutes
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                .putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discoverableSeconds)
        startActivity(discoverableIntent)
        showToast("Device discoverable for $discoverableSeconds seconds.")
    }

    @OnClick(R.id.btnIp)
    protected fun onClickLoadIp() {
        log("Ip address: ${ThingsUtils.getIPAddress(true)}")
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
        showToast("Check for updates...")
    }

    private fun log(s: String) {
        Log.d("EmgThings", s)
        txtLogging.append("$s\n")
        scrollViewLogging.fullScroll(ScrollView.FOCUS_DOWN)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun startupLog() {
        log("Setup EMG client with version ${getString(R.string.client_emg_version)}")
        log("EmgClient started at ${Date(System.currentTimeMillis())}")
        log("Ip address: ${ThingsUtils.getIPAddress(true)}")
    }

    private fun setupDebugComponents() {
        btClient.debugLogView = txtLogging

        debugDataDisposable = btClient.debugDataSubject
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { txtDataOutput.text = it }
    }

}
