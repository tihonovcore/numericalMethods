package equations.impl

import equations.EquationHelper.Companion.gradient
import equations.EquationSystem
import equations.MatrixSolver
import equations.SystemSolver
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class NewtonSystemSolver(val matrixSolver: MatrixSolver) : SystemSolver {

    private val rnd = Random()

    companion object {
        private val ROUNDS_TIMEOUT = 100000
        private val PRECISION = 1e-9
    }

    override fun solve(system: EquationSystem): List<Double> {
        check(system.equations.isNotEmpty()) { "Cannot solve empty equations system..." }
        val n = system.equations.size
        var precisionReached = false

        var x: List<Double> = ArrayList()
        while (!precisionReached) {
            x = initApproximation(n)

            var majorDelta = 0.0
            for (round in 1..ROUNDS_TIMEOUT) {
                val delta = findDelta(system, x)
                x = x.mapIndexed { index, d -> d + delta[index] }

                majorDelta = delta.map(Math::abs).max()!!
                if (majorDelta < PRECISION) {
                    precisionReached = true
                    break
                }
            }

            if (!precisionReached) {
                println("Newton processor made $ROUNDS_TIMEOUT iterations, but failed to reach desired precision")
                println("Will pick another initial approximation and try again")
                println("Major delta: $majorDelta")
            }
        }

        return x
    }

    private fun findDelta(system: EquationSystem, point: List<Double>): List<Double> {
        val n = system.equations.size

        val jacobi = Array(n) { index -> gradient(system.equations[index], point).toTypedArray() }
        val rhs = Array(n) { index -> -system.equations[index](point) }

        return matrixSolver.solve(jacobi, rhs).toList()
    }


    private fun initApproximation(size: Int): List<Double> = ArrayList<Double>().also {
        for (i in 1..size) {
            it.add(rnd.nextDouble())
        }
    }

}