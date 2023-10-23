package com.inzynierka.domain

import com.inzynierka.domain.service.IDataService
import com.inzynierka.model.RemoteCEC2022Data
import io.kvision.redux.Dispatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class Cec2022RankingState(
    val cec2022Scores: Scores? = null,
    val cec2022ScoresCombined: List<Score>? = null,
    val isFetching: Boolean = false
)

sealed class Cec2022RankingAction : RankingsAction() {
    object FetchRankingsStarted : Cec2022RankingAction()
    data class FetchRankingsSuccess(val scores: RemoteCEC2022Data) : Cec2022RankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : Cec2022RankingAction()
}

fun cec2022Reducer(state: Cec2022RankingState, action: Cec2022RankingAction) = when (action) {
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
            cec2022Scores = scores,
            cec2022ScoresCombined = combinedScores,
            isFetching = false
        )
    }

    is Cec2022RankingAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is Cec2022RankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
}

fun loadCec2022Scores(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(Cec2022RankingAction.FetchRankingsStarted)
        when (val result = dataService.getCEC2022Scores()) {
            is Result.Success -> dispatch(Cec2022RankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> dispatch(Cec2022RankingAction.FetchRankingsFailed(result.domainError))
        }
    }
}