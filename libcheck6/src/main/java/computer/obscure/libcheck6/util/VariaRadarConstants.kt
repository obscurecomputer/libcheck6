package computer.obscure.libcheck6.util

import java.util.UUID

/**
 * Contains constants related to the Garmin Varia Radar service.
 */
object VariaRadarConstants {
    val RADAR_SERVICE_UUID: UUID = UUID.fromString("6a4e3200-667b-11e3-949a-0800200c9a66")
    val RADAR_CHARACTERISTIC_UUID: UUID = UUID.fromString("6a4e3203-667b-11e3-949a-0800200c9a66")
    val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
}
