package at.fhooe.mc.emg.client.things.connection

import android.bluetooth.BluetoothAdapter
import android.content.Context
import at.fhooe.mc.emg.client.connection.EmgConnection
import com.github.ivbaranov.rxbluetooth.BluetoothConnection
import com.github.ivbaranov.rxbluetooth.RxBluetooth
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.*


/**
 * @author Martin Macheiner
 * Date: 24.01.2018.
 */

class EmgBluetoothConnection(context: Context,
                             private val bluetoothName: String): EmgConnection {

    private var rxBluetooth = RxBluetooth(context)
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var connectedDeviceName: String? = null
    private var connection: BluetoothConnection? = null

    private val uuid = UUID.fromString("5f77cdab-8f48-4784-9958-d2736f4727c5")

    // --------------------------------------------
    private var successHandler: Consumer<String>? = null
    private var errorHandler: Consumer<Throwable>? = null

    private var connectionDisposable: Disposable? = null

    override fun sendMessage(msg: String) {
        // \n is needed, otherwise the receiving site won't recognize incoming stream
        connection?.send("$msg\n")
    }

    override fun setup(successHandler: Consumer<String>?, errorHandler: Consumer<Throwable>?) {
        this.successHandler = successHandler
        this.errorHandler = errorHandler

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            errorHandler?.accept(NullPointerException("BluetoothAdapter is not present on device!"))
            return
        }
        bluetoothAdapter?.name = bluetoothName

        // Enable Bluetooth without asking the user
        bluetoothAdapter?.enable()

        connectionDisposable = rxBluetooth
                .observeBluetoothSocket("things_client_bluetooth_socket", uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    connectedDeviceName = it.remoteDevice.name
                    connection = BluetoothConnection(it)
                    successHandler?.accept(it.remoteDevice.name)
                }, {
                    errorHandler?.accept(it)
                })
    }

    override fun subscribeToIncomingMessages(): Flowable<String> {
        return connection?.observeStringStream()
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?: Flowable.error(NullPointerException("Incoming messages - Connection is null!"))
    }

    override fun cleanupAfterDisconnect() {
        connectionDisposable?.dispose()
        connectedDeviceName = null
        connection?.closeConnection()
        setup(successHandler, errorHandler)
    }

    override fun tearDown() {
        connectionDisposable?.dispose()
        rxBluetooth.cancelDiscovery()
        connection?.closeConnection()
    }

}