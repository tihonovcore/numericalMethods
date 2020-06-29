package equations

interface MatrixSolver {

    fun solve(matrix: Array<Array<Double>>, rhs: Array<Double>): Array<Double>
}