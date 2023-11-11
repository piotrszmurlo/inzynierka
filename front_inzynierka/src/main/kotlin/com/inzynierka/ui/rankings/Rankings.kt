package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.*
import com.inzynierka.ui.AppManager
import com.inzynierka.ui.tabButtonStyle
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.onClickLaunch
import io.kvision.html.button
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
                scoreRanking(
                    state.cec2022,
                    scoreHeaderTitle = "CEC 2022 score",
                    combinedScoreHeaderTitle = "combined CEC 2022 score"
                )
            }

            is Tab.ResultsTab.Friedman -> {
                scoreRanking(
                    state.friedman,
                    scoreHeaderTitle = "Average trial rank",
                    combinedScoreHeaderTitle = "Average trial rank"
                )
            }

            is Tab.ResultsTab.Mean -> {
                statisticsRanking(
                    headerNames = listOf("Rank", "Algorithm", "Mean", "Median", "Stddev", "Best", "Worst"),
                    state = state.mean,
                    toggleNumberNotation = { AppManager.store.dispatch(MeanRankingAction.ToggleNumberNotation) },
                    changePrecision = { AppManager.store.dispatch(MeanRankingAction.ChangePrecision(it)) }
                )
            }

            is Tab.ResultsTab.Median -> {
                statisticsRanking(
                    headerNames = listOf("Rank", "Algorithm", "Mean", "Median", "Stddev", "Best", "Worst"),
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
            text = "CEC 2022",
            style = tabButtonStyle(tab is Tab.ResultsTab.Cec2022)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Cec2022))
                AppManager.loadCec2022Scores()
            }
        button(
            text = "Mean",
            style = tabButtonStyle(tab is Tab.ResultsTab.Mean)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Mean))
                AppManager.loadMeanRanking()
            }
        button(
            text = "Median",
            style = tabButtonStyle(tab is Tab.ResultsTab.Median)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Median))
                AppManager.loadMedianRanking()
            }
        button(
            text = "ECDF",
            style = tabButtonStyle(tab is Tab.ResultsTab.Ecdf)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Ecdf))
                AppManager.loadEcdfData()
            }
        button(
            text = "Friedman",
            style = tabButtonStyle(tab is Tab.ResultsTab.Friedman)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Friedman))
                AppManager.loadFriedmanScores()
            }
        button(
            "Compare two algorithms",
            style = tabButtonStyle(tab is Tab.ResultsTab.PairTest)
        )
            .onClickLaunch {
                AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.PairTest))
                AppManager.getAvailableBenchmarkData()
            }
        button(
            text = "Revisited Ranking",
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