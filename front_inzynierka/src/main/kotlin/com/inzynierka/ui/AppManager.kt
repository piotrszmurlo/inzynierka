package com.inzynierka.ui

import com.inzynierka.common.Result
import com.inzynierka.domain.core.*
import com.inzynierka.domain.service.IDataService
import com.inzynierka.ui.StringResources.EMAIL_FAILED
import com.inzynierka.ui.StringResources.EMAIL_SENT
import com.inzynierka.ui.StringResources.LOGIN_FAILED_TOAST
import com.inzynierka.ui.StringResources.REGISTER_ERROR_TOAST
import com.inzynierka.ui.StringResources.REGISTER_SUCCESS_TOAST
import com.inzynierka.ui.StringResources.TOAST_DELETE_ALGORITHM_DATA_SUCCESS
import com.inzynierka.ui.StringResources.TOAST_FAILED_TO_DELETE_ALGORITHM_DATA
import com.inzynierka.ui.StringResources.TOAST_FAILED_TO_LOAD_ADMIN_CONSOLE
import com.inzynierka.ui.StringResources.TOAST_FAILED_TO_LOAD_RANKING
import com.inzynierka.ui.StringResources.TOAST_FAILED_TO_PROMOTE_USER
import com.inzynierka.ui.StringResources.TOAST_PROMOTE_USER_SUCCESS
import com.inzynierka.ui.StringResources.TOAST_VERIFY_USER_FAILED
import com.inzynierka.ui.StringResources.TOAST_VERIFY_USER_SUCCESS
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

    private val initialMainAppState = MainAppState(tab = Tab.Upload)
    private val dataService: IDataService by inject()
    val store = createTypedReduxStore(::mainAppReducer, initialMainAppState)

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

    fun uploadFiles(files: List<KFile>, overwriteExisting: Boolean) = launch {
        store.dispatch(UploadAction.UploadFileStarted)
        when (val result = dataService.postFiles(files, overwriteExisting)) {
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

    fun loadAdminConsole() = launch {
        store.dispatch(AdminConsoleAction.FetchAlgorithmsStarted)
        when (val result = dataService.getAvailableBenchmarkData()) {
            is Result.Success -> store.dispatch(AdminConsoleAction.FetchAlgorithmsSuccess(result.data.algorithms))
            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_ADMIN_CONSOLE)
                store.dispatch(AdminConsoleAction.FetchAlgorithmsFailed)
            }
        }
    }

    fun promoteUserToAdmin(email: String) = launch {
        store.dispatch(AdminConsoleAction.PromoteUserStarted)
        when (dataService.promoteUserToAdmin(email)) {
            is Result.Success -> {
                Toast.show(TOAST_PROMOTE_USER_SUCCESS)
                store.dispatch(AdminConsoleAction.PromoteUserSuccess)
            }

            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_PROMOTE_USER)
                store.dispatch(AdminConsoleAction.PromoteUserFailed)
            }
        }
    }

    fun verifyCurrentUser(code: String) = launch {
        store.dispatch(AdminConsoleAction.PromoteUserStarted)
        when (dataService.verifyAccount(code)) {
            is Result.Success -> {
                Toast.show(TOAST_VERIFY_USER_SUCCESS)
                when (val userData = dataService.getUserData()) {
                    is Result.Success -> {
                        store.dispatch(LoginAction.LoginSuccess(userData.data))
                    }

                    is Result.Error -> store.dispatch(LoginAction.LoginSuccess(null))
                }
                store.dispatch(MainAppAction.TabSelected(Tab.Upload))
                store.dispatch(AdminConsoleAction.VerifyUserSuccess)
            }

            is Result.Error -> {
                Toast.show(TOAST_VERIFY_USER_FAILED)
                store.dispatch(AdminConsoleAction.VerifyUserFailed)
            }
        }
    }

    fun resendVerificationCode() = launch {
        when (dataService.resendVerificationCode()) {
            is Result.Error -> Toast.show(EMAIL_FAILED)
            is Result.Success -> Toast.show(EMAIL_SENT)
        }


    }

    fun deleteAlgorithmData(algorithmName: String) = launch {
        store.dispatch(AdminConsoleAction.DeleteAlgorithmStarted)
        when (dataService.deleteFilesForAlgorithm(algorithmName)) {
            is Result.Success -> {
                Toast.show(TOAST_DELETE_ALGORITHM_DATA_SUCCESS)
                loadAdminConsole()
                store.dispatch(AdminConsoleAction.DeleteAlgorithmSuccess)
            }

            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_DELETE_ALGORITHM_DATA)
                store.dispatch(AdminConsoleAction.DeleteAlgorithmFailed)
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

    fun loginUser(email: String, password: String) = launch {
        store.dispatch(LoginAction.Login)
        when (val result = dataService.loginUser(email, password)) {
            is Result.Success -> {
                when (val userData = dataService.getUserData()) {
                    is Result.Success -> {
                        store.dispatch(LoginAction.LoginSuccess(userData.data))
                    }

                    is Result.Error -> store.dispatch(LoginAction.LoginSuccess(null))
                }
                store.dispatch(MainAppAction.TabSelected(Tab.Upload))
            }

            is Result.Error -> {
                Toast.show(LOGIN_FAILED_TOAST)
                store.dispatch(LoginAction.LoginFailed(result.domainError))
            }
        }
    }

    fun registerUser(email: String, password: String) = launch {
        store.dispatch(LoginAction.Register)
        when (val result = dataService.registerUser(email, password)) {
            is Result.Success -> {
                when (val userData = dataService.getUserData()) {
                    is Result.Success -> {
                        store.dispatch(LoginAction.RegisterSuccess(userData.data))
                        store.dispatch(MainAppAction.TabSelected(Tab.Login))
                    }

                    is Result.Error -> {
                        store.dispatch(LoginAction.RegisterSuccess(null))
                        store.dispatch(MainAppAction.TabSelected(Tab.Upload))
                    }

                }
                Toast.show(REGISTER_SUCCESS_TOAST)
            }

            is Result.Error -> {
                Toast.show(REGISTER_ERROR_TOAST)
                store.dispatch(LoginAction.RegisterFailed(result.domainError))
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