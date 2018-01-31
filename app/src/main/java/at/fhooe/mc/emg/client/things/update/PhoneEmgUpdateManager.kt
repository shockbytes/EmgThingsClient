package at.fhooe.mc.emg.client.things.update

import android.util.Log

/**
 * @author Martin Macheiner
 * Date: 31.01.2018.
 */

class PhoneEmgUpdateManager: EmgUpdateManager {

    override fun setPolicy(withReboot: Boolean, intervalHours: Long) {
        // Do nothing
    }

    override fun checkForUpdates() {
        // Do nothing
        Log.d("EmgThings", "Check for updates")
    }

}