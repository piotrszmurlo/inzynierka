package com.inzynierka.ui.rankings

//fun Container.friedman(state: ScoreRankingState) {
//    withLoadingSpinner(state.isFetching) {
//        flexPanel(direction = FlexDirection.ROW) {
//            justifyContent = JustifyContent.CENTER
//            state.scores?.forEach {
//                rankingTable(
//                    headerNames = listOf("Rank", "Algorithm", "CEC 2022 score"),
//                    title = "Dimension = ${it.key}",
//                    scores = it.value
//                )
//            }
//            state.combinedScores?.let {
//                rankingTable(
//                    headerNames = listOf("Rank", "Algorithm", "combined CEC 2022 score"),
//                    title = "Combined ranking",
//                    scores = it
//                )
//            }
//        }
//    }
//}
