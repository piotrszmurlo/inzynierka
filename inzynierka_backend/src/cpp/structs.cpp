#include <iostream>
#include <sstream>
#include <vector>


struct Trial {
    Trial(
        const std::string algorithmName,
        const int functionNumber,
        const int trialNumber,
        const double finalError,
        int numberOfEvaluations
    ):
        algorithmName(algorithmName),
        functionNumber(functionNumber),
        trialNumber(trialNumber),
        finalError(finalError),
        numberOfEvaluations(numberOfEvaluations) {}

    std::string algorithmName;
    int functionNumber;
    int trialNumber;
    double finalError;
    int numberOfEvaluations;

    bool operator==(const Trial &other) const {
        return finalError == other.finalError && numberOfEvaluations == other.numberOfEvaluations;
    }

    // std::sort will sort from worst trial to best trial
    bool operator<(const Trial &other) const {
        if (finalError != other.finalError) {
            return finalError > other.finalError;
        } else {
            return numberOfEvaluations > other.numberOfEvaluations;
        }
    }
};

struct AllErrors {
    AllErrors(
        const std::string algorithmName,
        const int functionNumber,
        const int dimension,
        std::vector<std::vector<double>>& errorsMatrix,
        std::vector<int> numberOfEvaluations
    ):
        algorithmName(algorithmName),
        functionNumber(functionNumber),
        dimension(dimension),
        errorsMatrix(errorsMatrix),
        numberOfEvaluations(numberOfEvaluations) {}

    std::string algorithmName;
    int functionNumber;
    int dimension;
    std::vector<std::vector<double>> errorsMatrix;
    std::vector<int> numberOfEvaluations;
};

struct StatisticsRankingEntry {
    StatisticsRankingEntry(
        const int dimension,
        const std::string algorithmName,
        const int functionNumber,
        const double mean,
        const double median,
        const double stdev,
        const double min,
        const double max,
        const int numberOfEvaluations
    ):
        dimension(dimension),
        algorithmName(algorithmName),
        functionNumber(functionNumber),
        mean(mean),
        median(median),
        stdev(stdev),
        max(max),
        min(min),
        numberOfEvaluations(numberOfEvaluations) {}

    int dimension;
    std::string algorithmName;
    int functionNumber;
    double mean;
    double median;
    double stdev;
    double max;
    double min;
    int numberOfEvaluations;
};

struct ScoreRankingEntry {
    ScoreRankingEntry(
        const int dimension,
        const std::string algorithmName,
        const double score
    ):
        dimension(dimension),
        algorithmName(algorithmName),
        score(score) {}

    int dimension;
    std::string algorithmName;
    double score;
};

struct RevisitedRankingEntry {
    RevisitedRankingEntry(
        const int dimension,
        const std::string algorithmName,
        const int functionNumber,
        const double successfulTrialsPercentage,
        const double thresholdsAchievedPercentage,
        const double budgetLeftPercentage,
        const double score
    ):
        dimension(dimension),
        algorithmName(algorithmName),
        functionNumber(functionNumber),
        successfulTrialsPercentage(successfulTrialsPercentage),
        thresholdsAchievedPercentage(thresholdsAchievedPercentage),
        budgetLeftPercentage(budgetLeftPercentage),
        score(score) {}

    int dimension;
    std::string algorithmName;
    int functionNumber;
    double successfulTrialsPercentage;
    double thresholdsAchievedPercentage;
    double budgetLeftPercentage;
    double score;
};

struct EcdfEntry {
    EcdfEntry(
        const int dimension,
        const std::string algorithmName,
        const int functionNumber,
        std::vector<double>& thresholdAchievedFractions,
        std::vector<double>& functionEvaluations
    ):
        dimension(dimension),
        algorithmName(algorithmName),
        functionNumber(functionNumber),
        thresholdAchievedFractions(thresholdAchievedFractions),
        functionEvaluations(functionEvaluations) {}

    int dimension;
    std::string algorithmName;
    int functionNumber;
    std::vector<double> thresholdAchievedFractions;
    std::vector<double> functionEvaluations;
};

using TrialsVector = std::vector<Trial>;
using FunctionTrialsVector = std::vector<TrialsVector>;
using AllErrorsVector = std::vector<AllErrors>;