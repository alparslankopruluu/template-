package com.screenmotion.app.model

enum class ThemeType(
    val title: String,
    val assetPath: String?,
    val emoji: String
) {
    SPACE("Uzay", "wallpapers/space.jpg", "🚀"),
    AQUARIUM("Akvaryum", "wallpapers/aquarium.jpg", "🐠"),
    CAR("Araba", "wallpapers/car_night.jpg", "🏎️"),
    TRUCK("Kamyon", "wallpapers/car_night.jpg", "🚛"),
    BUS("Otobüs", "wallpapers/car_night.jpg", "🚌"),
    TRACTOR("Traktör", "wallpapers/nature.jpg", "🚜"),
    TANK("Tank", "wallpapers/nature.jpg", "🛡️"),
    HELICOPTER("Helikopter", "wallpapers/nature.jpg", "🚁"),
    AIRPLANE("Uçak", "wallpapers/space.jpg", "✈️"),
    BICYCLE("Bisiklet", "wallpapers/nature.jpg", "🚲"),
    NATURE("Doğa", "wallpapers/nature.jpg", "🏔️")
}
