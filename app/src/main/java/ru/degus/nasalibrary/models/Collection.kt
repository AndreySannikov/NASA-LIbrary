package ru.degus.nasalibrary.models

data class Collection(                          //POJO класс
    val href: String?,
    val items: List<Item>?,
    val links: List<LinkX>?,
    val metadata: Metadata?,
    val version: String?
)