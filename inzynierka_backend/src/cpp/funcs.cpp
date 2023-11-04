#include <iostream>
#include <regex>
#include <sstream>
#include <iomanip>
#include <stdexcept>
#include <vector>
#include <numeric>
#include <algorithm>
#include "structs.cpp"
#define STRINGIFY(x) #x
#define MACRO_STRINGIFY(x) STRINGIFY(x)
#define MIN_VALUE 1e-8


double median(std::vector<double> &input) {
  if (input.empty()) {
    throw std::invalid_argument("Input data is empty");
  }
  auto n = input.size() / 2;
  std::nth_element(input.begin(), input.begin() + n, input.end());
  auto median = input[n];
  if (!(input.size() % 2)) {
    auto max = max_element(input.begin(), input.begin() + n);
    median = (*max + median) * 0.5;
  }
  return median;
}


double mean(const TrialsVector& input) {
    double sum = std::accumulate(input.begin(), input.end(), 0.0, [](double a, Trial b){
        return a + b.finalError;
    });
    return sum / input.size();
}

double stddev(const TrialsVector& input, double mean) {
    std::vector<double> differences(input.size());
    std::transform(input.begin(), input.end(), differences.begin(), [mean](Trial a) {
        return a.finalError - mean;
    });
    double sumOfSquares = std::inner_product(differences.begin(), differences.end(), differences.begin(), 0.0);
    return std::sqrt(sumOfSquares / input.size());
}

double roundToMinValue(const double& input) {
    if (input < MIN_VALUE) {
        return MIN_VALUE; 
    } else {
        return input;
    }
}


//trials must be sorted from best to worst
double _median(const TrialsVector& sortedTrials) {
    int numberOfTrials = sortedTrials.size();
    if (numberOfTrials % 2 != 0) {
        return sortedTrials[numberOfTrials/2].finalError;
    } else {
        return ((sortedTrials[numberOfTrials/2].finalError) + (sortedTrials[(numberOfTrials/2) - 1].finalError))/2;
    }
}

std::vector<ScoreRankingEntry> calculate_cec2022_scores(const int& numberOfTrials, const int& dimension, FunctionTrialsVector& input) {
    const int totalNumberOfFunctions = input.size();
    std::vector<ScoreRankingEntry> output;
    std::unordered_map<std::string, double> scores;
    for (auto& trial : input) {
        std::sort(trial.begin(), trial.end());

        // rank trials
        int equalValuesCount = 1;
        for (auto j = 0; j < trial.size(); j++) {
            if (j != trial.size() - 1 && trial[j] == trial[j + 1]) {
                ++equalValuesCount;
            } else {
                double score = (2 * j + 3 - equalValuesCount) / double(2);
                for (int k = 0; k < equalValuesCount; k++) {
                    if (scores.find(trial[j - k].algorithmName) != scores.end()) {
                        scores[trial[j - k].algorithmName] += score;
                    } else {
                        scores[trial[j - k].algorithmName] = score;
                    }
                }
                equalValuesCount = 1;
            }
        }
    }

    // apply correction term n(n-1)/2 * number of functions
    int correctionTerm = numberOfTrials * (numberOfTrials - 1) / 2 * totalNumberOfFunctions;
    for (auto& it: scores) {
        output.push_back(
            ScoreRankingEntry(
                dimension,
                it.first,
                it.second - correctionTerm
            )
        );
    }
    return output;
}

std::vector<ScoreRankingEntry> calculate_friedman_scores(const int& numberOfTrials, const int& dimension, FunctionTrialsVector& input) {
    const int totalNumberOfFunctions = input.size();
    std::vector<ScoreRankingEntry> output;
    std::unordered_map<std::string, double> scores;
        for (auto& trial : input) {
        std::sort(trial.rbegin(), trial.rend());

        // rank trials
        int equalValuesCount = 1;
        for (auto j = 0; j < trial.size(); j++) {
            if (j != trial.size() - 1 && trial[j] == trial[j + 1]) {
                ++equalValuesCount;
            } else {
                double score = (2 * j + 3 - equalValuesCount) / double(2); // average rank for equal trials
                for (int k = 0; k < equalValuesCount; k++) {
                    if (scores.find(trial[j - k].algorithmName) != scores.end()) {
                        scores[trial[j - k].algorithmName] += score;
                    } else {
                        scores[trial[j - k].algorithmName] = score;
                    }
                }
                equalValuesCount = 1;
            }
        }
    }

    int totalTrials = totalNumberOfFunctions * numberOfTrials;
    for (auto& it: scores) {
        output.push_back(
            ScoreRankingEntry(
                dimension,
                it.first,
                it.second / totalTrials
            )
        );
    }
    return output;
}

