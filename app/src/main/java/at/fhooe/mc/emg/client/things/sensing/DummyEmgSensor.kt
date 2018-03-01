package at.fhooe.mc.emg.client.things.sensing

import at.fhooe.mc.emg.client.sensing.EmgSensor

/**
 * @author Martin Macheiner
 * Date: 24.01.2018.
 */

class DummyEmgSensor: EmgSensor {

    var data = 0.0

    override fun provideEmgValues() = listOf(data++)

    override fun setup() {
        // Do nothing
    }

    override fun tearDown() {
        // Do nothing
    }

}