package algan

import algan.impl.AlganGrowthProcessorImpl
import equations.SystemSolver
import equations.impl.GaussMatrixSolver
import equations.impl.NewtonSystemSolver
import loaders.DiskLoader
import loaders.impl.CSVDiskLoaderImpl
import utils.Agent
import utils.Agent.*
import java.nio.file.Paths

class AlganProcessorMain(val systemSolver: SystemSolver, val loader: DiskLoader) {

    private val OUTPUT_DIR = Paths.get("task3-output")

    private fun createSystemParameters(P_AlCl3: Double, P_GaCl: Double, P_H2: Double, P_N2: Double) = SystemParameters(
        pressures = HashMap<Agent, Double>().also {
            it[AlCl3] = P_AlCl3
            it[GaCl] = P_GaCl
            it[H2] = P_H2
            it[N2] = P_N2
            it[HCl] = 0.0
            it[NH3] = 1500.0
        },
        temperature = 1100.0 + 273,
        delta = 0.01
    )

    private fun processWithDefinedGasesPressure(P_H2: Double, P_N2: Double): Map<Double, AlganGrowthModel> {
        val res = LinkedHashMap<Double, AlganGrowthModel>()
        for (P_AlCl3 in 0..30) {
            val P_GaCl = 30 - P_AlCl3
            val params = createSystemParameters(P_AlCl3.toDouble(), P_GaCl.toDouble(), P_H2, P_N2)

            val xg = P_AlCl3.toDouble() / (P_AlCl3 + P_GaCl)
            val processor = AlganGrowthProcessorImpl(params, systemSolver)
            res[xg] = processor.process()
        }

        return res
    }

    private fun saveData(dataId: String, processResult: Map<Double, AlganGrowthModel>) {
        val dir = OUTPUT_DIR.resolve(dataId)

        loader.saveOnDisk(
            dir,
            "V(AlGaN)",
            LinkedHashMap<String, List<String>>().also {
                it["P(AlCl3)/(P(AlCl3)+P(GaCl))"] = ArrayList(processResult.keys).map(Double::toString)
                it["V(AlGaN)"] = processResult.values
                    .map(AlganGrowthModel::alganGrowthSpeed)
                    .map(Double::toString)
            }
        )

        loader.saveOnDisk(
            dir,
            "interphaseFlows",
            LinkedHashMap<String, List<String>>().also {
                it["P(AlCl3)/(P(AlCl3)+P(GaCl))"] = ArrayList(processResult.keys).map(Double::toString)
                it["G(AlCl3)"] = processResult.values
                    .map { (it.interphaseFlows[AlCl3] ?: error("Evaluation didn't compute AlCl3 interphase flow")).toString() }
                it["G(GaCl)"] = processResult.values
                    .map { (it.interphaseFlows[GaCl] ?: error("Evaluation didn't compute GaCl interphase flow")).toString() }
            }
        )

        loader.saveOnDisk(
            dir,
            "AiNFraction",
            LinkedHashMap<String, List<String>>().also {
                it["P(AlCl3)/(P(AlCl3)+P(GaCl))"] = ArrayList(processResult.keys).map(Double::toString)
                it["fraction"] = processResult.values
                    .map(AlganGrowthModel::AlNproportion)
                    .map(Double::toString)
            }
        )
    }

    fun run() {
        println("Processing computation for pure N2 as a carrier gas")
        val pureN2 = processWithDefinedGasesPressure(0.0, 98470.0)
        saveData("pureN2", pureN2)

        println("Processing computation for mix of N2 and H2 as a carrier gas")
        val mixedGases = processWithDefinedGasesPressure(9847.0, 88623.0)
        saveData("mixedGases", mixedGases)
    }
}

fun main() = AlganProcessorMain(NewtonSystemSolver(GaussMatrixSolver()), CSVDiskLoaderImpl()).run()

