import Agent.*
import kotlin.math.absoluteValue

fun main() {
    for (t in 350 .. 650) {
        val kelvin = t + 273.15

        val (y1, y2, y3) = findSolution(kelvin)
        val s1 = stream(AlCl,  kelvin, y1)
        val s2 = stream(AlCl2, kelvin, y2)
        val s3 = stream(AlCl3, kelvin, y3)

        val (s1abs, s2abs, s3abs) = listOf(s1, s2, s3).map { it.absoluteValue }
        val speed = (s1 + s2 + s3) * (26.9815 / 2690) * 1e9

        println("$kelvin $s1abs $s2abs $s3abs $speed")
    }
}
