package me.timeto.shared.vm.history

import me.timeto.shared.ColorRgba

fun colorFromRgbaString(rgba: String): ColorRgba {
    return try {
        val parts = rgba.split(",").map { it.trim().toFloat() }
        if (parts.size == 4) {
            val r = (parts[0] * 255).toInt()
            val g = (parts[1] * 255).toInt()
            val b = (parts[2] * 255).toInt()
            val a = (parts[3] * 255).toInt()
            ColorRgba(r, g, b, a)
        } else {
            ColorRgba(0, 0, 0, 255) // Return black for invalid format
        }
    } catch (e: Exception) {
        ColorRgba(0, 0, 0, 255) // Return black on parsing error
    }
}

fun colorFromHex(hex: String): ColorRgba {
    val cleanHex = hex.removePrefix("#").removePrefix("0x")
    val colorInt = cleanHex.toLong(16).toInt()

    return if (cleanHex.length == 8) {
        // AARRGGBB
        val a = (colorInt shr 24) and 0xFF
        val r = (colorInt shr 16) and 0xFF
        val g = (colorInt shr 8) and 0xFF
        val b = colorInt and 0xFF
        ColorRgba(r, g, b, a)
    } else {
        // RRGGBB
        val r = (colorInt shr 16) and 0xFF
        val g = (colorInt shr 8) and 0xFF
        val b = colorInt and 0xFF
        ColorRgba(r, g, b)
    }
}
