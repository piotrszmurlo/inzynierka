package com.inzynierka.ui.rankings

import com.inzynierka.domain.NetworkActions
import com.inzynierka.domain.core.*
import io.kvision.core.*
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.panel.flexPanel
import io.kvision.redux.ReduxStore
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.utils.px

fun Container.rankings(
    store: ReduxStore<MainAppState, MainAppAction>,
    networkActions: NetworkActions
) {
    flexPanel(direction = FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
        display = Display.FLEX
        rankingTabs(store, networkActions)

        flexPanel(justify = JustifyContent.CENTER).bind(store.sub { it.tab }) { tab ->
            display = Display.FLEX
            when (tab as? Tab.ResultsTab) {
                is Tab.ResultsTab.Cec2022 -> {
                    div().bind(store.sub { it.rankingsState.cec2022 }) { cec2022 ->
                        scoreRanking(
                            cec2022,
                            scoreHeaderTitle = "CEC 2022 score",
                            combinedScoreHeaderTitle = "combined CEC 2022 score"
                        )
                    }
                }

                is Tab.ResultsTab.Friedman -> {
                    div().bind(store.sub { it.rankingsState.friedman }) { friedman ->
                        scoreRanking(
                            friedman,
                            scoreHeaderTitle = "Average trial rank",
                            combinedScoreHeaderTitle = "Average trial rank"
                        )
                    }
                }

                is Tab.ResultsTab.Mean -> {
                    div().bind(store.sub { it.rankingsState.mean }) { mean ->
                        statisticsRanking(
                            headerNames = listOf("Rank", "Algorithm", "Mean", "Median", "Stddev", "Best", "Worst"),
                            state = mean,
                            toggleNumberNotation = { store.dispatch(MeanRankingAction.ToggleNumberNotation) },
                            changePrecision = { store.dispatch(MeanRankingAction.ChangePrecision(it)) }
                        )
                    }
                }

                is Tab.ResultsTab.Median -> {
                    div().bind(store.sub { it.rankingsState.median }) { median ->
                        statisticsRanking(
                            headerNames = listOf("Rank", "Algorithm", "Mean", "Median", "Stddev", "Best", "Worst"),
                            state = median,
                            toggleNumberNotation = { store.dispatch(MedianRankingAction.ToggleNumberNotation) },
                            changePrecision = { store.dispatch(MedianRankingAction.ChangePrecision(it)) }
                        )
                    }
                }

                is Tab.ResultsTab.PairTest -> {
                    div().bind(store.sub { it.rankingsState.pairTest }) { pairTest ->
                        pairTest(pairTest, store, networkActions)
                    }
                }

                is Tab.ResultsTab.Ecdf -> {
                    div().bind(store.sub { it.rankingsState.ecdf }) { ecdf ->
                        ecdfTab(
                            state = ecdf,
                            perFunctionClicked = { store.dispatch(EcdfAction.EcdfTypeChanged(EcdfType.PerFunction)) },
                            averagedClicked = { store.dispatch(EcdfAction.EcdfTypeChanged(EcdfType.Averaged)) }
                        )
                    }
                }

                is Tab.ResultsTab.Revisited -> {
                    div().bind(store.sub { it.rankingsState.revisited }) { revisited ->
                        revisitedRanking(revisited)
                    }
                }

                null -> {}
            }
        }
    }
}

fun Container.rankingTabs(
    store: ReduxStore<MainAppState, MainAppAction>,
    networkActions: NetworkActions
) {
    flexPanel(direction = FlexDirection.ROW, justify = JustifyContent.CENTER, spacing = 8) {
        padding = 16.px
        paddingBottom = 32.px
        button(text = "CEC 2022", style = ButtonStyle.OUTLINEPRIMARY)
            .onClickLaunch {
                store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Cec2022))
                store.dispatch { dispatch, _ ->
                    networkActions.loadCec2022Scores(dispatch)
                }
            }
        button(text = "Mean", style = ButtonStyle.OUTLINEPRIMARY)
            .onClickLaunch {
                store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Mean))
                store.dispatch { dispatch, _ ->
                    networkActions.loadMeanRanking(dispatch)
                }
            }
        button(text = "Median", style = ButtonStyle.OUTLINEPRIMARY)
            .onClickLaunch {
                store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Median))
                store.dispatch { dispatch, _ ->
                    networkActions.loadMedianRanking(dispatch)
                }
            }
        button(text = "ECDF", style = ButtonStyle.OUTLINEPRIMARY)
            .onClickLaunch {
                store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Ecdf))
                store.dispatch { dispatch, _ ->
                    networkActions.loadEcdfData(dispatch)
                }
            }
        button(text = "Friedman", style = ButtonStyle.OUTLINEPRIMARY)
            .onClickLaunch {
                store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Friedman))
                store.dispatch { dispatch, _ ->
                    networkActions.loadFriedmanScores(dispatch)
                }
            }
        button("Compare two algorithms", style = ButtonStyle.OUTLINEPRIMARY)
            .onClickLaunch {
                store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.PairTest))
                store.dispatch { dispatch, _ ->
                    networkActions.getAvailableBenchmarkData(dispatch)
                }
            }
        button(text = "Revisited Ranking", style = ButtonStyle.OUTLINEPRIMARY)
            .onClickLaunch {
                store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Revisited))
                store.dispatch { dispatch, _ ->
                    networkActions.loadRevisitedRanking(dispatch)
                }
            }
    }
}