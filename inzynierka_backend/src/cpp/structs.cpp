#include <iostream>
#include <sstream>
#include <vector>


struct Trial {
    Trial(const std::string &algorithmName, const int &functionNumber, const int &trialNumber, const double finalError, int numberOfEvaluations):
        algorithmName(algorithmName), functionNumber(functionNumber), trialNumber(trialNumber), finalError(finalError), numberOfEvaluations(numberOfEvaluations) {}
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

struct StatisticsRankingEntry {
    StatisticsRankingEntry(
        const int& dimension,
        const std::string& algorithmName,
        const int& functionNumber,
        const double& mean,
        const double& median,
        const double& stdev,
        const double& min,
        const double& max,
        const int& numberOfEvaluations
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
        const int& dimension,
        const std::string& algorithmName,
        const double& score
    ):
        dimension(dimension),
        algorithmName(algorithmName),
        score(score) {}

    int dimension;
    std::string algorithmName;
    double score;
};

using TrialsVector = std::vector<Trial>;
using FunctionTrialsVector = std::vector<TrialsVector>;