package at.fhooe.mc.emg.client.things.sensing

/**
 * @author Martin Macheiner
 * Date: 24.01.2018.
 */

interface EmgSensor {

    fun provideEmgValue(): Double

    fun setup()

    fun tearDown()

}