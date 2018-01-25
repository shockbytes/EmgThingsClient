package at.fhooe.mc.emg.client.things.sensing

/**
 * @author Martin Macheiner
 * Date: 24.01.2018.
 */

class DummyEmgSensor: EmgSensor {

    var data = 0.0

    override fun provideEmgValue() = data++

    override fun setup() {
        // Do nothing
    }

    override fun tearDown() {
        // Do nothing
    }

}