#include <iostream>
#include <regex>
#include <sstream>
#include <iomanip>
#include <stdexcept>
#include <vector>
#include <pybind11/pybind11.h>
#include <numeric>
#include <algorithm>
#define STRINGIFY(x) #x
#define MACRO_STRINGIFY(x) STRINGIFY(x)
#define MIN_VALUE 1e-8


struct FunctionAlgorithmTrial {
    FunctionAlgorithmTrial(const std::string &algorithmName, const int &functionNumber, const int &trialNumber, const double finalError, int numberOfEvaluations):
        algorithmName(algorithmName), functionNumber(functionNumber), trialNumber(trialNumber), finalError(finalError), numberOfEvaluations(numberOfEvaluations) {}
    std::string algorithmName;
    int functionNumber;
    int trialNumber;
    double finalError;
    int numberOfEvaluations;

    bool operator==(const FunctionAlgorithmTrial &other) const {
        return finalError == other.finalError && numberOfEvaluations == other.numberOfEvaluations;
    }

    // std::sort will sort from worst trial to best trial
    bool operator<(const FunctionAlgorithmTrial &other) const {
        if (finalError != other.finalError) {
            return finalError > other.finalError;
        } else {
            return numberOfEvaluations > other.numberOfEvaluations;
        }
    }
};

using TrialsVector = std::vector<FunctionAlgorithmTrial>;
using FunctionTrialsVector = std::vector<TrialsVector>;

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
    double sum = std::accumulate(input.begin(), input.end(), 0.0, [](double a, FunctionAlgorithmTrial b){
        return a + b.finalError;
    });
    return sum / input.size();
}

double stddev(const TrialsVector& input, double mean) {
    std::vector<double> differences(input.size());
    std::transform(input.begin(), input.end(), differences.begin(), [mean](FunctionAlgorithmTrial a) {
        return a.finalError - mean;
    });
    double sumOfSquares = std::inner_product(differences.begin(), differences.end(), differences.begin(), 0.0);
    return std::sqrt(sumOfSquares / input.size());
}

std::unordered_map<std::string, double> calculate_cec2022_scores(const int& numberOfTrials, FunctionTrialsVector& input) {
    const int totalNumberOfFunctions = input.size();
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
        it.second -= correctionTerm;
    }
    return scores;
}

std::unordered_map<std::string, double> calculate_friedman_scores(const int& numberOfTrials, FunctionTrialsVector& input) {
    const int totalNumberOfFunctions = input.size();
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
        it.second /= totalTrials;
    }
    return scores;
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

using BasicRankingInput = std::vector<std::map<int, std::map<std::string, TrialsVector>>>;
void calculate_statisticsV2(const BasicRankingInput& input, pybind11::list output) {
    for (size_t i = 0; i < input.size(); i++) {
        for (auto& dimension : input[i]) {
            for (auto& algorithm : dimension.second) {
                std::string algorithmName = algorithm.first;
                TrialsVector trialsVector = algorithm.second;
                std::sort(trialsVector.rbegin(), trialsVector.rend()); //sort from best to worst
                pybind11::dict obj = pybind11::dict();
                obj["min"] = trialsVector.front().finalError;
                obj["max"] = trialsVector.back().finalError;
                obj["algorithm_name"] = algorithmName;
                obj["dimension"] = dimension.first;
                obj["function_number"] = i + 1;
                int numberOfTrials = trialsVector.size();
                if (numberOfTrials % 2 != 0) {
                    obj["median"] = trialsVector[numberOfTrials/2].finalError;
                } else {
                    double median = ((trialsVector[numberOfTrials/2].finalError) + (trialsVector[(numberOfTrials/2) - 1].finalError))/2;
                    obj["median"] = median;
                }
                obj["min_fe_term"] = (*std::min_element(trialsVector.begin(), trialsVector.end(), [](const FunctionAlgorithmTrial& a, const FunctionAlgorithmTrial& b){
                    return a.numberOfEvaluations < b.numberOfEvaluations;
                })).numberOfEvaluations;
                double errorMean = mean(trialsVector);
                obj["mean"] = errorMean;
                obj["stddev"] = stddev(trialsVector, errorMean);
                output.append(obj);
            } 
        }
    }
}
