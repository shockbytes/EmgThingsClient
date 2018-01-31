package at.fhooe.mc.emg.client.things.update

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
    }

}