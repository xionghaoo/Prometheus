package xh.destiny.core.utils

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
    companion object {
        private val dateParser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        private val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
        private val minuteFormatter = SimpleDateFormat("mm:ss", Locale.CHINA)
        private val hourFormatter = SimpleDateFormat("HH:mm", Locale.CHINA)

        // 格式yyyy-MM-dd
        fun yesterday() : String {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -1)
            return dateFormatter.format(cal.time)
        }

        // 格式yyyy-MM-dd
        fun today() : String = dateFormatter.format(Date())/*"2019-06-19"*/

        fun currentMonth() : DateRange {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, - cal.get(Calendar.DATE) + 1)
            val start = dateFormatter.format(cal.time)
            val end = dateFormatter.format(Calendar.getInstance().time)
            return DateRange(start, end)
        }

        /**
         * 最近30天
         */
        fun lastThirtyDays() : DateRange {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, - 30)
            val start = dateFormatter.format(cal.time)
            val end = dateFormatter.format(Calendar.getInstance().time)
            return DateRange(start, end)
        }

        // 格式yyyy-MM-dd HH:mm:ss
        fun now() : String = dateParser.format(Date())

        fun nowTime() : String = timeFormatter.format(Date())

        fun toDate(d: String?) : String? {
            return try {
                dateFormatter.format(dateParser.parse(d))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun toTime(d: String?) : String? {
            if (d == null) return null
            return try {
                timeFormatter.format(dateParser.parse(d))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun toTime(date: Date) : String? {
            return try {
                timeFormatter.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun toMinute(d: String?) : String? {
            if (d == null) return null

            return try {
                minuteFormatter.format(dateParser.parse(d))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun toHour(d: String?) : String? {
            if (d == null) return null

            return try {
                hourFormatter.format(dateParser.parse(d))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun parseDate(date: String?) : Date? {
            if (date == null) return null

            return try {
                dateFormatter.parse(date)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun parseTime(d: String?) : Date? {
            if (d == null) return null

            return try {
                dateParser.parse(d)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun formatToDate(d: Date?) : String? {
            return try {
                dateFormatter.format(d)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }

    class DateRange(var start: String, var end: String)
}