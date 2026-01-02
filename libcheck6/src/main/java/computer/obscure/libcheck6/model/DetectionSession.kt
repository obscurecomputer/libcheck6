package computer.obscure.libcheck6.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Represents a completed session of radar detections.
 */
data class DetectionSession(
    val id: Long = System.currentTimeMillis(),
    val timestamp: Long = id,
    val threats: List<RadarThreat>
) {
    fun getFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
