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
import computer.obscure.libcheck6.util.VariaRadarConstants
import java.util.UUID

/**
 * Manages the BLE connection and data parsing for a Garmin Varia radar device.
 */
class VariaRadarManager(private val context: Context) {

    private val TAG = "VariaRadarManager"
    private var bluetoothGatt: BluetoothGatt? = null

    var onRadarDataReceived: ((List<RadarThreat>) -> Unit)? = null
    var onConnectionStateChange: ((Boolean) -> Unit)? = null
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
        if (VariaRadarConstants.RADAR_CHARACTERISTIC_UUID == uuid) {
            val threats = parseRadarData(data)
            onRadarDataReceived?.invoke(threats)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(device: BluetoothDevice) {
        if (bluetoothGatt == null) {
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
            Log.d(TAG, "Attempting to connect to ${device.address}")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun enableRadarNotifications(gatt: BluetoothGatt?) {
        val service = gatt?.getService(VariaRadarConstants.RADAR_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(VariaRadarConstants.RADAR_CHARACTERISTIC_UUID)

        if (characteristic == null) {
            Log.e(TAG, "Radar characteristic not found!")
            return
        }

        gatt.setCharacteristicNotification(characteristic, true)
        val descriptor = characteristic.getDescriptor(VariaRadarConstants.CCCD_UUID)
        if (descriptor != null) {
            gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            Log.i(TAG, "Enabled notifications for radar characteristic.")
        } else {
            Log.e(TAG, "CCCD not found for radar characteristic!")
        }
    }

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
