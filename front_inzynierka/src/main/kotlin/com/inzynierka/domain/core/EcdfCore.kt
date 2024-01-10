package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.domain.models.RankingType
import com.inzynierka.model.EcdfData

sealed class EcdfAction : RankingsAction() {
    object FetchRankingsStarted : EcdfAction()
    data class EcdfTypeChanged(val type: RankingType) : EcdfAction()
    data class FetchRankingsSuccess(val data: List<EcdfData>) : EcdfAction()
    data class FetchRankingsFailed(val error: DomainError?) : EcdfAction()
}


data class EcdfState(
    val isFetching: Boolean = false,
    val rankingType: RankingType = RankingType.PerFunction,
    val splitData: Map<Dimension, Map<FunctionNumber, List<EcdfData>>>? = null,
    val combinedData: Map<Dimension, List<EcdfData>>? = null,
    val functionGroupData: Map<Dimension, Map<FunctionGroup, List<EcdfData>>>? = null
)

fun ecdfReducer(state: EcdfState, action: EcdfAction) = when (action) {
    is EcdfAction.FetchRankingsFailed -> state.copy(
        isFetching = false,
        splitData = null,
        combinedData = null,
        functionGroupData = null
    )

    is EcdfAction.FetchRankingsStarted -> state.copy(isFetching = true)
    is EcdfAction.FetchRankingsSuccess -> {
        val combinedData = action.data.groupBy { it.dimension }
        state.copy(
            isFetching = false,
            splitData = splitEcdfs(action.data),
            combinedData = combinedData.mapValues {
                it.value
                    .groupBy { ecdfData -> ecdfData.algorithmName }
                    .map { (_, oneAlgorithmData) ->
                        oneAlgorithmData.averageThresholdsAchieved()
                    }
            },
            functionGroupData = groupsData(combinedData)
        )
    }

    is EcdfAction.EcdfTypeChanged -> state.copy(rankingType = action.type)
}

private fun groupsData(data: Map<Dimension, List<EcdfData>>): Map<Dimension, Map<FunctionGroup, List<EcdfData>>>? {
    return data
        .mapValues { (_, dimensionData) ->
            dimensionData.also {
                val functionsSet = mutableSetOf<FunctionNumber>()
                it.forEach { functionsSet.add(it.functionNumber) }
                if (functionsSet != setOf(1..12)) return null
            }
                .groupBy { ecdfData -> getFunctionGroup(ecdfData.functionNumber) }
                .mapValues { (_, functionGroupData) ->
                    functionGroupData
                        .groupBy { ecdfData -> ecdfData.algorithmName }.values
                        .map { oneAlgorithmData ->
                            oneAlgorithmData.averageThresholdsAchieved()
                        }
                }
        }
}

fun List<EcdfData>.averageThresholdsAchieved(): EcdfData {
    return this.reduce { acc, next ->
        acc.copy(
            thresholdAchievedFractions = acc.thresholdAchievedFractions
                .mapIndexed { index, fraction ->
                    fraction + next.thresholdAchievedFractions[index]
                }
        )
    }
        .let { it.copy(thresholdAchievedFractions = it.thresholdAchievedFractions.map { fraction -> fraction / this.size }) }
}

fun splitEcdfs(data: List<EcdfData>): Map<Dimension, Map<FunctionNumber, List<EcdfData>>> {
    return data
        .groupBy { it.dimension }
        .mapValues {
            it.value.groupBy { data -> data.functionNumber }
        }
}

enum class FunctionGroup(val functionNumbers: List<Int>) {
    UNIMODAL(listOf(1)),
    BASIC(listOf(2, 3, 4, 5)),
    HYBRID(listOf(6, 7, 8)),
    COMPOSITION(listOf(9, 10, 11, 12))
}

fun getFunctionGroup(functionNumber: FunctionNumber): FunctionGroup {
    return when (functionNumber) {
        in FunctionGroup.UNIMODAL.functionNumbers -> FunctionGroup.UNIMODAL
        in FunctionGroup.BASIC.functionNumbers -> FunctionGroup.BASIC
        in FunctionGroup.HYBRID.functionNumbers -> FunctionGroup.HYBRID
        in FunctionGroup.COMPOSITION.functionNumbers -> FunctionGroup.COMPOSITION
        else -> throw IllegalArgumentException()
    }
}

