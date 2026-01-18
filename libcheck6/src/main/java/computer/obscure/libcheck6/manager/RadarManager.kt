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

package computer.obscure.libcheck6.manager

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import computer.obscure.libcheck6.model.RadarThreat
import computer.obscure.libcheck6.util.RadarConstants
import java.util.UUID

/**
 * Manages BLE connections and data parsing for cycling radar devices.
 *
 * This class serves as the main entry point for interacting with BLE cycling radars. It handles
 * establishing BLE connections, discovering GATT services, enabling characteristic notifications,
 * and parsing incoming radar threat data into usable [RadarThreat] objects.
 *
 * ## Usage
 *
 * ```kotlin
 * val manager = RadarManager(context)
 *
 * // Set up callbacks
 * manager.onRadarDataReceived = { threats ->
 *     threats.forEach { threat ->
 *         Log.d("Radar", "Threat at ${threat.distanceMeters}m, ${threat.speedKph} km/h")
 *     }
 * }
 *
 * manager.onConnectionStateChange = { isConnected ->
 *     Log.d("Radar", "Connection state: $isConnected")
 * }
 *
 * manager.onConnectionFailed = { device ->
 *     Log.e("Radar", "Failed to connect to ${device.address}")
 * }
 *
 * // Connect to a discovered device
 * manager.connect(bluetoothDevice)
 *
 * // Later, disconnect
 * manager.disconnect()
 * ```
 *
 * ## Required Permissions
 *
 * This class requires the `BLUETOOTH_CONNECT` permission to be granted before calling
 * [connect] or [disconnect].
 *
 * @param context The Android [Context] used for BLE operations.
 * @constructor Creates a new [RadarManager] instance bound to the given context.
 * @see RadarThreat
 * @see RadarConstants
 */
class RadarManager(private val context: Context) {

    private val TAG = "RadarManager"
    private var bluetoothGatt: BluetoothGatt? = null

    /**
     * Callback invoked when new radar threat data is received from the connected device.
     *
     * The callback receives a list of [RadarThreat] objects representing all currently
     * detected threats. An empty list indicates no threats are currently detected.
     *
     * This callback is invoked on a background thread. If you need to update UI,
     * ensure you switch to the main thread.
     */
    var onRadarDataReceived: ((List<RadarThreat>) -> Unit)? = null

    /**
     * Callback invoked when the BLE connection state changes.
     *
     * @param isConnected `true` when connected to the radar device, `false` when disconnected.
     */
    var onConnectionStateChange: ((Boolean) -> Unit)? = null

    /**
     * Callback invoked when a connection attempt fails.
     *
     * This is called when the GATT connection fails with a non-success status,
     * allowing the application to handle reconnection logic or notify the user.
     *
     * @param device The [BluetoothDevice] that failed to connect.
     */
    var onConnectionFailed: ((BluetoothDevice) -> Unit)? = null

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            val device = gatt?.device ?: return

            // Check the status first
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "GATT connection error. Status: $status")
                onConnectionFailed?.invoke(device)
                gatt.close()
                bluetoothGatt = null
                return // Exit early on failure
            }

            // Handle success cases
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.")
                bluetoothGatt = gatt
                bluetoothGatt?.discoverServices()
                onConnectionStateChange?.invoke(true)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.")
                gatt.close()
                bluetoothGatt = null
                onConnectionStateChange?.invoke(false)
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Services discovered.")
                enableRadarNotifications(gatt)
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            handleCharacteristicChanged(characteristic.uuid, value)
        }
    }

    private fun handleCharacteristicChanged(uuid: UUID, data: ByteArray) {
        if (RadarConstants.RADAR_CHARACTERISTIC_UUID == uuid) {
            val threats = parseRadarData(data)
            onRadarDataReceived?.invoke(threats)
        }
    }

    /**
     * Initiates a BLE connection to the specified radar device.
     *
     * This method starts the asynchronous connection process. Connection success or failure
     * will be reported through the [onConnectionStateChange] and [onConnectionFailed] callbacks.
     *
     * If a connection is already established or in progress, this method does nothing.
     *
     * @param device The [BluetoothDevice] to connect to, typically discovered via BLE scanning.
     * @throws SecurityException if [Manifest.permission.BLUETOOTH_CONNECT] is not granted.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(device: BluetoothDevice) {
        if (bluetoothGatt == null) {
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
            Log.d(TAG, "Attempting to connect to ${device.address}")
        }
    }

    /**
     * Disconnects from the currently connected radar device.
     *
     * This initiates a graceful disconnection. The [onConnectionStateChange] callback
     * will be invoked with `false` when the disconnection completes.
     *
     * If no device is currently connected, this method does nothing.
     *
     * @throws SecurityException if [Manifest.permission.BLUETOOTH_CONNECT] is not granted.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun enableRadarNotifications(gatt: BluetoothGatt?) {
        val service = gatt?.getService(RadarConstants.RADAR_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(RadarConstants.RADAR_CHARACTERISTIC_UUID)

        if (characteristic == null) {
            Log.e(TAG, "Radar characteristic not found!")
            return
        }

        gatt.setCharacteristicNotification(characteristic, true)
        val descriptor = characteristic.getDescriptor(RadarConstants.CCCD_UUID)
        if (descriptor != null) {
            gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            Log.i(TAG, "Enabled notifications for radar characteristic.")
        } else {
            Log.e(TAG, "CCCD not found for radar characteristic!")
        }
    }

    /**
     * Parses raw radar data bytes into a list of [RadarThreat] objects.
     *
     * The radar data format consists of:
     * - Byte 0: Header/length byte (skipped during parsing)
     * - Bytes 1-3: First threat (threatId, distance, speed)
     * - Bytes 4-6: Second threat (if present)
     * - And so on in 3-byte increments
     *
     * Each threat occupies 3 bytes:
     * - Byte n: Threat ID (unique identifier for tracking the same vehicle)
     * - Byte n+1: Distance in meters
     * - Byte n+2: Speed in km/h
     *
     * @param data The raw byte array received from the BLE characteristic notification.
     * @return A list of parsed [RadarThreat] objects. Returns an empty list if the data
     *         is malformed, too short, or if a parsing error occurs.
     */
    internal fun parseRadarData(data: ByteArray): List<RadarThreat> {
        if (data.size < 4 && data.isNotEmpty()) {
            return emptyList()
        }

        val threats = mutableListOf<RadarThreat>()
        try {
            for (i in 1 until data.size step 3) {
                if (i + 2 >= data.size) break

                val threatId = data[i].toUByte().toInt()
                val distance = data[i + 1].toUByte().toInt()
                val speed = data[i + 2].toUByte().toInt()

                threats.add(RadarThreat(threatId, speed, distance))
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.e(TAG, "Error parsing radar data: packet format unexpected.", e)
            return emptyList()
        }
        return threats
    }
}
