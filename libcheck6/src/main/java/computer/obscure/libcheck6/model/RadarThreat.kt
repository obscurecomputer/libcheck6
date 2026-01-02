package computer.obscure.libcheck6.model

import kotlin.math.roundToInt

/**
 * A data class to hold the parsed radar threat information.
 * Includes computed properties for imperial units.
 */
data class RadarThreat(
    val threatId: Int,
    val speedKph: Int,
    val distanceMeters: Int
) {
    val speedMph: Int
        get() = (speedKph * 0.621371).roundToInt()

    val distanceFeet: Int
        get() = (distanceMeters * 3.28084).roundToInt()
}
