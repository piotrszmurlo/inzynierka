package com.inzynierka.ui

import com.inzynierka.common.Result
import com.inzynierka.domain.core.*
import com.inzynierka.domain.service.IBenchmarkService
import com.inzynierka.domain.service.IFileService
import com.inzynierka.domain.service.IRankingsService
import com.inzynierka.domain.service.IUserService
import com.inzynierka.ui.StringResources.EMAIL_FAILED
import com.inzynierka.ui.StringResources.EMAIL_SENT
import com.inzynierka.ui.StringResources.LOGIN_FAILED_TOAST
import com.inzynierka.ui.StringResources.REGISTER_ERROR_TOAST
import com.inzynierka.ui.StringResources.REGISTER_SUCCESS_TOAST
import com.inzynierka.ui.StringResources.TOAST_CREATE_BENCHMARK_FAILED
import com.inzynierka.ui.StringResources.TOAST_CREATE_BENCHMARK_SUCCESS
import com.inzynierka.ui.StringResources.TOAST_DELETE_BENCHMARK_DATA_FAILED
import com.inzynierka.ui.StringResources.TOAST_DELETE_BENCHMARK_DATA_SUCCESS
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

    private val initialMainAppState = MainAppState(tab = Tab.Login)
    private val userService: IUserService by inject()
    private val fileService: IFileService by inject()
    private val benchmarkService: IBenchmarkService by inject()
    private val rankingsService: IRankingsService by inject()
    val store = createTypedReduxStore(::mainAppReducer, initialMainAppState)

    fun loadStatisticsRanking(benchmarkName: String, statisticsRankingType: StatisticsRankingType) {
        launch {
            store.dispatch(StatisticsRankingAction.FetchRankingsStarted)
            when (val result = rankingsService.getStatisticsRankingEntries(benchmarkName)) {
                is Result.Success -> store.dispatch(
                    StatisticsRankingAction.FetchRankingsSuccess(
                        result.data,
                        statisticsRankingType
                    )
                )

                is Result.Error -> {
                    Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                    store.dispatch(StatisticsRankingAction.FetchRankingsFailed(result.domainError))
                }
            }
        }
    }

    fun initializeUploadTab() {
        launch {
            when (val result = benchmarkService.getAvailableBenchmarks()) {
                is Result.Success -> store.dispatch(UploadAction.FetchAvailableBenchmarksSuccess(result.data))
                is Result.Error -> {
                    store.dispatch(UploadAction.FetchAvailableBenchmarksFailed(result.domainError))
                }
            }
        }
    }

    fun uploadFiles(files: List<KFile>, benchmarkName: String, overwriteExisting: Boolean) = launch {
        store.dispatch(UploadAction.UploadFileStarted)
        when (val result = fileService.postFiles(files, benchmarkName, overwriteExisting)) {
            is Result.Success -> {
                store.dispatch(UploadAction.UploadFileSuccess)
            }

            is Result.Error -> {
                store.dispatch(UploadAction.UploadFileFailed(result.domainError))
            }
        }
    }

    fun loadFriedmanScores(benchmarkName: String) = launch {
        store.dispatch(FriedmanRankingAction.FetchRankingsStarted)
        when (val result = rankingsService.getFriedmanScores(benchmarkName)) {
            is Result.Success -> store.dispatch(FriedmanRankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                store.dispatch(FriedmanRankingAction.FetchRankingsFailed(result.domainError))
            }
        }
    }

    fun getAvailableBenchmarkData(benchmarkName: String) = launch {
        store.dispatch(PairTestAction.Initialize)
        when (val result = benchmarkService.getAvailableBenchmarkData(benchmarkName)) {
            is Result.Success -> store.dispatch(PairTestAction.InitializeSuccess(result.data))
            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                store.dispatch(PairTestAction.InitializeFailed(result.domainError))
            }
        }
    }

    fun deleteBenchmark(benchmarkName: String) = launch {
        when (benchmarkService.deleteBenchmark(benchmarkName)) {
            is Result.Success -> {
                store.dispatch(AdminConsoleAction.CreateBenchmarkSuccess)
                Toast.show(TOAST_DELETE_BENCHMARK_DATA_SUCCESS)
                loadAdminConsole()
            }

            is Result.Error -> {
                store.dispatch(AdminConsoleAction.CreateBenchmarkFailed)
                Toast.show(TOAST_DELETE_BENCHMARK_DATA_FAILED)
            }
        }

    }

    fun loadRevisitedRanking(benchmarkName: String) = launch {
        store.dispatch(RevisitedRankingAction.FetchRankingsStarted)
        when (val result = rankingsService.getRevisitedRankingEntries(benchmarkName)) {
            is Result.Success -> store.dispatch(RevisitedRankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                store.dispatch(RevisitedRankingAction.FetchRankingsFailed(result.domainError))
            }
        }
    }

    fun loadEcdfData(benchmarkName: String) = launch {
        store.dispatch(FriedmanRankingAction.FetchRankingsStarted)
        when (val result = rankingsService.getEcdfData(benchmarkName)) {
            is Result.Success -> store.dispatch(EcdfAction.FetchRankingsSuccess(result.data))
            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                store.dispatch(EcdfAction.FetchRankingsFailed(result.domainError))
            }
        }
    }

    fun loadAdminConsole() = launch {
        when (val result = benchmarkService.getAvailableBenchmarks()) {
            is Result.Success -> {
                store.dispatch(AdminConsoleAction.FetchBenchmarksSuccess(result.data))
                result.data.firstOrNull()?.let {
                    getAlgorithmNamesForBenchmark(
                        it.name,
                        actionOnsuccess = { store.dispatch(AdminConsoleAction.FetchAlgorithmsSuccess(it)) },
                        actionOnFail = { store.dispatch(AdminConsoleAction.FetchAlgorithmsFailed) }
                    )
                } ?: store.dispatch(AdminConsoleAction.FetchAlgorithmsFailed)
            }

            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_ADMIN_CONSOLE)
                store.dispatch(AdminConsoleAction.FetchBenchmarksFailed)
            }
        }
    }

    fun loadAccountSettings() = launch {
        when (val result = benchmarkService.getAvailableBenchmarks()) {
            is Result.Success -> {
                store.dispatch(AccountSettingsAction.FetchBenchmarksSuccess(result.data))
                result.data.firstOrNull()?.let {
                    getCurrentUserOwnedAlgorithmNamesForBenchmark(
                        it.name,
                        actionOnsuccess = { store.dispatch(AccountSettingsAction.FetchAlgorithmsSuccess(it)) },
                        actionOnFail = { store.dispatch(AccountSettingsAction.FetchAlgorithmsFailed) })
                } ?: store.dispatch(AccountSettingsAction.FetchAlgorithmsFailed)
            }

            is Result.Error -> {
                store.dispatch(AccountSettingsAction.FetchBenchmarksFailed)
            }
        }
    }

    fun initializeRankings(initialBenchmark: String?) = launch {
        when (val result = benchmarkService.getAvailableBenchmarks()) {
            is Result.Success -> {
                store.dispatch(RankingsAction.FetchAvailableBenchmarksSuccess(result.data))
                initialBenchmark?.let {
                    loadCec2022Scores(it)
                } ?: result.data.firstOrNull()?.let { loadCec2022Scores(it.name) }
            }

            is Result.Error -> {
                store.dispatch(RankingsAction.FetchAvailableBenchmarksFailed(result.domainError))
            }
        }
    }

    fun getAlgorithmNamesForBenchmark(
        benchmarkName: String,
        actionOnsuccess: (List<String>) -> Unit,
        actionOnFail: () -> Unit
    ) = launch {
        when (val algorithmsResult = benchmarkService.getAvailableBenchmarkData(benchmarkName)) {
            is Result.Success -> actionOnsuccess(algorithmsResult.data.algorithms)
            is Result.Error -> actionOnFail()
        }
    }

    fun getCurrentUserOwnedAlgorithmNamesForBenchmark(
        benchmarkName: String,
        actionOnsuccess: (List<String>) -> Unit,
        actionOnFail: () -> Unit
    ) = launch {
        when (val algorithmsResult = benchmarkService.getMyAlgorithms(benchmarkName)) {
            is Result.Success -> actionOnsuccess(algorithmsResult.data)
            is Result.Error -> actionOnFail()
        }
    }

    fun promoteUserToAdmin(email: String) = launch {
        store.dispatch(AdminConsoleAction.PromoteUserStarted)
        when (userService.promoteUserToAdmin(email)) {
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
        when (userService.verifyAccount(code)) {
            is Result.Success -> {
                Toast.show(TOAST_VERIFY_USER_SUCCESS)
                when (val userData = userService.getUserData()) {
                    is Result.Success -> {
                        store.dispatch(LoginAction.LoginSuccess(userData.data))
                    }

                    is Result.Error -> store.dispatch(LoginAction.LoginSuccess(null))
                }
                initializeUploadTab()
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
        when (userService.resendVerificationCode()) {
            is Result.Error -> Toast.show(EMAIL_FAILED)
            is Result.Success -> Toast.show(EMAIL_SENT)
        }


    }

    fun deleteAlgorithmData(
        algorithmName: String,
        benchmarkName: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) = launch {
        store.dispatch(AdminConsoleAction.DeleteAlgorithmStarted)
        when (fileService.deleteFilesForAlgorithm(algorithmName, benchmarkName)) {
            is Result.Success -> onSuccess()
            is Result.Error -> onError()
        }
    }

    fun loadCec2022Scores(benchmarkName: String) = launch {
        store.dispatch(Cec2022RankingAction.FetchRankingsStarted)
        when (val result = rankingsService.getCec2022Scores(benchmarkName)) {
            is Result.Success -> store.dispatch(Cec2022RankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> {
                Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
                store.dispatch(Cec2022RankingAction.FetchRankingsFailed(result.domainError))
            }
        }
    }

    fun loginUser(email: String, password: String) = launch {
        store.dispatch(LoginAction.Login)
        when (val result = userService.loginUser(email, password)) {
            is Result.Success -> {
                refreshUserData()
                store.dispatch(MainAppAction.TabSelected(Tab.Upload))
            }

            is Result.Error -> {
                Toast.show(LOGIN_FAILED_TOAST)
                store.dispatch(LoginAction.LoginFailed(result.domainError))
            }
        }
    }

    fun logoutUser() {
        userService.logout()
    }

    private fun refreshUserData() = launch {
        when (val userData = userService.getUserData()) {
            is Result.Success -> {
                store.dispatch(LoginAction.LoginSuccess(userData.data))
            }

            is Result.Error -> store.dispatch(LoginAction.LoginSuccess(null))
        }
        initializeUploadTab()
    }

    fun registerUser(email: String, password: String) = launch {
        store.dispatch(LoginAction.Register)
        when (val result = userService.registerUser(email, password)) {
            is Result.Success -> {
                when (val userData = userService.getUserData()) {
                    is Result.Success -> {
                        store.dispatch(LoginAction.RegisterSuccess(userData.data))
                        store.dispatch(MainAppAction.TabSelected(Tab.Login))
                    }

                    is Result.Error -> {
                        store.dispatch(LoginAction.RegisterSuccess(null))
                        initializeUploadTab()
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

    fun createBenchmark(
        name: String,
        description: String,
        functionCount: Int,
        trialCount: Int
    ) = launch {
        when (benchmarkService.createBenchmark(name, description, functionCount, trialCount)) {
            is Result.Error -> {
                Toast.show(TOAST_CREATE_BENCHMARK_FAILED)
            }

            is Result.Success -> {
                loadAdminConsole()
                Toast.show(TOAST_CREATE_BENCHMARK_SUCCESS)
            }
        }
    }

    fun performPairTest(
        algorithmFirst: String,
        algorithmSecond: String,
        dimension: Int,
        benchmarkName: String
    ) = launch {
        store.dispatch(PairTestAction.PerformPairTest)
        val result = rankingsService.getPairTest(
            algorithmFirst,
            algorithmSecond,
            dimension,
            benchmarkName
        )
        when (result) {
            is Result.Success -> store.dispatch(PairTestAction.PairTestSuccess(result.data))
            is Result.Error -> store.dispatch(PairTestAction.PairTestFailed(result.domainError))
        }
    }

    fun changePassword(newPassword: String, oldPassword: String) = launch {
        store.dispatch(AccountSettingsAction.ChangeStarted)
        when (val result = userService.changePassword(newPassword, oldPassword)) {
            is Result.Success -> store.dispatch(AccountSettingsAction.ChangePasswordSuccess)
            is Result.Error -> store.dispatch(AccountSettingsAction.ChangePasswordFailed(result.domainError))
        }
    }

    fun changeEmail(email: String) = launch {
        store.dispatch(AccountSettingsAction.ChangeStarted)
        when (val result = userService.changeEmail(email)) {
            is Result.Success -> {
                refreshUserData()
                store.dispatch(AccountSettingsAction.ChangeEmailSuccess)
                store.dispatch(MainAppAction.TabSelected(Tab.Login))
            }

            is Result.Error -> store.dispatch(AccountSettingsAction.ChangeEmailFailed(result.domainError))
        }
    }
}