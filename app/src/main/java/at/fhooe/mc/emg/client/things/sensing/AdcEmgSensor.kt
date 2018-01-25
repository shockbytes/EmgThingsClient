package at.fhooe.mc.emg.client.things.sensing

import nz.geek.android.things.drivers.adc.Adc
import nz.geek.android.things.drivers.adc.I2cAdc

/**
 * @author Martin Macheiner
 * Date: 24.01.2018.
 */

class AdcEmgSensor: EmgSensor {

    private var adc: Adc? = null

    override fun provideEmgValue() = (adc?.readChannel(0) ?: -1).toDouble()

    override fun setup() {

        // Setup adc converter for reading analog signal
        adc = I2cAdc.builder()
                .address(0x48)
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