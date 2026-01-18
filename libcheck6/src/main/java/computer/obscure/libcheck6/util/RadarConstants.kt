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

package computer.obscure.libcheck6.util

import java.util.UUID

/**
 * BLE UUIDs and constants for communicating with cycling radar devices.
 *
 * These UUIDs identify the GATT service and characteristics used by cycling radars
 * to transmit threat detection data. Used internally by [RadarManager] but exposed
 * for advanced use cases such as custom BLE scanning filters.
 *
 * > **Note:** These UUIDs are based on the Garmin Varia protocol. Other cycling radars
 * > using the same protocol should be compatible.
 *
 * ## BLE Service Structure
 *
 * ```
 * Service: RADAR_SERVICE_UUID (6a4e3200-667b-11e3-949a-0800200c9a66)
 *   └── Characteristic: RADAR_CHARACTERISTIC_UUID (6a4e3203-667b-11e3-949a-0800200c9a66)
 *         └── Descriptor: CCCD_UUID (standard Client Characteristic Configuration)
 * ```
 *
 * ## Usage for Scanning
 *
 * ```kotlin
 * val scanFilter = ScanFilter.Builder()
 *     .setServiceUuid(ParcelUuid(RadarConstants.RADAR_SERVICE_UUID))
 *     .build()
 * ```
 *
 * @see RadarManager
 */
object RadarConstants {
    /**
     * The UUID of the cycling radar GATT service.
     *
     * Use this UUID to filter BLE scan results or to locate the radar service
     * after connecting to a device.
     */
    val RADAR_SERVICE_UUID: UUID = UUID.fromString("6a4e3200-667b-11e3-949a-0800200c9a66")

    /**
     * The UUID of the radar data characteristic.
     *
     * This characteristic sends notifications containing threat data whenever
     * the radar detects changes in nearby vehicles. Enable notifications on
     * this characteristic to receive real-time threat updates.
     */
    val RADAR_CHARACTERISTIC_UUID: UUID = UUID.fromString("6a4e3203-667b-11e3-949a-0800200c9a66")

    /**
     * The UUID of the Client Characteristic Configuration Descriptor (CCCD).
     *
     * This is the standard Bluetooth CCCD UUID used to enable or disable
     * notifications on a characteristic. Write [BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE]
     * to this descriptor to start receiving radar data notifications.
     */
    val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
}