std::unordered_map<std::string, double> calculate_average(const int& numberOfTrials, FunctionTrialsVector& input) {
    if (input.empty()) {
        throw std::invalid_argument("Input data is empty");
    }
    const int totalNumberOfFunctions = input.size();
    std::unordered_map<std::string, double> averages;
    for (auto trials : input) {
        for (auto trial : trials) {
            if (averages.find(trial.algorithmName) != averages.end()) {
                averages[trial.algorithmName] += trial.finalError;
            } else {
                averages[trial.algorithmName] = trial.finalError;
            }
        }
    }

    int totalTrials = totalNumberOfFunctions * numberOfTrials;
    for (auto& it: averages) {
        it.second /= totalTrials;
    }
    return averages;
}

std::unordered_map<std::string, double> calculate_median(const FunctionTrialsVector& input) {
    if (input.empty()) {
        throw std::invalid_argument("Input data is empty");
    }
    std::unordered_map<std::string, double> medians;
    std::unordered_map<std::string, std::vector<double>> algorithmTrials;
    for (const auto& trials : input) {
        for (const auto& trial : trials) {
            algorithmTrials[trial.algorithmName].push_back(trial.finalError);
        }
    }
    for (auto& results : algorithmTrials) {
        medians[results.first] = median(results.second);
    }
    return medians;
}


double strict_stod(const std::string& s) {
    std::size_t pos;
    const auto result = std::stod(s, &pos);
    if (pos != s.size()) throw std::invalid_argument("Unexpected character found in data");
    return result;
}

std::string parse_results(std::string input) {
    std::string adjusted_delimiter_input = std::regex_replace(input, std::regex("[^\\S\r\n]+|,"), " "); // acceptable delimiters are one or more whitespace or comma
    std::stringstream ss(adjusted_delimiter_input);
    std::stringstream result("");
    result << std::setprecision(8);
    std::string word;
    double value;
    int rowcount = 0;
    while (ss >> word) {
        value = strict_stod(word);
        if (value < MIN_VALUE) { value = MIN_VALUE; }
        if (++rowcount == 30) {
            result << value << "\n";
            rowcount = 0;
        } else {
            result << value << " ";
        }
    }

    return result.str();
}

using BasicRankingInput = std::vector<std::unordered_map<int, std::unordered_map<std::string, TrialsVector>>>;

std::vector<StatisticsRankingEntry> calculate_statistics_entries(const BasicRankingInput& input) {
    std::vector<StatisticsRankingEntry> output = std::vector<StatisticsRankingEntry>();
    for (size_t function = 0; function < input.size(); function++) {
        for (auto& dimension : input[function]) {
            for (auto& algorithm : dimension.second) {
                std::string algorithmName = algorithm.first;
                TrialsVector trialsVector = algorithm.second;
                std::sort(trialsVector.rbegin(), trialsVector.rend()); //sort from best to worst
                double min = trialsVector.front().finalError;
                double max = trialsVector.back().finalError;
                int functionNumber = function + 1;
                double median = _median(trialsVector);
                double meanError = roundToMinValue(mean(trialsVector));
                double stdev = roundToMinValue(stddev(trialsVector, meanError));
                int numberOfEvaluations = (*std::min_element(trialsVector.begin(), trialsVector.end(), [](const Trial& a, const Trial& b) {
                    return a.numberOfEvaluations < b.numberOfEvaluations;
                })).numberOfEvaluations;

                output.push_back(
                    StatisticsRankingEntry(
                        dimension.first,
                        algorithmName,
                        functionNumber,
                        meanError,
                        median,
                        stdev,
                        min,
                        max,    
                        numberOfEvaluations
                    )
            );
            } 
        }
    }
    return output;
}

std::vector<StatisticsRankingEntry> calculate_revisited_ranking(const BasicRankingInput& input) {

}
