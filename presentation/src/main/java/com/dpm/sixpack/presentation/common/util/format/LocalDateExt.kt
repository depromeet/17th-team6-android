package com.dpm.sixpack.presentation.common.util.format

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun LocalDate.toYyyyMmDdString(): String {
    return this.format(DateTimeFormatter.ISO_LOCAL_DATE)
}

