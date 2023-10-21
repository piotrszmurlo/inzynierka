package com.inzynierka.domain

import com.inzynierka.domain.service.IDataService
import com.inzynierka.model.RemoteCEC2022Data
import io.kvision.redux.Dispatch
import io.kvision.redux.RAction
import io.kvision.types.KFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

sealed class Tab {
    object Upload : Tab()
    sealed class ResultsTab : Tab() {
        object PairTest : ResultsTab()
        object CEC2022 : ResultsTab()
        object Friedman : ResultsTab()
        object Median : ResultsTab()
        object Mean : ResultsTab()
        object ECDF : ResultsTab()
    }
}

data class MainAppState(
    val data: List<Int>,
    val tab: Tab,
    val isFetching: Boolean,
    val success: Boolean,
    val error: DomainError?,
    val uploadButtonDisabled: Boolean = true,
    val rankingsData: RankingsData = RankingsData(),
    val availableAlgorithms: List<String> = listOf(),
    val availableDimensions: List<Int> = listOf()
) : KoinComponent

typealias Scores = Map<Int, List<Score>>

data class RankingsData(
    val cec2022Scores: Scores? = null,
    val cec2022ScoresCombined: List<Score>? = null
)

data class Score(
    val rank: Int,
    val algorithmName: String,
    val score: Double
)

sealed class MainAppAction : RAction {
    object FetchDataStarted : MainAppAction()
    object FetchCEC2022ScoresStarted : MainAppAction()
    data class FetchCEC2022ScoresSuccess(val scores: RemoteCEC2022Data) : MainAppAction()
    data class FetchCEC2022ScoresFailed(val error: DomainError?) : MainAppAction()

    object PerformPairTest : MainAppAction()
    data class PairTestSuccess(val scores: Int) : MainAppAction()
    data class PairTestFailed(val error: DomainError?) : MainAppAction()
    object FetchAlgorithmNamesStarted : MainAppAction()
    data class FetchAlgorithmNamesSuccess(val names: List<String>) : MainAppAction()
    data class FetchAlgorithmNamesFailed(val error: DomainError?) : MainAppAction()
    object UploadFileStarted : MainAppAction()
    object UploadFileSuccess : MainAppAction()
    object ErrorHandled : MainAppAction()
    data class UploadFormOnChangeHandler(val kFile: KFile?) : MainAppAction()
    data class UploadFileFailed(val error: DomainError?) : MainAppAction()
    data class FetchDataSuccess(val data: List<Int>) : MainAppAction()
    data class FetchDataFailed(val error: DomainError) : MainAppAction()
    data class TabSelected(val tab: Tab) : MainAppAction()
}

fun mainAppReducer(state: MainAppState, action: MainAppAction): MainAppState = when (action) {

    is MainAppAction.FetchDataStarted -> {
        state.copy(isFetching = true)
    }

    is MainAppAction.FetchDataFailed -> {
        state.copy(error = action.error)
    }

    is MainAppAction.FetchDataSuccess -> {
        state.copy(
            data = action.data,
            success = true
        )
    }

    is MainAppAction.UploadFileStarted -> {
        state
    }

    is MainAppAction.UploadFileFailed -> {
        state
    }

    is MainAppAction.UploadFileSuccess -> {
        state
    }

    is MainAppAction.ErrorHandled -> {
        state.copy(error = null)
    }

    is MainAppAction.UploadFormOnChangeHandler -> {
        state.copy(uploadButtonDisabled = action.kFile == null)
    }

    is MainAppAction.TabSelected -> {
        state.copy(
            tab = action.tab,
            rankingsData = state.rankingsData.copy()
        )
    }

    is MainAppAction.FetchCEC2022ScoresFailed -> state

    is MainAppAction.FetchCEC2022ScoresStarted -> state

    is MainAppAction.FetchCEC2022ScoresSuccess -> {
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
            rankingsData = state.rankingsData.copy(
                cec2022Scores = scores,
                cec2022ScoresCombined = combinedScores
            )
        )
    }

    MainAppAction.FetchAlgorithmNamesStarted -> state
    is MainAppAction.FetchAlgorithmNamesFailed -> state
    is MainAppAction.FetchAlgorithmNamesSuccess -> {
        state.copy(availableAlgorithms = action.names)
    }

    is MainAppAction.PairTestFailed -> state
    is MainAppAction.PairTestSuccess -> state
    is MainAppAction.PerformPairTest -> state
}

fun loadCec2022Scores(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(MainAppAction.FetchCEC2022ScoresStarted)
        when (val result = dataService.getCEC2022Scores()) {
            is Result.Success -> dispatch(MainAppAction.FetchCEC2022ScoresSuccess(result.data))
            is Result.Error -> dispatch(MainAppAction.FetchCEC2022ScoresFailed(result.domainError))
        }
    }
}

fun loadAvailableAlgorithms(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(MainAppAction.FetchAlgorithmNamesStarted)
        when (val result = dataService.getAvailableAlgorithms()) {
            is Result.Success -> dispatch(MainAppAction.FetchAlgorithmNamesSuccess(result.data))
            is Result.Error -> dispatch(MainAppAction.FetchAlgorithmNamesFailed(result.domainError))
        }
    }
}
