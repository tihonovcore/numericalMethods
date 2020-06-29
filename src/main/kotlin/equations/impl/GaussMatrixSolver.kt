package equations.impl

import equations.MatrixSolver
import kotlin.math.abs
import kotlin.math.max

class GaussMatrixSolver : MatrixSolver {

    override fun solve(matrix: Array<Array<Double>>, rhs: Array<Double>): Array<Double> {
        check(matrix.isNotEmpty()) { "Empty matrix for Gauss? Seriously?" }

        for (row in matrix) {
            check(row.size == matrix.size) { "Illegal matrix was passed to Gauss solver. Probably, it's not quadratic" }
        }

        val m = arrayOf(*matrix)
        val res = arrayOf(*rhs)

        val n = m.size

        val order = IntArray(n) { index -> index }

        for (k in 0 until n) {
            var maxRow = k
            var maxColumn = k
            for (i in k until n) {
                for (j in k until n) {
                    if (abs(m[i][j]) > abs(m[maxRow][maxColumn])) {
                        maxRow = i
                        maxColumn = j
                    }
                }
            }

            order[maxColumn] = order[k].also { order[k] = order[maxColumn] }

            m[k] = m[maxRow].also { m[maxRow] = m[k] }
            for (row in m) {
                row[maxColumn] = row[k].also { row[k] = row[maxColumn] }
            }
            res[k] = res[maxRow].also { res[maxRow] = res[k] }


            for (i in k + 1 until n) {
                val coef = m[i][k] / m[k][k]
                res[i] -= coef * res[k]
                for (j in k until n) {
                    m[i][j] -= coef * m[k][j]
                }
            }
        }

        val v = DoubleArray(n)
        for (i in n - 1 downTo 0) {
            var sum = 0.0
            for (j in i + 1 until n) {
                sum += m[i][j] * v[j]
            }
            v[i] = (res[i] - sum) / m[i][i]
        }
        val solution = DoubleArray(n)
        for (i in 0 until n) {
            solution[order[i]] = v[i]
        }
        return solution.toTypedArray()
    }
}