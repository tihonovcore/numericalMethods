import Agent.*
import kotlin.math.absoluteValue

fun main() {
    for (t in 350 .. 650) {
        val kelvin = t + 273.15

        val (x1, x2, x3) = findSolution(kelvin)
        val s1 = stream(AlCl,  kelvin, x1)
        val s2 = stream(AlCl2, kelvin, x2)
        val s3 = stream(AlCl3, kelvin, x3)

        val (s1abs, s2abs, s3abs) = listOf(s1, s2, s3).map { it.absoluteValue }
        val speed = (s1 + s2 + s3) * (26.9815 / 2690) * 1e9

        println("$kelvin $s1abs $s2abs $s3abs $speed")
    }
}
