package com.trendyol.gpapi.infrastructure.util

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object Clock {
    private val DATE_FORMAT: String = "yyyy-MM-dd'T'HH:mmX"
    private val UTC_DATE_FORMAT: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private val EPOCH_TO_DATE_FORMAT: String = "yyyy-MM-dd"
    private var isFrozen: Boolean = false
    private var timeSet: ZonedDateTime? = null
    private val TURKEY_ZONE: ZoneId = ZoneId.of("Europe/Istanbul")
    private val UTC_ZONE: ZoneId = ZoneId.of("UTC")
    @Synchronized
    fun freeze() {
        isFrozen = true
    }

    @Synchronized
    fun freeze(date: ZonedDateTime?) {
        freeze()
        setTime(date)
    }

    @Synchronized
    fun unfreeze() {
        isFrozen = false
        timeSet = null
    }

    fun now(): LocalDateTime {
        return nowWithZone(TURKEY_ZONE)!!
            .toLocalDateTime()
    }

    fun nowUTC(): LocalDateTime {
        return nowWithZone(UTC_ZONE)!!
            .toLocalDateTime()
    }

    fun nowEpoch(): Long {
        return nowWithZone(TURKEY_ZONE)!!
            .toInstant().toEpochMilli()
    }

    fun nowEpochUTC(): Long {
        return nowWithZone(UTC_ZONE)!!
            .toInstant().toEpochMilli()
    }

    fun before(days: Int): Long {
        return nowWithZone(TURKEY_ZONE)!!
            .minusDays(days.toLong()).toInstant().toEpochMilli()
    }

    fun beforeUTC(days: Int): Long {
        return nowWithZone(UTC_ZONE)!!
            .minusDays(days.toLong()).toInstant().toEpochMilli()
    }

    fun epochToDate(epoch: Long?): String {
        return SimpleDateFormat(EPOCH_TO_DATE_FORMAT).format(Date((epoch)!!))
    }

    fun beforeMin(minutes: Int): Long {
        return nowWithZone(TURKEY_ZONE)!!
            .minusMinutes(minutes.toLong()).toInstant().toEpochMilli()
    }

    fun beforeMinUTC(minutes: Int): Long {
        return nowWithZone(UTC_ZONE)!!
            .minusMinutes(minutes.toLong()).toInstant().toEpochMilli()
    }

    fun beforeHourUTC(hours: Int): Long {
        return nowWithZone(UTC_ZONE)!!
            .minusHours(hours.toLong()).toInstant().toEpochMilli()
    }

    @Synchronized
    fun setTime(date: ZonedDateTime?) {
        timeSet = date
    }

    val timestamp: String
        get() = nowWithZone(TURKEY_ZONE)!!
            .format(DateTimeFormatter.ofPattern(DATE_FORMAT))
    val timestampUTC: String
        get() {
            return nowWithZone(UTC_ZONE)!!
                .format(DateTimeFormatter.ofPattern(UTC_DATE_FORMAT))
        }

    fun hoursBeforeTimestampUTC(hoursBefore: Int): String {
        return nowWithZone(UTC_ZONE)!!
            .minusHours(hoursBefore.toLong()).format(DateTimeFormatter.ofPattern(UTC_DATE_FORMAT))
    }

    private fun nowWithZone(zoneId: ZoneId): ZonedDateTime? {
        val zonedDateTime: ZonedDateTime = ZonedDateTime.now(zoneId)
        if (isFrozen) {
            if (timeSet == null) {
                timeSet = zonedDateTime
            }
            return timeSet
        }
        return zonedDateTime
    }
}
