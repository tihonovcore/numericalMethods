package equations

typealias Equation = (List<Double>) -> Double

interface EquationHelper {

    companion object {
        private const val DERIVATIVE_STEP = 1e-5

        fun derivative(equation: Equation, variable: Int, point: List<Double>): Double {
            val val1 = equation(point)
            val pointInRange = ArrayList(point)
            pointInRange[variable] += DERIVATIVE_STEP
            val val2 = equation(pointInRange)

            return (val2 - val1) / DERIVATIVE_STEP
        }

        fun gradient(equation: Equation, point: List<Double>): List<Double> {
            val grad = ArrayList<Double>()
            for (i in point.indices) {
                grad.add(derivative(equation, i, point))
            }
            return grad
        }
    }
}
