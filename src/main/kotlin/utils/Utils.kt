package utils

import utils.Agent.*
import kotlin.math.*

enum class Agent(
    val h: Double,
    val phi: List<Double>,
    val mu: Double,
    val sigma: Double,
    val epsilon: Double
) {
    AlCl(-51032.0, listOf(318.9948, 36.94626, -0.001226431, 1.1881743, 5.638541, -5.066135, 5.219347), 62.4345, 3.58, 932.0),
    AlCl2(-259000.0, listOf(427.2137, 56.56409, -0.002961273, 1.893842, 12.40072, -22.65441, 21.29898), 97.8875, 5.3, 825.0),
    AlCl3(-584100.0, listOf(511.8114, 81.15042, -0.004834879, 2.752097, 13.40078, -21.28001, 16.92868), 133.3405, 5.13, 472.0),
    Al(0.0, listOf(172.8289, 50.51806, -0.00411847, 1.476107, -458.1279, 2105.75, -4168.337), 26.9815, /*NONE*/ 0.0, /*NONE*/ 0.0),
    H2(0.0, listOf(205.5368, 29.50487, 0.000168424, 0.86065612, -14.95312, 78.18955, -82.78981), 2.016, 2.93, 34.1),
    HCl(-92310.0, listOf(243.9878, 23.15984, 0.001819985, 0.6147384, 51.16604, -36.89502, 9.174252), 36.461, 2.737, 167.1),
    N2(0.0, listOf(242.8156, 21.47467, 0.001748786, 0.5910039, 81.08497, -103.6265, 71.30775), 28.0135, 3.798, 71.4)
}

/**
 * Diffusion coefficient
 */
fun D(agent: Agent, t: Double): Double = with(agent) {
    val c = 2.628 * 0.01
    val p = 100_000
    val s = (sigma + N2.sigma) / 2
    val e = sqrt(epsilon * N2.epsilon)
    val omega = 1.074 * (t / e).pow(-0.1604)
    val m = 2 * mu * N2.mu / (mu + N2.mu)

    return c * t.pow(1.5) / (p * s * omega * m.pow(0.5))
}

fun gibbsEnergy(agent: Agent, t: Double): Double = with(agent) {
    val x = t / 10_000
    val x2 = x * x
    val x3 = x * x * x

    val Phi =  phi[0] + phi[1] * ln(x) + phi[2] / x2 + phi[3] / x + phi[4] * x + phi[5] * x2 + phi[6] * x3

    return agent.h - Phi * t
}

const val R = 8.314 //дж/(моль*К)
const val P = 100_000 //атмосферное давление
fun K1(t: Double): Double {
    val delta = 2 * gibbsEnergy(Al, t) + 2 * gibbsEnergy(HCl, t) - 2 * gibbsEnergy(AlCl, t) - gibbsEnergy(H2, t)
    return exp(-delta / (R * t)) / P
}

fun K2(t: Double): Double {
    val delta = gibbsEnergy(Al, t) + 2 * gibbsEnergy(HCl, t) - gibbsEnergy(AlCl2, t) - gibbsEnergy(H2, t)
    return exp(-delta / (R * t))
}

fun K3(t: Double): Double {
    val delta = 2 * gibbsEnergy(Al, t) + 6 * gibbsEnergy(HCl, t) - 2 * gibbsEnergy(AlCl3, t) - 3 * gibbsEnergy(H2, t)
    return exp(-delta / (R * t)) * P
}

fun stream(agent: Agent, t: Double, p: Double): Double {
    val delta = 0.01
    val r = 8314

    return D(agent, t) * (-p) / (r * t * delta)
}
