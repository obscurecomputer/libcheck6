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

/**
 * Represents the current state of the BLE connection to a cycling radar device.
 *
 * Use this enum to track and display connection status in your application's UI,
 * or to make decisions about when certain operations are safe to perform.
 *
 * ## State Transitions
 *
 * Typical state flow:
 * ```
 * DISCONNECTED -> CONNECTING -> CONNECTED
 *                     |
 *                     v
 *              DISCONNECTED (on failure)
 * ```
 *
 * ## Example
 *
 * ```kotlin
 * var connectionStatus = ConnectionStatus.DISCONNECTED
 *
 * when (connectionStatus) {
 *     ConnectionStatus.DISCONNECTED -> showConnectButton()
 *     ConnectionStatus.CONNECTING -> showLoadingSpinner()
 *     ConnectionStatus.CONNECTED -> showRadarData()
 * }
 * ```
 */
enum class ConnectionStatus {
    /**
     * No active connection to a radar device.
     * The application should allow the user to initiate a new connection.
     */
    DISCONNECTED,

    /**
     * A connection attempt is currently in progress.
     * The application should show a loading state and disable connection controls.
     */
    CONNECTING,

    /**
     * Successfully connected to a radar device and receiving data.
     * The application can display radar threat information.
     */
    CONNECTED
}
