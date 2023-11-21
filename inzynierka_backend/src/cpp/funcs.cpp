#include <iostream>
#include <regex>
#include <sstream>
#include <iomanip>
#include <stdexcept>
#include <vector>
#include <numeric>
#include <algorithm>
#include "utils.cpp"

#define STRINGIFY(x) #x
#define MACRO_STRINGIFY(x) STRINGIFY(x)

const int FINAL_ERROR_INDEX = 16;
const int EVALUATION_ROW_INDEX = 17;
const int MAX_COLUMN_COUNT = 30;
const int PRECISION = 8;

std::vector<ScoreRankingEntry> calculate_cec2022_scores(const int& numberOfTrials, const int& dimension, FunctionTrialsVector& input) {
    const int totalNumberOfFunctions = input.size();
    std::vector<ScoreRankingEntry> output;
    std::unordered_map<AlgorithmName, double> scores;
    for (auto& trial : input) {
        std::sort(trial.begin(), trial.end());

        // rank trials
        int equalValuesCount = 1;
        for (size_t j = 0; j < trial.size(); ++j) {
            if (j != trial.size() - 1 && trial[j] == trial[j + 1]) {
                ++equalValuesCount;
            } else {
                double score = (2 * j + 3 - equalValuesCount) / double(2);
                for (int k = 0; k < equalValuesCount; ++k) {
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
                std::nullopt,
                it.second - correctionTerm
            )
        );
    }
    return output;
}

std::unordered_map<AlgorithmName, double> calculate_average(const int& numberOfTrials, FunctionTrialsVector& input) {
    if (input.empty()) {
        throw std::invalid_argument("Input data is empty");
    }
    const int totalNumberOfFunctions = input.size();
    std::unordered_map<AlgorithmName, double> averages;
    for (auto& trials : input) {
        for (auto& trial : trials) {
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

std::vector<ScoreRankingEntry> calculate_friedman_scores(const int& numberOfTrials, BasicRankingInput& input) {
    std::vector<ScoreRankingEntry> output;
    std::unordered_map<Dimension, std::unordered_map<FunctionNumber, std::unordered_map<AlgorithmName, double>>> scores;
    std::unordered_map<Dimension, std::unordered_map<FunctionNumber, TrialsVector>> averagedTrials;
    
    for (size_t function = 0; function < input.size(); ++function) {
        for (auto& dimension : input[function]) {
            for (auto& algorithm : dimension.second) {
                std::string algorithmName = algorithm.first;
                TrialsVector trialsVector = algorithm.second;
                averagedTrials[dimension.first][function + 1].push_back(
                    Trial(
                        algorithmName,
                        function + 1,
                        0,
                        mean(trialsVector),
                        std::accumulate(trialsVector.begin(), trialsVector.end(), 0, [](int a, Trial& b) {
                            return a + b.numberOfEvaluations;
                        }) / double(trialsVector.size())
                    )
                );
            }
        }
    }

    for (auto& dimension : averagedTrials) {
        for (auto& functionNumber : dimension.second) {
            TrialsVector averageTrials = functionNumber.second;
            std::sort(averageTrials.rbegin(), averageTrials.rend());
            int equalValuesCount = 1;
            for (size_t j = 0; j < averageTrials.size(); ++j) {
                if (j != averageTrials.size() - 1 && averageTrials[j] == averageTrials[j + 1]) {
                    ++equalValuesCount;
                } else {
                    double rank = (2 * j + 3 - equalValuesCount) / double(2); // average rank for equal trials
                    for (int k = 0; k < equalValuesCount; ++k) {
                        scores[dimension.first][functionNumber.first][averageTrials[j - k].algorithmName] = rank;
                    }
                    equalValuesCount = 1;
                }
            }
        }
    }

    for (auto& dimension : scores) {
        for (auto& functionNumber : dimension.second) {
            for (auto& rankedAlgorithms : functionNumber.second) {
                output.push_back(
                    ScoreRankingEntry(
                        dimension.first,
                        rankedAlgorithms.first,
                        functionNumber.first,
                        rankedAlgorithms.second
                    )
                );
            }
        }
    }
    return output;
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

std::string parse_cec2022_results(std::string input, std::string fileName, int maxBudget) {
    std::string adjusted_delimiter_input = std::regex_replace(input, std::regex("[^\\S\r\n]+|,"), " "); // acceptable delimiters are one or more whitespace or comma
    std::stringstream ss(adjusted_delimiter_input);
    std::stringstream result("");
    result << std::setprecision(PRECISION);
    std::string word;
    double value;
    int columnCount = 0;
    int rowNumber = 1;
    int totalValuesCount = 0;
    std::vector<double> finalErrors;
    while (ss >> word) {
        ++totalValuesCount;
        try {
            value = strict_stod(word);
        }
        catch (std::invalid_argument) {
            throw std::invalid_argument("Unexpected character found in data: " + word + " in file: " + fileName);
        }
        if (value < MIN_VALUE) { 
            value = MIN_VALUE; 
        }
        if (rowNumber == FINAL_ERROR_INDEX) {
            finalErrors.push_back(value);
        } else if (rowNumber == EVALUATION_ROW_INDEX) {
            if ((finalErrors[columnCount] != MIN_VALUE) && (value != maxBudget)) {
                throw std::invalid_argument("Unexpected evaluation count in data: " + std::to_string(value) + " in file: " + fileName + " (If trial failed, max budget must be recorded)");
            }
            if ((finalErrors[columnCount] == MIN_VALUE) && (value == maxBudget)) {
                throw std::invalid_argument("Unexpected evaluation count in data: " + std::to_string(value) + " in file: " + fileName + " (If trial succeeded, record actual used budget, not max budget)");
            }
            if (value > maxBudget) {
                throw std::invalid_argument("Unexpected evaluation number in data (greater than max): " + std::to_string(value) + " in file: " + fileName);
            }
        }
        if (++columnCount == MAX_COLUMN_COUNT) {
            result << value << "\n";
            columnCount = 0;
            ++rowNumber;
        } else {
            result << value << " ";
        }
    }
    if (totalValuesCount != MAX_COLUMN_COUNT * (EVALUATION_ROW_INDEX)) {
        throw std::invalid_argument("Incorrect data format in file: " + fileName +". Provide a file with 17x30 matrix");
    }

    return result.str();
}

std::vector<StatisticsRankingEntry> calculate_statistics_entries(const BasicRankingInput& input) {
    std::vector<StatisticsRankingEntry> output = std::vector<StatisticsRankingEntry>();
    for (size_t function = 0; function < input.size(); ++function) {
        for (auto& dimension : input[function]) {
            for (auto& algorithm : dimension.second) {
                std::string algorithmName = algorithm.first;
                TrialsVector trialsVector = algorithm.second;
                std::sort(trialsVector.rbegin(), trialsVector.rend()); //sort from best to worst
                double min = trialsVector.front().finalError;
                double max = trialsVector.back().finalError;
                int functionNumber = function + 1;
                double median = median4sorted(trialsVector);
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
    for (size_t function = 0; function < input.size(); ++function) {
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

std::vector<double> getRecordedErrorSteps(int dimension, int maxBudget) {
    std::vector<double> output;
    for (int i = 0; i < 16; ++i) {
        output.push_back(floor(pow(10, (i / double(5)) - 3 ) * maxBudget));
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
            step = floor(step / errors.dimension);
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
