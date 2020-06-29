package algan

import utils.Agent

data class SystemParameters(
    val pressures: Map<Agent, Double>,
    val temperature: Double,
    val delta: Double // diff. board layer depth)
)