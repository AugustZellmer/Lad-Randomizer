package me.augustzellmer.ladRandomizer.backend.repo

import me.augustzellmer.ladRandomizer.backend.Color
import me.augustzellmer.ladRandomizer.backend.Polygon

data class Row(val roomId: String, val userId: String, val polygon: Polygon?, val color: Color?)
