package at.fhooe.mc.emg.client.things.update

import com.google.android.things.update.UpdateManager
import com.google.android.things.update.UpdatePolicy
import java.util.concurrent.TimeUnit

/**
 * @author Martin Macheiner
 * Date: 31.01.2018.
 */
class ThingsEmgUpdateManager : EmgUpdateManager {

    private val updateManager: UpdateManager = UpdateManager()

    var policy: Int = UpdateManager.POLICY_APPLY_AND_REBOOT

    override fun setPolicy(withReboot: Boolean, intervalHours: Long) {
        policy = if (withReboot) UpdateManager.POLICY_APPLY_AND_REBOOT else UpdateManager.POLICY_CHECKS_ONLY
        updateManager.setPolicy(UpdatePolicy.Builder()
                .setPolicy(policy)
                .setApplyDeadline(intervalHours, TimeUnit.HOURS)
                .build())
    }

    override fun checkForUpdates() {
        updateManager.performUpdateNow(policy)
    }

}