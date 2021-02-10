package ru.degus.nasalibrary.util

import java.time.format.DateTimeFormatter

fun String.videoLink(): String {                                      //метод обрезки строки для получения ссылки для игнорирования базового URL
    return this.substringAfter("video/")
}

fun String.audioLink(): String {                                      //метод обрезки строки для получения ссылки для игнорирования базового URL
    return this.substringAfter("audio/")
}

fun String.replaceSpace(): String {                                      //метод обрезки строки для получения года релиза "2010-01-01T08:00:00Z"
    return this.replace(" ", "%20")
}

fun String.replaceHTTPS(): String {                                      //метод замены http запроса на https (для Android 9.0 и выше)
    return this.replace("http://images-assets.nasa.gov", "https://images-assets.nasa.gov")
}

fun String.normalDate(): String = DateTimeFormatter.ofPattern("MM-dd-yyyy")             //метод для измения формата даты
    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd").parse(this.substringBefore("T")))
