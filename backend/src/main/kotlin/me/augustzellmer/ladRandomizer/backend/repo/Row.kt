package me.augustzellmer.ladRandomizer.backend.repo

import me.augustzellmer.ladRandomizer.backend.objects.Color
import me.augustzellmer.ladRandomizer.backend.objects.Polygon

data class Row(val roomId: String, val userId: String, val polygon: Polygon?, val color: Color?)
