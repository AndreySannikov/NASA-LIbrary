package ru.degus.nasalibrary.models

data class Item(                                //POJO класс
    val `data`: List<Data>?,
    val href: String?,
    val links: List<Link>?
)