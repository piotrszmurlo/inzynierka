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
import com.inzynierka.ui.StringResources.FRIEDMAN_RANKING_TABLE_HEADER
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
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.onClickLaunch
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.utils.px

fun Container.rankings(
    state: RankingsState,
    tab: Tab.ResultsTab,
) {
    flexPanel(direction = FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
        rankingTabs(tab)
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
                scoreRanking(
                    state.friedman,
                    scoreHeaderTitle = FRIEDMAN_RANKING_TABLE_HEADER,
                    combinedScoreHeaderTitle = FRIEDMAN_RANKING_TABLE_HEADER
                )
            }

            is Tab.ResultsTab.Mean -> {
                statisticsRanking(
                    headerNames = listOf(RANK, ALGORITHM, MEAN, MEDIAN, STDEV, BEST, WORST),
                    state = state.mean,
                    toggleNumberNotation = { AppManager.store.dispatch(MeanRankingAction.ToggleNumberNotation) },
                    changePrecision = { AppManager.store.dispatch(MeanRankingAction.ChangePrecision(it)) }
                )
            }

            is Tab.ResultsTab.Median -> {
                statisticsRanking(
                    headerNames = listOf(RANK, ALGORITHM, MEAN, MEDIAN, STDEV, BEST, WORST),
                    state = state.median,
                    toggleNumberNotation = { AppManager.store.dispatch(MedianRankingAction.ToggleNumberNotation) },
                    changePrecision = { AppManager.store.dispatch(MedianRankingAction.ChangePrecision(it)) }
                )
            }

            is Tab.ResultsTab.PairTest -> {
                pairTest(state.pairTest)
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


fun Container.rankingTabs(tab: Tab.ResultsTab) {
    flexPanel(direction = FlexDirection.ROW, spacing = 8) {
        padding = 16.px
        paddingBottom = 32.px
        button(
            text = CEC2022_TAB_LABEL,
            style = tabButtonStyle(tab is Tab.ResultsTab.Cec2022)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Cec2022))
                AppManager.loadCec2022Scores()
            }
        button(
            text = MEAN_TAB_LABEL,
            style = tabButtonStyle(tab is Tab.ResultsTab.Mean)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Mean))
                AppManager.loadMeanRanking()
            }
        button(
            text = MEDIAN_TAB_LABEL,
            style = tabButtonStyle(tab is Tab.ResultsTab.Median)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Median))
                AppManager.loadMedianRanking()
            }
        button(
            text = ECDF_TAB_LABEL,
            style = tabButtonStyle(tab is Tab.ResultsTab.Ecdf)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Ecdf))
                AppManager.loadEcdfData()
            }
        button(
            text = FRIEDMAN_TAB_LABEL,
            style = tabButtonStyle(tab is Tab.ResultsTab.Friedman)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Friedman))
                AppManager.loadFriedmanScores()
            }
        button(
            text = COMPARE_TWO_ALGORITHMS_TAB_LABEL,
            style = tabButtonStyle(tab is Tab.ResultsTab.PairTest)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.PairTest))
                AppManager.getAvailableBenchmarkData()
            }
        button(
            text = REVISITED_TAB_LABEL,
            style = tabButtonStyle(
                tab is Tab.ResultsTab.Revisited
            )
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Revisited))
                AppManager.loadRevisitedRanking()
            }
    }
}