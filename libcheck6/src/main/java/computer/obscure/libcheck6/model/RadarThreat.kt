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
