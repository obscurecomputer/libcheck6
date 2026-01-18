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

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Represents a snapshot of radar detections at a specific point in time.
 *
 * A detection session captures a group of [RadarThreat] objects along with a timestamp,
 * making it useful for recording and reviewing past detection events. This is particularly
 * helpful for logging rides, analyzing traffic patterns, or displaying detection history
 * in a user interface.
 *
 * ## Default Behavior
 *
 * By default, both [id] and [timestamp] are set to the current system time when the
 * session is created. This provides unique identification and automatic timestamping.
 *
 * ## Example
 *
 * ```kotlin
 * // Create a session from current radar data
 * val session = DetectionSession(threats = currentThreats)
 *
 * // Display in UI
 * println("Detection at ${session.getFormattedTimestamp()}: ${session.threats.size} threats")
 *
 * // Store for later analysis
 * sessionHistory.add(session)
 * ```
 *
 * @property id A unique identifier for this session, defaults to current system time in milliseconds.
 * @property timestamp The time this session was created, in milliseconds since epoch.
 *                     Defaults to the same value as [id].
 * @property threats The list of [RadarThreat] objects detected at this moment.
 * @constructor Creates a new [DetectionSession] with the specified properties.
 */
data class DetectionSession(
    val id: Long = System.currentTimeMillis(),
    val timestamp: Long = id,
    val threats: List<RadarThreat>
) {
    /**
     * Returns a human-readable formatted string of the session timestamp.
     *
     * The format used is "MMM dd, yyyy, hh:mm a" (e.g., "Jan 15, 2026, 02:30 PM")
     * using the device's default locale.
     *
     * @return A formatted date/time string representing when this session was recorded.
     */
    fun getFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
