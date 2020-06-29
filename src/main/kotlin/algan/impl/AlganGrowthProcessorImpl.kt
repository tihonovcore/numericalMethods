package algan.impl

import algan.AlganGrowthModel
import algan.AlganGrowthProcessor
import algan.SystemParameters
import equations.Equation
import equations.EquationSystem
import equations.SystemSolver
import utils.*
import utils.Agent.*
import kotlin.math.pow

class AlganGrowthProcessorImpl(
    val params: SystemParameters,
    val systemSolver: SystemSolver
) : AlganGrowthProcessor {

    private class EquationsUnknown(
        val P_AlCl3: Double,
        val P_GaCl: Double,
        val P_NH3: Double,
        val P_HCl: Double,
        val P_H2: Double,
        val x: Double
    ) {
        companion object {
            @JvmStatic
            fun fromList(values: List<Double>): EquationsUnknown {
                check(values.size == 6)
                return EquationsUnknown(values[0], values[1], values[2], values[3], values[4], values[5])
            }
        }
    }

    private fun pressure(agent: Agent): Double {
        val p = params.pressures[agent]
        check(p != null) { "Missing pressure parameter for agent: $agent" }

        return p
    }

    private fun density(agent: Agent): Double {
        val p = densities[agent]
        check(p != null) { "Missing density parameter for agent: $agent" }

        return p
    }

    override fun process(): AlganGrowthModel {
        val t = params.temperature
        val equations: List<Equation> = listOf(
            { v: EquationsUnknown -> with(v) { P_AlCl3 * P_NH3 - K9(t) * x * P_HCl.pow(3) } },
            { v: EquationsUnknown -> with(v) { P_GaCl * P_NH3 - K10(t) * (1 - x) * P_HCl * P_H2 } },
            { v: EquationsUnknown ->
                with(v) {
                    D(HCl, t) * (pressure(HCl) - P_HCl) + 2.0 * D(H2, t) * (pressure(H2) - P_H2) + 3.0 * D(NH3, t) * (pressure(NH3) - P_NH3)
                }
            },
            { v: EquationsUnknown ->
                with(v) {
                    D(HCl, t) * (pressure(HCl) - P_HCl) + D(GaCl, t) * (pressure(GaCl) - P_GaCl) + 3 * D(AlCl3, t) * (pressure(AlCl3) - P_AlCl3)
                }
            },
            { v: EquationsUnknown ->
                with(v) {
                    D(NH3, t) * (pressure(NH3) - P_NH3) - D(GaCl, t) * (pressure(GaCl) - P_GaCl) - D(AlCl3, t) * (pressure(AlCl3) - P_AlCl3)
                }
            },
            { v: EquationsUnknown ->
                with(v) {
                    D(AlCl3, t) * (pressure(AlCl3) - P_AlCl3) * (1 - x) - D(GaCl, t) * (pressure(GaCl) - P_GaCl) * x
                }
            }
        ).map { f -> { g: List<Double> -> f(EquationsUnknown.fromList(g)) } }

        val system = EquationSystem(equations)

        var solution: EquationsUnknown

        do {
            solution = EquationsUnknown.fromList(systemSolver.solve(system))
        } while (!isValid(solution))

        val interphaseFlowAlCl3 = D(AlCl3, t) * (pressure(AlCl3) - solution.P_AlCl3) / (R * t * params.delta)
        val interphaseFlowGaCl = D(GaCl, t) * (pressure(GaCl) - solution.P_GaCl) / (R * t * params.delta)

        val growthSpeed =
            (interphaseFlowAlCl3 * (AlN.mu / density(AlN)) + interphaseFlowGaCl * (GaN.mu / density(GaN))) * 1_000_000_000

        return AlganGrowthModel(
            HashMap<Agent, Double>().also {
                it[AlCl3] = interphaseFlowAlCl3
                it[GaCl] = interphaseFlowGaCl
            },
            HashMap<Agent, Double>().also {
                it[AlCl3] = solution.P_AlCl3
                it[GaCl] = solution.P_GaCl
                it[NH3] = solution.P_NH3
                it[HCl] = solution.P_HCl
                it[H2] = solution.P_H2
            },
            growthSpeed,
            solution.x
        )
    }


    private fun isValid(solution: EquationsUnknown): Boolean {
        return with(solution) {
            isValidPressure(P_GaCl)
                    && isValidPressure(P_HCl)
                    && isValidPressure(P_AlCl3)
                    && isValidPressure(P_H2)
                    && isValidPressure(P_NH3)
                    && x >= 0 && x <= 1
        }
    }

    private fun isValidPressure(p: Double) = p > 0 && p < P
}