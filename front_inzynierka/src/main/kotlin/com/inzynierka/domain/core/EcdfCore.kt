package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.domain.models.RankingType
import com.inzynierka.domain.models.ScoreRankingEntry
import com.inzynierka.model.EcdfData
import com.inzynierka.ui.rankings.DIM_MAX_FES

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
    val splitAreas: Map<Dimension, Map<FunctionNumber, List<ScoreRankingEntry>>>? = null,
    val combinedData: Map<Dimension, List<EcdfData>>? = null,
    val functionGroupData: Map<Dimension, Map<FunctionGroup, List<EcdfData>>>? = null,
    val averageRankSplitAreas: List<ScoreRankingEntry>? = null
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
        val splitData = splitEcdfs(action.data)
        val areaData = areaData(action.data)
        val splitAreaData =
            areaData.splitData().mapValues { dimensionEntries ->
                dimensionEntries.value.mapValues { functionEntries ->
                    functionEntries.value.sortedBy { score -> -score.score }.let { scores ->
                        rankSortedList(scores, { score -> score.score }) { entry, rank -> entry.copy(rank = rank) }
                    }
                }
            }
        state.copy(
            isFetching = false,
            splitData = splitData,
            combinedData = combinedData.mapValues {
                it.value
                    .groupBy { ecdfData -> ecdfData.algorithmName }
                    .map { (_, oneAlgorithmData) ->
                        oneAlgorithmData.averageThresholdsAchieved()
                    }
            },
            functionGroupData = groupsData(combinedData),
            splitAreas = splitAreaData,
            averageRankSplitAreas = createAverageRanksRanking(splitAreaData)
        )
    }

    is EcdfAction.EcdfTypeChanged -> state.copy(rankingType = action.type)
}

fun createAverageRanksRanking(splitData: Map<Dimension, Map<FunctionNumber, List<ScoreRankingEntry>>>) =
    splitData.flatMap { it.value.flatMap { it.value } }
        .groupBy { it.algorithmName }.mapValues { perAlgorithmData ->
            perAlgorithmData.value.reduce { acc, next ->
                acc.copy(score = acc.score + next.score)
            }.let { it.copy(score = it.score / perAlgorithmData.value.size) }
        }.values.toList().let { rankSortedList(it, { score -> score.score }) { el, rank -> el.copy(rank = rank) } }

private fun areaData(dataList: List<EcdfData>) =
    dataList.map { data ->
        ScoreRankingEntry(
            rank = null,
            dimension = data.dimension,
            algorithmName = data.algorithmName,
            functionNumber = data.functionNumber,
            score = percentageArea(
                calculateAreaUnderCurve(
                    data.functionEvaluations,
                    data.thresholdAchievedFractions
                ), data.dimension
            )
        )
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

fun calculateAreaUnderCurve(x: List<Double>, y: List<Double>): Double {
    var sm = 0.0
    for (i in 1 until x.size) {
        val h = x[i] - x[i - 1]
        sm += h * (y[i - 1] + y[i]) / 2
    }
    return sm
}

private fun percentageArea(area: Double, dimension: Dimension) = area / DIM_MAX_FES[dimension]!! * 100 * dimension
