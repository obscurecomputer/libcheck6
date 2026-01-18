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

import android.bluetooth.BluetoothDevice

/**
 * Represents a Bluetooth device discovered during BLE scanning.
 *
 * This class wraps a [BluetoothDevice] with additional metadata useful for managing
 * a scan list UI, including whether the device is currently visible and when it was
 * last detected. This allows applications to show devices that have gone out of range
 * differently from those currently available.
 *
 * ## Usage in Scanning UI
 *
 * ```kotlin
 * // Update scan list with new discovery
 * val scannedDevice = ScannedDevice(
 *     device = discoveredDevice,
 *     isCurrentlyFound = true,
 *     lastSeenTimestamp = System.currentTimeMillis()
 * )
 *
 * // Mark device as no longer visible
 * val staleDevice = scannedDevice.copy(isCurrentlyFound = false)
 * ```
 *
 * @property device The underlying Android [BluetoothDevice] object.
 * @property isCurrentlyFound `true` if the device was found in the most recent scan cycle,
 *                            `false` if it was previously found but is no longer detected.
 * @property lastSeenTimestamp The timestamp (in milliseconds since epoch) when this device
 *                             was last detected during scanning. Defaults to 0.
 * @constructor Creates a new [ScannedDevice] with the specified properties.
 */
data class ScannedDevice(
    val device: BluetoothDevice,
    val isCurrentlyFound: Boolean,
    val lastSeenTimestamp: Long = 0L
)
