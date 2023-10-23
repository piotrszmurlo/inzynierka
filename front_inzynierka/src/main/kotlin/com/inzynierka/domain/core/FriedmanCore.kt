package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.domain.service.IDataService
import com.inzynierka.model.RankingScores
import io.kvision.redux.Dispatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


sealed class FriedmanRankingAction : RankingsAction() {
    object FetchRankingsStarted : FriedmanRankingAction()
    data class FetchRankingsSuccess(val scores: RankingScores) : FriedmanRankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : FriedmanRankingAction()
}

fun friedmanReducer(state: ScoreRankingState, action: FriedmanRankingAction) = when (action) {
    is FriedmanRankingAction.FetchRankingsSuccess -> {
        val scores = action.scores.dimension.entries.associate { entry ->
            entry.key to entry.value
                .sortedBy { it.score }
                .mapIndexed { index, scoreEntry ->
                    Score(rank = index + 1, algorithmName = scoreEntry.algorithmName, score = scoreEntry.score)
                }
        }
        val combinedScores = scores.values.toMutableList()
            .reduce { acc, next -> acc + next }
            .groupingBy { score -> score.algorithmName }
            .reduce { _, acc, next ->
                acc.copy(score = (acc.score + next.score) / 2)
            }
            .values
            .sortedBy { score -> score.score }
            .mapIndexed { index, score ->
                score.copy(rank = index + 1)
            }

        state.copy(
            scores = scores,
            combinedScores = combinedScores,
            isFetching = false
        )
    }

    is FriedmanRankingAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is FriedmanRankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
}

fun loadFriedmanScores(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(FriedmanRankingAction.FetchRankingsStarted)
        when (val result = dataService.getFriedmanScores()) {
            is Result.Success -> dispatch(FriedmanRankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> dispatch(FriedmanRankingAction.FetchRankingsFailed(result.domainError))
        }
    }
}