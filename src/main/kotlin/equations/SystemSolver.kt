package equations

interface SystemSolver {

    fun solve(system: EquationSystem): List<Double>
}