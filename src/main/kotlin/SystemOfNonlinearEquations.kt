import Agent.*
import org.ejml.data.DMatrix5x5
import kotlin.math.pow

/*
Original equations:

x4 * x4 - K1(t) * x1.pow(2) * x5 = 0
x4 * x4 - K2(t) * x2 * x5 = 0
x4.pow(6) - K3(t) * x3.pow(2) * x5.pow(3) = 0
D(HCl, t) * (10_000 - x4) + 2 * D(H2, t) * (-x5) = 0
D(AlCl, t) * (-x1) + 2 * D(AlCl2, t) * (-x2) + 3 * D(AlCl3, t) * (-x3) + D(HCl, t) * (10_000 - x4) = 0
 */

fun jacobianMatrix(x: List<Double>, t: Double): DMatrix5x5 {
    val (x1, x2, x3, x4, x5) = x
    return DMatrix5x5(
        -2 * K1(t) * x1 * x5, 0.0, 0.0, 2 * x4, -K1(t) * x1 * x1,
        0.0, -K2(t) * x5, 0.0, 2 * x4, -K2(t) * x2,
        0.0, 0.0, -2 * K3(t) * x3 * x5.pow(3), 6 * x4.pow(5), -3 * K3(t) * x3.pow(2) * x5.pow(2),
        0.0, 0.0, 0.0, -D(HCl, t), -2 * D(H2, t),
        -D(AlCl, t), -2 * D(AlCl2, t), -3 * D(AlCl3, t), -D(HCl, t), 0.0
    )
}
