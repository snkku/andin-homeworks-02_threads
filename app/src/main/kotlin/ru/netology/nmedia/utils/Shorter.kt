package ru.netology.nmedia.utils

class Shorter {
    fun short(value: Double): String
    {
        //  дебильное округление не дает выводить по %.1f :\
        return when (value) {
            in 0.0..999.0 -> value.toInt().toString()
            in 1000.0 .. 9999.0 -> String.format("%.2f", (value/1000.0)).dropLast(1).trimEnd('0','.').plus('K')
            in 10000.0 .. 999999.0 -> String.format("%d", (value/1000.0).toInt()).plus('K')
            else -> String.format("%.2f", (value/1000000.0)).dropLast(1).trimEnd('0','.').plus('M')
        }
    }
}