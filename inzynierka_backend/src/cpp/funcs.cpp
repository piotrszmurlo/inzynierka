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
    if (pos != s.size()) throw std::invalid_argument("Unexpected character found in data: " + s);
    return result;
}

const int FINAL_ERROR_INDEX = 16;
const int EVALUATION_ROW_INDEX = 17;
const int MAX_COLUM_COUNT = 30;

std::string parse_results(std::string input,std::string fileName, int maxBudget) {
    std::string adjusted_delimiter_input = std::regex_replace(input, std::regex("[^\\S\r\n]+|,"), " "); // acceptable delimiters are one or more whitespace or comma
    std::stringstream ss(adjusted_delimiter_input);
    std::stringstream result("");
    result << std::setprecision(8);
    std::string word;
    double value;
    int columnCount = 0;
    int rowNumber = 1;
    std::vector<double> finalErrors;
    while (ss >> word) {
        try {
            value = strict_stod(word);
        }
        catch(std::invalid_argument) {
            throw std::invalid_argument("Unexpected character found in data: " + word + " in file: " + fileName);
        }
        if (value < MIN_VALUE) { value = MIN_VALUE; }
        if (rowNumber == FINAL_ERROR_INDEX) {
            finalErrors.push_back(value);
        } else if (rowNumber == EVALUATION_ROW_INDEX) {
            if ((finalErrors[columnCount] != MIN_VALUE) && (value != maxBudget)) throw std::invalid_argument("Unexpected evaluation count in data: " + std::to_string(value) + " in file: " + fileName + " (If trial failed, max budget must be recorded)");
            if ((finalErrors[columnCount] == MIN_VALUE) && (value == maxBudget)) throw std::invalid_argument("Unexpected evaluation count in data: " + std::to_string(value) + " in file: " + fileName + " (If trial succeeded, record actual used budget, not max budget)");
            if (value > maxBudget) throw std::invalid_argument("Unexpected evaluation number in data (greater than max): " + std::to_string(value) + " in file: " + fileName);
        }
        if (++columnCount == MAX_COLUM_COUNT) {
            result << value << "\n";
            columnCount = 0;
            ++rowNumber;
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


double successfulTrialsPercentage(const TrialsVector& input) {
    int successfulTrialsCount = 0;
    for (auto& trial: input) {
        if (trial.finalError == MIN_VALUE) {
            ++successfulTrialsCount;
        }
    }
    return successfulTrialsCount / double(input.size());
}

double thresholdsAchievedPercentage(const TrialsVector& input, const std::vector<double>& thresholds) {
    int thresholdAchieved = 0;
    for (auto& trial : input) {
        for(auto& threshold : thresholds){
            if (trial.finalError <= threshold) {
                ++thresholdAchieved;
            }
        }
    }
    return double(thresholdAchieved) / (thresholds.size() * input.size());
}

double budgetLeftPercentage(const TrialsVector& input, const double maxBudget) {
    int budgetLeftSum = 0;
    for (auto& trial : input) {
        budgetLeftSum += (maxBudget - trial.numberOfEvaluations);
    }
    return double(budgetLeftSum) / (maxBudget * input.size());
}


std::vector<RevisitedRankingEntry> calculate_revisited_ranking(const BasicRankingInput& input, const std::vector<double> thresholds, std::unordered_map<int, int> maxBudgets, double weight) {
    double weightSquared = pow(weight, 2);
    std::vector<RevisitedRankingEntry> output = std::vector<RevisitedRankingEntry>();
    for (size_t function = 0; function < input.size(); function++) {
        for (auto& dimension : input[function]) {
            for (auto& algorithm : dimension.second) {
                std::string algorithmName = algorithm.first;
                TrialsVector trialsVector = algorithm.second;
                int functionNumber = function + 1;
                double successfulTrials = successfulTrialsPercentage(trialsVector);
                double thresholdsAchieved = thresholdsAchievedPercentage(trialsVector, thresholds);
                double budgetLeft = budgetLeftPercentage(trialsVector, maxBudgets[dimension.first]);
                double score = successfulTrials + weight * thresholdsAchieved + budgetLeft * weightSquared;
                output.push_back(
                    RevisitedRankingEntry(
                        dimension.first,
                        algorithmName,
                        functionNumber,
                        successfulTrials,
                        thresholdsAchieved,
                        budgetLeft,
                        score
                    )
                );
            }
        }
    }
    return output;
}

double thresholdsAchievedFraction(const std::vector<double>& input, const std::vector<double>& thresholds) {
    int thresholdAchieved = 0;
    for (auto& error : input) {
        for(auto& threshold : thresholds){
            if (error <= threshold) {
                ++thresholdAchieved;
            }
        }
    }
    return double(thresholdAchieved) / (thresholds.size() * input.size());
}

std::vector<double>  getRecordedErrorSteps(int dimension, int maxBudget) {
    std::vector<double> output;
    for (int i = 0; i < 16; ++i) {
        output.push_back(pow(10, (i / double(5)) - 3 ) * maxBudget);
    }
    return output;
}

std::vector<EcdfEntry> calculate_ecdf_data(const AllErrorsVector& input, const std::vector<double>& thresholds, std::unordered_map<int, int> maxBudgets) {
    std::vector<EcdfEntry> output = std::vector<EcdfEntry>();
    for (auto& errors : input) {
        std::vector<double> fractions;
        for (auto& row : errors.errorsMatrix) {
            fractions.push_back(thresholdsAchievedFraction(row, thresholds));
        }
        double mean = 0;
        for (auto& evalNumber : errors.numberOfEvaluations) {
            mean += evalNumber;
        }
        mean = mean / double(errors.numberOfEvaluations.size());
        std::vector<double> steps = getRecordedErrorSteps(errors.dimension, maxBudgets[errors.dimension]);
        for (auto& step : steps) {
            step = log10(step/double(errors.dimension));
        }

        output.push_back(
            EcdfEntry(
                errors.dimension,
                errors.algorithmName,
                errors.functionNumber,
                fractions,
                steps
            )
        );
    }
    return output;
}

