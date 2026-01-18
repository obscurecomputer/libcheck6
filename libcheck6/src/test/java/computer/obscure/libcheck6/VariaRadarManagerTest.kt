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

package computer.obscure.libcheck6

import android.content.Context
import computer.obscure.libcheck6.manager.VariaRadarManager
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VariaRadarManagerTest {

    private val context = mockk<Context>(relaxed = true)
    private val manager = VariaRadarManager(context)

    @Test
    fun `parseRadarData returns empty list for empty data`() {
        val data = byteArrayOf()
        val result = manager.parseRadarData(data)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseRadarData returns empty list for insufficient data`() {
        val data = byteArrayOf(0x01, 0x02)
        val result = manager.parseRadarData(data)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseRadarData correctly parses single threat`() {
        // for (i in 1 until data.size step 3)
        // threatId = data[i]
        // distance = data[i+1]
        // speed = data[i+2]
        
        val data = byteArrayOf(
            0x00, // Header/Length? (Ignored by the loop starting at 1)
            0x01, // threatId
            0x0A, // distance (10)
            0x05  // speed (5)
        )
        println(data.joinToString { it.toString(16) })
        val result = manager.parseRadarData(data)
        assertEquals(1, result.size)
        assertEquals(1, result[0].threatId)
        assertEquals(10, result[0].distanceMeters)
        assertEquals(5, result[0].speedKph)
    }

    @Test
    fun `parseRadarData correctly parses multiple threats`() {
        val data = byteArrayOf(
            0x00,
            0x01, 0x0A, 0x05, // Threat 1
            0x02, 0x14, 0x0F  // Threat 2
        )
        val result = manager.parseRadarData(data)
        assertEquals(2, result.size)
        
        assertEquals(1, result[0].threatId)
        assertEquals(10, result[0].distanceMeters)
        assertEquals(5, result[0].speedKph)
        
        assertEquals(2, result[1].threatId)
        assertEquals(20, result[1].distanceMeters)
        assertEquals(15, result[1].speedKph)
    }

    @Test
    fun `parseRadarData handles partial threat data at end`() {
        val data = byteArrayOf(
            0x00,
            0x01, 0x0A, 0x05,
            0x02, 0x14 // Incomplete threat 2
        )
        val result = manager.parseRadarData(data)
        assertEquals(1, result.size) // Only first threat should be parsed
    }
}
