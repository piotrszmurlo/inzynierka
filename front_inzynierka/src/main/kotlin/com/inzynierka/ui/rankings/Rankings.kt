package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.*
import com.inzynierka.ui.AppManager
import com.inzynierka.ui.StringResources.ALGORITHM
import com.inzynierka.ui.StringResources.BENCHMARK
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
        benchmarkSelect(state, tab)
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
        tabButton(
            CEC2022_TAB_LABEL,
            Tab.ResultsTab.Cec2022,
            tab is Tab.ResultsTab.Cec2022
        ) { AppManager.loadCec2022Scores(benchmarkName) }
        tabButton(
            MEAN_TAB_LABEL,
            Tab.ResultsTab.Mean,
            tab is Tab.ResultsTab.Mean
        ) { AppManager.loadStatisticsRanking(benchmarkName, StatisticsRankingType.Mean) }
        tabButton(
            MEDIAN_TAB_LABEL,
            Tab.ResultsTab.Median,
            tab is Tab.ResultsTab.Median
        ) { AppManager.loadStatisticsRanking(benchmarkName, StatisticsRankingType.Median) }
        tabButton(
            ECDF_TAB_LABEL,
            Tab.ResultsTab.Ecdf,
            tab is Tab.ResultsTab.Ecdf
        ) { AppManager.loadEcdfData(benchmarkName) }
        tabButton(
            FRIEDMAN_TAB_LABEL,
            Tab.ResultsTab.Friedman,
            tab is Tab.ResultsTab.Friedman
        ) { AppManager.loadFriedmanScores(benchmarkName) }
        tabButton(
            COMPARE_TWO_ALGORITHMS_TAB_LABEL,
            Tab.ResultsTab.PairTest,
            tab is Tab.ResultsTab.PairTest
        ) { AppManager.getAvailableBenchmarkData(benchmarkName) }
        tabButton(
            REVISITED_TAB_LABEL,
            Tab.ResultsTab.Revisited,
            tab is Tab.ResultsTab.Revisited
        ) { AppManager.loadRevisitedRanking(benchmarkName) }
    }
}

fun Container.tabButton(label: String, tab: Tab.ResultsTab, isSelected: Boolean, loadAction: () -> Unit) = button(
    text = label,
    style = tabButtonStyle(
        isSelected
    )
).onClickLaunch {
    AppManager.store.dispatch(MainAppAction.TabSelected(tab))
    loadAction()
}


fun Container.benchmarkSelect(state: RankingsState, tab: Tab.ResultsTab) {
    select(
        options = state.benchmarkNames.map { it to it },
        value = state.selectedBenchmarkName,
        label = BENCHMARK
    ) {
        width = 250.px
    }.onChange {
        AppManager.store.dispatch(
            RankingsAction.BenchmarkSelected(
                benchmarkName = this.value!!,
            )
        )
        with(this.value!!) {
            AppManager.store.dispatch(MainAppAction.TabSelected(tab))
            when (tab) {
                is Tab.ResultsTab.Cec2022 -> AppManager.loadCec2022Scores(this)
                is Tab.ResultsTab.Ecdf -> AppManager.loadEcdfData(this)
                is Tab.ResultsTab.Friedman -> AppManager.loadFriedmanScores(this)
                is Tab.ResultsTab.Mean -> AppManager.loadStatisticsRanking(this, StatisticsRankingType.Mean)
                is Tab.ResultsTab.Median -> AppManager.loadStatisticsRanking(this, StatisticsRankingType.Median)
                is Tab.ResultsTab.PairTest -> AppManager.getAvailableBenchmarkData(this)
                is Tab.ResultsTab.Revisited -> AppManager.loadRevisitedRanking(this)
            }

        }
    }
}