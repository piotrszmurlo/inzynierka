package com.inzynierka.domain

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.domain.core.*
import com.inzynierka.domain.service.IDataService
import com.inzynierka.ui.show
import io.kvision.redux.Dispatch
import io.kvision.toast.Toast
import io.kvision.types.KFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkActions(private val dataService: IDataService) {
    fun loadMeanRanking(dispatch: Dispatch<MainAppAction>) {
        CoroutineScope(Dispatchers.Default).launch {
            dispatch(MeanRankingAction.FetchRankingsStarted)
            when (val result = dataService.getStatisticsRankingEntries()) {
                is Result.Success -> dispatch(MeanRankingAction.FetchRankingsSuccess(result.data))
                is Result.Error -> {
                    Toast.show("Ranking fetch failed")
                    dispatch(MeanRankingAction.FetchRankingsFailed(result.domainError))
                }
            }
        }
    }

    suspend fun uploadFiles(dispatch: Dispatch<MainAppAction>, files: List<KFile>) {
        dispatch(UploadAction.UploadFileStarted)
        when (val result = dataService.postFiles(files)) {
            is Result.Success -> {
                Toast.show("File upload completed")
                dispatch(UploadAction.UploadFileSuccess)
            }

            is Result.Error -> {
                dispatch(UploadAction.UploadFileFailed(result.domainError))
                Toast.show("File upload failed: " + (result.domainError as DomainError.FileUploadError).message)
            }
        }
    }

    fun loadFriedmanScores(dispatch: Dispatch<MainAppAction>) {
        CoroutineScope(Dispatchers.Default).launch {
            dispatch(FriedmanRankingAction.FetchRankingsStarted)
            when (val result = dataService.getFriedmanScores()) {
                is Result.Success -> dispatch(FriedmanRankingAction.FetchRankingsSuccess(result.data))
                is Result.Error -> {
                    Toast.show("Ranking fetch failed")
                    dispatch(FriedmanRankingAction.FetchRankingsFailed(result.domainError))
                }
            }
        }
    }

    fun getAvailableBenchmarkData(dispatch: Dispatch<MainAppAction>) {
        CoroutineScope(Dispatchers.Default).launch {
            dispatch(PairTestAction.Initialize)
            when (val result = dataService.getAvailableBenchmarkData()) {
                is Result.Success -> dispatch(PairTestAction.InitializeSuccess(result.data))
                is Result.Error -> {
                    Toast.show("Ranking fetch failed")
                    dispatch(PairTestAction.InitializeFailed(result.domainError))
                }
            }
        }
    }

    fun loadRevisitedRanking(dispatch: Dispatch<MainAppAction>) {
        CoroutineScope(Dispatchers.Default).launch {
            dispatch(RevisitedRankingAction.FetchRankingsStarted)
            when (val result = dataService.getRevisitedRankingEntries()) {
                is Result.Success -> dispatch(RevisitedRankingAction.FetchRankingsSuccess(result.data))
                is Result.Error -> {
                    Toast.show("Ranking fetch failed")
                    dispatch(RevisitedRankingAction.FetchRankingsFailed(result.domainError))
                }
            }
        }
    }

    fun loadEcdfData(dispatch: Dispatch<MainAppAction>) {
        CoroutineScope(Dispatchers.Default).launch {
            dispatch(FriedmanRankingAction.FetchRankingsStarted)
            when (val result = dataService.getEcdfData()) {
                is Result.Success -> dispatch(EcdfAction.FetchRankingsSuccess(result.data))
                is Result.Error -> {
                    Toast.show("Ranking fetch failed")
                    dispatch(EcdfAction.FetchRankingsFailed(result.domainError))
                }
            }
        }
    }

    fun loadMedianRanking(dispatch: Dispatch<MainAppAction>) {
        CoroutineScope(Dispatchers.Default).launch {
            dispatch(MedianRankingAction.FetchRankingsStarted)
            when (val result = dataService.getStatisticsRankingEntries()) {
                is Result.Success -> dispatch(MedianRankingAction.FetchRankingsSuccess(result.data))
                is Result.Error -> {
                    Toast.show("Ranking fetch failed")
                    dispatch(MedianRankingAction.FetchRankingsFailed(result.domainError))
                }
            }
        }
    }

    fun loadCec2022Scores(dispatch: Dispatch<MainAppAction>) {
        CoroutineScope(Dispatchers.Default).launch {
            dispatch(Cec2022RankingAction.FetchRankingsStarted)
            when (val result = dataService.getCec2022Scores()) {
                is Result.Success -> dispatch(Cec2022RankingAction.FetchRankingsSuccess(result.data))
                is Result.Error -> {
                    Toast.show("Ranking fetch failed")
                    dispatch(Cec2022RankingAction.FetchRankingsFailed(result.domainError))
                }
            }
        }
    }

    fun performPairTest(
        dispatch: Dispatch<MainAppAction>,
        algorithmFirst: String,
        algorithmSecond: String,
        dimension: Int
    ) {
        dispatch(PairTestAction.PerformPairTest)
        CoroutineScope(Dispatchers.Default).launch {
            val result = dataService.getPairTest(
                algorithmFirst,
                algorithmSecond,
                dimension
            )
            when (result) {
                is Result.Success -> dispatch(PairTestAction.PairTestSuccess(result.data))
                is Result.Error -> dispatch(PairTestAction.PairTestFailed(result.domainError))
            }
        }
    }
}