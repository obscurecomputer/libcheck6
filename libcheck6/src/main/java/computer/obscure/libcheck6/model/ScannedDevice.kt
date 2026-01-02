package computer.obscure.libcheck6.model

import android.bluetooth.BluetoothDevice

/**
 * Represents a device shown in the scan list.
 */
data class ScannedDevice(
    val device: BluetoothDevice,
    val isCurrentlyFound: Boolean,
    val lastSeenTimestamp: Long = 0L
)
