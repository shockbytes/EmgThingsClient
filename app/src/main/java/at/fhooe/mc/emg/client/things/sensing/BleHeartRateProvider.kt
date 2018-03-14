package at.fhooe.mc.emg.client.things.sensing

import android.content.Context
import android.util.Log
import android.widget.Toast
import at.fhooe.mc.emg.client.sensing.heart.HeartRateProvider
import com.polidea.rxandroidble.RxBleClient
import com.polidea.rxandroidble.RxBleConnection
import com.polidea.rxandroidble.RxBleDevice
import com.polidea.rxandroidble.helpers.ValueInterpreter
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import rx.Subscription

/**
 * @author  Martin Macheiner
 * Date:    07.02.2018.
 */

class BleHeartRateProvider(private val context: Context,
                           private val bleClient: RxBleClient,
                           private val macAddress: String = "D0:AD:40:8B:0A:D6") : HeartRateProvider {

    private val characteristic: java.util.UUID = java.util.UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")

    private var heartRateCallback: ((Int) -> Unit)? = null
    private var stateCallback: ((HeartRateProvider.ConnectionState) -> Unit)? = null

    private var bleSubscription: Subscription? = null
    private var stateSubscription: Subscription? = null


    override fun subscribeForHeartRateUpdates(callback: (Int) -> Unit) {
        this.heartRateCallback = callback
    }

    override fun subscribeToConnectionChanges(callback: (HeartRateProvider.ConnectionState) -> Unit) {
        this.stateCallback = callback
    }

    override fun unsubscribeToConnectionChanges() {
        stateCallback = null
    }

    override fun start() {
        val device: RxBleDevice = bleClient.getBleDevice(macAddress)
        observeConnectionChanges(device)
        setupHeartRateNotification(device)
    }

    override fun stop() {

        // Force disconnection message
        if (bleSubscription != null) {
            stateCallback?.invoke(HeartRateProvider.ConnectionState.DISCONNECTED)
        }

        bleSubscription?.unsubscribe()
        bleSubscription = null

        stateSubscription?.unsubscribe()
        stateSubscription = null

        heartRateCallback = null
    }

    private fun observeConnectionChanges(device: RxBleDevice) {
        stateSubscription = device.observeConnectionStateChanges()
                .subscribeOn(RxJavaInterop.toV1Scheduler(Schedulers.io()))
                .observeOn(RxJavaInterop.toV1Scheduler(AndroidSchedulers.mainThread()))
                .subscribe {
                    when (it) {
                        RxBleConnection.RxBleConnectionState.CONNECTING -> {
                            Log.d(TAG, "Connecting")
                            stateCallback?.invoke(HeartRateProvider.ConnectionState.CONNECTING)
                        }
                        RxBleConnection.RxBleConnectionState.CONNECTED -> {
                            Log.d(TAG, "Connected")
                            stateCallback?.invoke(HeartRateProvider.ConnectionState.CONNECTED)
                        }
                        RxBleConnection.RxBleConnectionState.DISCONNECTED -> {
                            Log.d(TAG, "Disconnected")
                            stateCallback?.invoke(HeartRateProvider.ConnectionState.DISCONNECTED)
                        }
                        RxBleConnection.RxBleConnectionState.DISCONNECTING -> {
                            Log.d(TAG, "Disconnecting")
                        }
                        else -> {
                            // This state should not happen
                            Log.d(TAG, "Default branch state")
                        }
                    }
                }
    }

    private fun setupHeartRateNotification(device: RxBleDevice) {
        bleSubscription = device.establishConnection(true)
                .doOnUnsubscribe(this::stop)
                .flatMap { rxBleConnection -> rxBleConnection.setupNotification(characteristic) }
                .doOnNext {
                    // Notification has been set up
                }
                .flatMap { notificationObservable -> notificationObservable } // <-- Notification has been set up, now observe value changes.
                .map { ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT8, 1) }
                .subscribeOn(RxJavaInterop.toV1Scheduler(Schedulers.io()))
                .observeOn(RxJavaInterop.toV1Scheduler(AndroidSchedulers.mainThread()))
                .subscribe({ hr ->
                    heartRateCallback?.invoke(hr)
                }, { t: Throwable ->
                    t.printStackTrace()
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
                })
    }

    companion object {

        private const val TAG = "BleHeartRate"
    }

}