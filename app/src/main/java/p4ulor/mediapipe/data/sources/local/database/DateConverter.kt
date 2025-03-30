package p4ulor.mediapipe.data.sources.local.database

import androidx.room.TypeConverter
import p4ulor.mediapipe.data.utils.globalDateFormat
import java.util.Date

/**
 * Converts non-primitive types to primitive types that Room can store
 * Using this could be circumvented but YOLO
 */
class DateConverter {
    @TypeConverter
    fun stringToDate(date: String?) : Date? {
        return runCatching {
            date?.let {
                globalDateFormat.parse(date)
            }
        }.getOrNull()
    }

    @TypeConverter
    fun stringToDate(date: Date?) : String? {
        return date?.let {
            globalDateFormat.format(it)
        }
    }
}
