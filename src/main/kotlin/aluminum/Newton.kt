package aluminum

import utils.Agent.*
import utils.D
import utils.K1
import utils.K2
import utils.K3
import org.ejml.data.DMatrix5x5
import org.ejml.dense.fixed.CommonOps_DDF5
import kotlin.math.pow

fun findSolution(t: Double): List<Double> {
    //X
    var x1 = 1.0
    var x2 = 2.0
    var x3 = 3.0
    var x4 = 4.0
    var x5 = 5.0

    repeat(100) {
        //Y^{-1}(X_k)
        val m = jacobianMatrix(listOf(x1, x2, x3, x4, x5), t)
        val m_inv = DMatrix5x5()
        CommonOps_DDF5.invert(m, m_inv)

        //F(X_k)
        val f1x = x4 * x4 - K1(t) * x1.pow(2) * x5
        val f2x = x4 * x4 - K2(t) * x2 * x5
        val f3x = x4.pow(6) - K3(t) * x3.pow(2) * x5.pow(3)
        val f4x = D(HCl, t) * (10_000 - x4) + 2 * D(H2, t) * (-x5)
        val f5x = D(AlCl, t) * (-x1) + 2 * D(AlCl2, t) * (-x2) + 3 * D(AlCl3, t) * (-x3) + D(HCl, t) * (10_000 - x4)

        //X_{k + 1} = X_k - Y^{-1}(X_k) * F(X_k)
        val newX1 = with(m_inv) { x1 - (f1x * a11 + f2x * a12 + f3x * a13 + f4x * a14 + f5x * a15) }
        val newX2 = with(m_inv) { x2 - (f1x * a21 + f2x * a22 + f3x * a23 + f4x * a24 + f5x * a25) }
        val newX3 = with(m_inv) { x3 - (f1x * a31 + f2x * a32 + f3x * a33 + f4x * a34 + f5x * a35) }
        val newX4 = with(m_inv) { x4 - (f1x * a41 + f2x * a42 + f3x * a43 + f4x * a44 + f5x * a45) }
        val newX5 = with(m_inv) { x5 - (f1x * a51 + f2x * a52 + f3x * a53 + f4x * a54 + f5x * a55) }

        x1 = newX1
        x2 = newX2
        x3 = newX3
        x4 = newX4
        x5 = newX5
    }

    return listOf(x1, x2, x3, x4, x5)
}
