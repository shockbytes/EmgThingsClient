package at.fhooe.mc.emg.client.things.update

/**
 * @author Martin Macheiner
 * Date: 31.01.2018.
 */

interface EmgUpdateManager {

    fun setPolicy(withReboot: Boolean, intervalHours: Long)

    fun checkForUpdates()
}