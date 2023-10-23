package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.domain.service.IDataService
import com.inzynierka.model.RankingScores
import io.kvision.redux.Dispatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


sealed class Cec2022RankingAction : RankingsAction() {
    object FetchRankingsStarted : Cec2022RankingAction()
    data class FetchRankingsSuccess(val scores: RankingScores) : Cec2022RankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : Cec2022RankingAction()
}

fun cec2022Reducer(state: ScoreRankingState, action: Cec2022RankingAction) = when (action) {
    is Cec2022RankingAction.FetchRankingsSuccess -> {
        val scores = action.scores.dimension.entries.associate { entry ->
            entry.key to entry.value
                .sortedByDescending { it.score }
                .mapIndexed { index, scoreEntry ->
                    Score(rank = index + 1, algorithmName = scoreEntry.algorithmName, score = scoreEntry.score)
                }
        }
        val combinedScores = scores.values.toMutableList()
            .reduce { acc, next -> acc + next }
            .groupingBy { score -> score.algorithmName }
            .reduce { _, acc, next ->
                acc.copy(score = acc.score + next.score)
            }
            .values
            .sortedByDescending { score -> score.score }
            .mapIndexed { index, score ->
                score.copy(rank = index + 1)
            }

        state.copy(
            scores = scores,
            combinedScores = combinedScores,
            isFetching = false
        )
    }

    is Cec2022RankingAction.FetchRankingsFailed -> {
        console.log("${(action.error as DomainError.NetworkError).message}")
        state.copy(isFetching = false)
    }

    is Cec2022RankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
}

fun loadCec2022Scores(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(Cec2022RankingAction.FetchRankingsStarted)
        when (val result = dataService.getCec2022Scores()) {
            is Result.Success -> dispatch(Cec2022RankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> dispatch(Cec2022RankingAction.FetchRankingsFailed(result.domainError))
        }
    }
}

//fun RankingScores.sortScores(): Map<Int, List<Score>> {
//    return this.scores.entries.associate { entry ->
//        entry.key to entry.value
//            .sortedByDescending { it.score }
//            .mapIndexed { index, scoreEntry ->
//                Score(rank = index + 1, algorithmName = scoreEntry.algorithmName, score = scoreEntry.score)
//            }
//    }
//}