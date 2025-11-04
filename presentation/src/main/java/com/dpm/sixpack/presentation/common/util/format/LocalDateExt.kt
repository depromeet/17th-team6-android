package com.dpm.sixpack.presentation.common.util.format

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun LocalDate.toYyyyMmDdString(): String = this.format(DateTimeFormatter.ISO_LOCAL_DATE)
