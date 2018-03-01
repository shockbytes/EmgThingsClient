package at.fhooe.mc.emg.client.things.sensing

import at.fhooe.mc.emg.client.sensing.EmgSensor
import nz.geek.android.things.drivers.adc.I2cAdc

/**
 * @author Martin Macheiner
 * Date: 24.01.2018.
 */

class I2cAdcEmgSensor(private val channels: List<Int> = listOf(0)): EmgSensor {

    private var adc: I2cAdc? = null

    override fun provideEmgValues(): List<Double> {
        return channels.map { channel -> (adc?.readChannel(channel) ?: -1).toDouble() }
    }

    override fun setup() {

        // Setup adc converter for reading analog signal
        adc = I2cAdc.builder()
                .address(0x00)
                .fourSingleEnded()
                .withConversionRate(100)
                .build()
        adc?.startConversions()
    }

    override fun tearDown() {

        // Teardown analog conversions
        adc?.stopConversions()
        adc?.close()
    }


}