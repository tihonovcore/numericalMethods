package algan

import utils.Agent

class AlganGrowthModel(val interphaseFlows: Map<Agent, Double>,
                       val thermodynamicPressures: Map<Agent, Double>,
                       val alganGrowthSpeed: Double,
                       val AlNproportion: Double
)