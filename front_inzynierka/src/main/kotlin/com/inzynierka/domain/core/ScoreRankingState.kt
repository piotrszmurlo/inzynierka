package com.inzynierka.domain.core

import com.inzynierka.domain.models.ScoreRankingEntry

data class ScoreRankingState(
    val scores: Map<Dimension, List<ScoreRankingEntry>>? = null,
    val combinedScores: List<ScoreRankingEntry>? = null,
    val isFetching: Boolean = false
)

fun List<ScoreRankingEntry>.createRankings(
    comparator: Comparator<ScoreRankingEntry>
): ScoreRankingState {
    val scores = this.groupBy { it.dimension }
        .mapValues { (_, scores) ->
            scores.sortedWith(comparator)
                .mapIndexed { index, score ->
                    score.copy(rank = index + 1)
                }
        }

    val combinedScores = this.groupBy { it.algorithmName }
        .mapValues { (_, scores) ->
            scores.reduce { acc, next ->
                acc.copy(score = acc.score + next.score)
            }
        }.values
        .sortedWith(comparator)
        .mapIndexed { index, score ->
            score.copy(rank = index + 1)
        }
    return ScoreRankingState(scores = scores, combinedScores = combinedScores, isFetching = false)
}