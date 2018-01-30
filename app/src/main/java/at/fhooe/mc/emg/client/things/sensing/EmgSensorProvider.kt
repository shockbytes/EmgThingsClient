package at.fhooe.mc.emg.client.things.sensing

/**
 * @author Martin Macheiner
 * Date: 29.01.2018.
 */

interface EmgSensorProvider {

    fun provideEmgSensor(): EmgSensor
}