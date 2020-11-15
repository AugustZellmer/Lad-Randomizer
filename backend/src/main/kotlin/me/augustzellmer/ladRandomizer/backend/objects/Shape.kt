package me.augustzellmer.ladRandomizer.backend.objects

data class Shape(val polygon: Polygon, val color: Color);

enum class Polygon{
    SQUARE,
    TRIANGLE,
    CIRCLE,
    OCTAGON
}

enum class Color{
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    BLUE,
    PURPLE,
    PINK
}
