/*
 * Copyright (c) Obscure Computer 2026.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package computer.obscure.libcheck6.model

import kotlin.math.roundToInt

/**
 * Represents a single detected threat (vehicle) from the cycling radar.
 *
 * Each threat object contains information about a vehicle detected behind the cyclist,
 * including its unique identifier, speed, and distance. The radar continuously tracks
 * threats and assigns consistent IDs to allow tracking the same vehicle across multiple
 * data updates.
 *
 * ## Unit Conversions
 *
 * The radar natively reports data in metric units. This class provides computed properties
 * for imperial unit conversions:
 * - [speedMph] converts [speedKph] to miles per hour
 * - [distanceFeet] converts [distanceMeters] to feet
 *
 * ## Example
 *
 * ```kotlin
 * manager.onRadarDataReceived = { threats ->
 *     threats.forEach { threat ->
 *         // Metric units
 *         println("Vehicle ${threat.threatId}: ${threat.distanceMeters}m at ${threat.speedKph} km/h")
 *
 *         // Imperial units
 *         println("Vehicle ${threat.threatId}: ${threat.distanceFeet}ft at ${threat.speedMph} mph")
 *     }
 * }
 * ```
 *
 * @property threatId A unique identifier for this threat. The radar maintains consistent IDs
 *                    for tracked vehicles across multiple updates, allowing applications to
 *                    track individual vehicles over time.
 * @property speedKph The approaching speed of the threat in kilometers per hour.
 * @property distanceMeters The distance from the radar to the threat in meters.
 * @constructor Creates a new [RadarThreat] with the specified properties.
 */
data class RadarThreat(
    val threatId: Int,
    val speedKph: Int,
    val distanceMeters: Int
) {
    /**
     * The approaching speed of the threat converted to miles per hour.
     *
     * This is a computed property that converts [speedKph] using the factor 0.621371.
     */
    val speedMph: Int
        get() = (speedKph * 0.621371).roundToInt()

    /**
     * The distance to the threat converted to feet.
     *
     * This is a computed property that converts [distanceMeters] using the factor 3.28084.
     */
    val distanceFeet: Int
        get() = (distanceMeters * 3.28084).roundToInt()
}
