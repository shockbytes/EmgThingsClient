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
import at.fhooe.mc.emg.messaging.model.EmgPacket
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.google.android.things.device.DeviceManager
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotterknife.bindView
import rx.Single
import java.util.*
import javax.inject.Inject

class MainActivity : Activity() {

    private val txtEmgData: TextView by bindView(R.id.txtEmg)
    private val txtPulseData: TextView by bindView(R.id.txtPulse)

    private val txtLogView: TextView by bindView(R.id.txtLogging)
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
                    DeviceManager.getInstance().reboot()
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
        txtLogView.append("$s\n")
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
        btClient.debugLogView = txtLogView

        btClient.debugDataListener = { packet: EmgPacket ->
            Single.fromCallable {
                txtEmgData.text = packet.channels[0].toString()
                txtPulseData.text = packet.heartRate.toString()
            }.subscribeOn(RxJavaInterop.toV1Scheduler(AndroidSchedulers.mainThread())).subscribe()
        }
    }

}
