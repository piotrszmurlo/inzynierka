#include <pybind11/pybind11.h>
#include <pybind11/stl.h>
#include <pybind11/iostream.h>
#include <pybind11/stl_bind.h>
#include <iostream>
#include <regex>
#include <sstream>
#include <iomanip>
#include <vector>

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
};

bool operator==(const FunctionAlgorithmTrial &a, const FunctionAlgorithmTrial &b) {
    return a.finalError == b.finalError && a.numberOfEvaluations == b.numberOfEvaluations;
}

bool operator<(const FunctionAlgorithmTrial &a, const FunctionAlgorithmTrial &b) {
    if (a.finalError != b.finalError) {
        return a.finalError < b.finalError;
    } else {
        return a.numberOfEvaluations > b.numberOfEvaluations;
    }
}
using TrialsVector = std::vector<FunctionAlgorithmTrial>;
using FunctionTrialsVector = std::vector<TrialsVector>;
PYBIND11_MAKE_OPAQUE(FunctionTrialsVector);
PYBIND11_MAKE_OPAQUE(TrialsVector);

using namespace std;
namespace py = pybind11;

unordered_map<string, float> calculate_cec2022_score(const int& totalNumberOfFunctions, const int& numberOfTrials, FunctionTrialsVector& input) {
    unordered_map<string, float> scores;
    for (auto& trial : input) {
        sort(trial.begin(), trial.end());

        // rank trials
        int equalValuesCount = 1;
        for (auto j = 0; j < trial.size(); j++) {
            if (j != trial.size() - 1 && trial[j] == trial[j + 1]) { // 60?
                ++equalValuesCount;
            } else {
                for (int k = 0; k < equalValuesCount; k++) {
                    if (scores.find(trial[j + k].algorithmName) != scores.end()) {
                        scores[trial[j + k].algorithmName] += j / float(equalValuesCount);
                    } else {
                        scores[trial[j + k].algorithmName] = j / float(equalValuesCount);
                    }
                    equalValuesCount = 1;
                }
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

unordered_map<string, float> calculate_average(const int& totalNumberOfFunctions, const int& numberOfTrials, FunctionTrialsVector& input) {
    unordered_map<string, float> scores;
    for (auto trials : input) {
        for (auto trial : trials) {
            if (scores.find(trial.algorithmName) != scores.end()) {
                scores[trial.algorithmName] += trial.finalError;
            } else {
                scores[trial.algorithmName] = trial.finalError;
            }
        }
    }

    int totalTrials = totalNumberOfFunctions*numberOfTrials;
    for (auto& it: scores) {
        it.second /= totalTrials;
    }
    return scores;
}

string parse_results(string input) {
    string adjusted_delimiter_input = regex_replace(input, regex("[^\\S\r\n]+|,"), " "); // acceptable delimiters are one or more whitespace or comma
    stringstream ss(adjusted_delimiter_input);
    stringstream result("");
    result << setprecision(8);
    string word;
    double value;
    int rowcount = 0;
    while (ss >> word) {

        value = stod(word);
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

PYBIND11_MODULE(python_extensions, m) {
    m.doc() = R"pbdoc(
        Pybind11 C++ extensions
        -----------------------

        .. currentmodule:: python_extensions

        .. autosummary::
           :toctree: _generate

           parse_results
           calculate_cec2022_score
           calculate_average
    )pbdoc";

    m.def("parse_results", &parse_results, R"pbdoc(
        Parse results file content
    )pbdoc");

    m.def("calculate_cec2022_score", &calculate_cec2022_score, R"pbdoc(
        Calculate score
    )pbdoc");


    m.def("calculate_average", &calculate_average, R"pbdoc(
        Calculate average
    )pbdoc");

    py::class_<FunctionAlgorithmTrial>(m, "FunctionAlgorithmTrial")
        .def(py::init<const string&, const int&, const int&, const double&, const int&>())
        .def("__repr__",
            [](const FunctionAlgorithmTrial &trial) {
                return "<extensions.FunctionAlgorithmTrial "
                + trial.algorithmName + " " 
                + to_string(trial.functionNumber) + " " 
                + to_string(trial.trialNumber) + " "
                + to_string(trial.finalError) + " "
                + to_string(trial.numberOfEvaluations) + " "
                 + ">";
            }
        );

    py::bind_vector<FunctionTrialsVector>(m, "FunctionTrialsVector");
    py::bind_vector<TrialsVector>(m, "TrialsVector");

#ifdef VERSION_INFO
    m.attr("__version__") = MACRO_STRINGIFY(VERSION_INFO);
#else
    m.attr("__version__") = "dev";
#endif
}
