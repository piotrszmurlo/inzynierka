package com.inzynierka.ui

import com.inzynierka.common.Result
import com.inzynierka.domain.core.*
import com.inzynierka.domain.service.IDataService
import com.inzynierka.ui.StringResources.TOAST_FAILED_TO_LOAD_RANKING
import io.kvision.redux.createTypedReduxStore
import io.kvision.toast.Toast
import io.kvision.types.KFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AppManager : CoroutineScope by CoroutineScope(Dispatchers.Default + SupervisorJob()), KoinComponent {

    private val initialMainAppState = MainAppState(tab = Tab.Upload, error = null)
    val store = createTypedReduxStore(::mainAppReducer, initialMainAppState)
    private val dataService: IDataService by inject()

    fun loadMeanRanking() {
        launch {
            store.dispatch(MeanRankingAction.FetchRankingsStarted)
            when (val result = dataService.getStatisticsRankingEntries()) {
                is Result.Success -> store.dispatch(MeanRankingAction.FetchRankingsSuccess(result.data))
                is Result.Error -> {
                    Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                    store.dispatch(MeanRankingAction.FetchRankingsFailed(result.domainError))
                }
            }
        }
    }

    fun uploadFiles(files: List<KFile>) = launch {
        store.dispatch(UploadAction.UploadFileStarted)
        when (val result = dataService.postFiles(files)) {
            is Result.Success -> {
                store.dispatch(UploadAction.UploadFileSuccess)
            }

            is Result.Error -> {
                store.dispatch(UploadAction.UploadFileFailed(result.domainError))
            }
        }
    }

    fun loadFriedmanScores() = launch {
        store.dispatch(FriedmanRankingAction.FetchRankingsStarted)
        when (val result = dataService.getFriedmanScores()) {
            is Result.Success -> store.dispatch(FriedmanRankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                store.dispatch(FriedmanRankingAction.FetchRankingsFailed(result.domainError))
            }
        }
    }

    fun getAvailableBenchmarkData() = launch {
        store.dispatch(PairTestAction.Initialize)
        when (val result = dataService.getAvailableBenchmarkData()) {
            is Result.Success -> store.dispatch(PairTestAction.InitializeSuccess(result.data))
            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                store.dispatch(PairTestAction.InitializeFailed(result.domainError))
            }
        }
    }

    fun loadRevisitedRanking() = launch {
        store.dispatch(RevisitedRankingAction.FetchRankingsStarted)
        when (val result = dataService.getRevisitedRankingEntries()) {
            is Result.Success -> store.dispatch(RevisitedRankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                store.dispatch(RevisitedRankingAction.FetchRankingsFailed(result.domainError))
            }
        }
    }

    fun loadEcdfData() = launch {
        store.dispatch(FriedmanRankingAction.FetchRankingsStarted)
        when (val result = dataService.getEcdfData()) {
            is Result.Success -> store.dispatch(EcdfAction.FetchRankingsSuccess(result.data))
            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                store.dispatch(EcdfAction.FetchRankingsFailed(result.domainError))
            }
        }
    }

    fun loadMedianRanking() = launch {
        store.dispatch(MedianRankingAction.FetchRankingsStarted)
        when (val result = dataService.getStatisticsRankingEntries()) {
            is Result.Success -> store.dispatch(MedianRankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                store.dispatch(MedianRankingAction.FetchRankingsFailed(result.domainError))
            }
        }
    }

    fun loadCec2022Scores() = launch {
        store.dispatch(Cec2022RankingAction.FetchRankingsStarted)
        when (val result = dataService.getCec2022Scores()) {
            is Result.Success -> store.dispatch(Cec2022RankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                store.dispatch(Cec2022RankingAction.FetchRankingsFailed(result.domainError))
            }
        }
    }

    fun performPairTest(
        algorithmFirst: String,
        algorithmSecond: String,
        dimension: Int
    ) = launch {
        store.dispatch(PairTestAction.PerformPairTest)
        val result = dataService.getPairTest(
            algorithmFirst,
            algorithmSecond,
            dimension
        )
        when (result) {
            is Result.Success -> store.dispatch(PairTestAction.PairTestSuccess(result.data))
            is Result.Error -> store.dispatch(PairTestAction.PairTestFailed(result.domainError))
        }
    }

}