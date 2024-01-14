package com.inzynierka.data.service

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.data.models.toDomain
import com.inzynierka.data.parsedRemoteExceptionMessage
import com.inzynierka.data.repository.IRankingsRepository
import com.inzynierka.domain.models.PairTestEntry
import com.inzynierka.domain.models.RevisitedRankingEntry
import com.inzynierka.domain.models.ScoreRankingEntry
import com.inzynierka.domain.models.StatisticsRankingEntry
import com.inzynierka.domain.service.IRankingsService
import com.inzynierka.model.EcdfData

class RankingsService(val rankingsRepository: IRankingsRepository) : IRankingsService {
    override suspend fun getCec2022Scores(benchmarkName: String): Result<List<ScoreRankingEntry>> {
        return try {
            val result = rankingsRepository.getCec2022Scores(benchmarkName).map { it.toDomain() }
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getFriedmanScores(benchmarkName: String): Result<List<ScoreRankingEntry>> {
        return try {
            Result.Success(rankingsRepository.getFriedmanScores(benchmarkName).map { it.toDomain() })
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getStatisticsRankingEntries(benchmarkName: String): Result<List<StatisticsRankingEntry>> {
        return try {
            val result = rankingsRepository.getStatisticsEntries(benchmarkName).map { it.toDomain() }
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getRevisitedRankingEntries(benchmarkName: String): Result<List<RevisitedRankingEntry>> {
        return try {
            Result.Success(rankingsRepository.getRevisitedEntries(benchmarkName).map { it.toDomain() })
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getEcdfData(benchmarkName: String): Result<List<EcdfData>> {
        return try {
            Result.Success(rankingsRepository.getEcdfData(benchmarkName).map { it.toDomain() })
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }


    override suspend fun getPairTest(
        algorithm1: String,
        algorithm2: String,
        dimension: Int,
        benchmarkName: String
    ): Result<List<PairTestEntry>> {
        return try {
            val result =
                rankingsRepository.getPairTest(algorithm1, algorithm2, dimension, benchmarkName).map { it.toDomain() }
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }
}