package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.*
import com.inzynierka.ui.AppManager
import com.inzynierka.ui.StringResources.ALGORITHM
import com.inzynierka.ui.StringResources.BEST
import com.inzynierka.ui.StringResources.CEC2022_RANKING_DESCRIPTION
import com.inzynierka.ui.StringResources.CEC2022_RANKING_TABLE_HEADER
import com.inzynierka.ui.StringResources.CEC2022_TAB_LABEL
import com.inzynierka.ui.StringResources.COMBINED_CEC2022_RANKING_TABLE_HEADER
import com.inzynierka.ui.StringResources.COMPARE_TWO_ALGORITHMS_TAB_LABEL
import com.inzynierka.ui.StringResources.ECDF_TAB_LABEL
import com.inzynierka.ui.StringResources.FRIEDMAN_RANKING_DESCRIPTION
import com.inzynierka.ui.StringResources.FRIEDMAN_TAB_LABEL
import com.inzynierka.ui.StringResources.MEAN
import com.inzynierka.ui.StringResources.MEAN_TAB_LABEL
import com.inzynierka.ui.StringResources.MEDIAN
import com.inzynierka.ui.StringResources.MEDIAN_TAB_LABEL
import com.inzynierka.ui.StringResources.RANK
import com.inzynierka.ui.StringResources.REVISITED_TAB_LABEL
import com.inzynierka.ui.StringResources.STDEV
import com.inzynierka.ui.StringResources.WORST
import com.inzynierka.ui.divider
import com.inzynierka.ui.tabButtonStyle
import io.kvision.core.*
import io.kvision.form.select.select
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.utils.px

fun Container.rankings(
    state: RankingsState,
    tab: Tab.ResultsTab,
) {
    flexPanel(direction = FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
        rankingTabs(tab, state.selectedBenchmarkName)
        select(
            options = state.benchmarkNames.map { it to it },
            value = state.selectedBenchmarkName,
            label = "Benchmark"
        ) {
            width = 250.px
        }.onChange {
            AppManager.store.dispatch(
                RankingsAction.BenchmarkSelected(
                    benchmarkName = this.value!!,
                )
            )
            AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Cec2022))
            AppManager.loadCec2022Scores(this.value!!)
        }
        when (tab) {
            is Tab.ResultsTab.Cec2022 -> {
                h5(CEC2022_RANKING_DESCRIPTION)
                divider()
                scoreRanking(
                    state.cec2022,
                    scoreHeaderTitle = CEC2022_RANKING_TABLE_HEADER,
                    combinedScoreHeaderTitle = COMBINED_CEC2022_RANKING_TABLE_HEADER
                )
            }

            is Tab.ResultsTab.Friedman -> {
                h5(FRIEDMAN_RANKING_DESCRIPTION)
                divider()
                friedmanTab(state.friedman)
            }

            is Tab.ResultsTab.Mean -> {
                statisticsRanking(
                    headerNames = listOf(RANK, ALGORITHM, MEAN, MEDIAN, STDEV, BEST, WORST),
                    state = state.mean,
                    onHandleError = { AppManager.store.dispatch(StatisticsRankingAction.ErrorHandled) },
                    toggleNumberNotation = { AppManager.store.dispatch(StatisticsRankingAction.ToggleNumberNotation) },
                    changePrecision = { AppManager.store.dispatch(StatisticsRankingAction.ChangePrecision(it)) }
                )
            }

            is Tab.ResultsTab.Median -> {
                statisticsRanking(
                    headerNames = listOf(RANK, ALGORITHM, MEAN, MEDIAN, STDEV, BEST, WORST),
                    state = state.mean,
                    onHandleError = { AppManager.store.dispatch(StatisticsRankingAction.ErrorHandled) },
                    toggleNumberNotation = { AppManager.store.dispatch(StatisticsRankingAction.ToggleNumberNotation) },
                    changePrecision = { AppManager.store.dispatch(StatisticsRankingAction.ChangePrecision(it)) }
                )
            }

            is Tab.ResultsTab.PairTest -> {
                state.selectedBenchmarkName?.let { pairTest(state.pairTest, it) }
            }

            is Tab.ResultsTab.Ecdf -> {
                ecdfTab(
                    state = state.ecdf
                )
            }

            is Tab.ResultsTab.Revisited -> {
                revisitedRanking(state.revisited)
            }
        }
    }
}


fun Container.rankingTabs(tab: Tab.ResultsTab, benchmarkName: String?) {
    if (benchmarkName == null) return
    flexPanel(direction = FlexDirection.ROW, spacing = 8) {
        padding = 16.px
        paddingBottom = 32.px
        button(
            text = CEC2022_TAB_LABEL,
            style = tabButtonStyle(tab is Tab.ResultsTab.Cec2022)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Cec2022))
                AppManager.loadCec2022Scores(benchmarkName)
            }
        button(
            text = MEAN_TAB_LABEL,
            style = tabButtonStyle(tab is Tab.ResultsTab.Mean)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Mean))
                AppManager.loadStatisticsRanking(benchmarkName, RankingType.Mean)
            }
        button(
            text = MEDIAN_TAB_LABEL,
            style = tabButtonStyle(tab is Tab.ResultsTab.Median)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Median))
                AppManager.loadStatisticsRanking(benchmarkName, RankingType.Median)
            }
        button(
            text = ECDF_TAB_LABEL,
            style = tabButtonStyle(tab is Tab.ResultsTab.Ecdf)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Ecdf))
                AppManager.loadEcdfData(benchmarkName)
            }
        button(
            text = FRIEDMAN_TAB_LABEL,
            style = tabButtonStyle(tab is Tab.ResultsTab.Friedman)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Friedman))
                AppManager.loadFriedmanScores(benchmarkName)
            }
        button(
            text = COMPARE_TWO_ALGORITHMS_TAB_LABEL,
            style = tabButtonStyle(tab is Tab.ResultsTab.PairTest)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.PairTest))
                AppManager.getAvailableBenchmarkData(benchmarkName)
            }
        button(
            text = REVISITED_TAB_LABEL,
            style = tabButtonStyle(
                tab is Tab.ResultsTab.Revisited
            )
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Revisited))
                AppManager.loadRevisitedRanking(benchmarkName)
            }
    }
}