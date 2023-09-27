#include <pybind11/pybind11.h>
#include <pybind11/stl.h>
#include <pybind11/iostream.h>
#include <iostream>
#include <regex>
#include <sstream>
#include <iomanip>
#include <vector>

#define STRINGIFY(x) #x
#define MACRO_STRINGIFY(x) STRINGIFY(x)
#define MIN_VALUE 1e-8
#define CORRECTION_TERM 


using namespace std;
namespace py = pybind11;

struct FunctionAlgorithmTrial {
    FunctionAlgorithmTrial(const string &algorithmName, const int &functionNumber, const int &trialNumber, const double finalError, int numberOfEvaluations):
        algorithmName(algorithmName), functionNumber(functionNumber), trialNumber(trialNumber), finalError(finalError), numberOfEvaluations(numberOfEvaluations) {}
    string algorithmName;
    int functionNumber;
    int trialNumber;
    double finalError;
    int numberOfEvaluations;
    int rank;
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


unordered_map<string, float> calculate_cec2022_score(vector<vector<FunctionAlgorithmTrial>> input, const int& totalNumberOfFunctions, const int& numberOfTrials) {
    unordered_map<string, float> scores;
    for (auto trial : input) {
        sort(trial.begin(), trial.end());

        // rank trials
        int equalValuesCount = 1;
        for (auto j = 0; j < trial.size(); j++) {
            if (trial[j] == trial[j + 1]) { // 60?
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
        if (value < MIN_VALUE) {
            value = MIN_VALUE;
        }
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
           calculate_average
    )pbdoc";

    m.def("parse_results", &parse_results, R"pbdoc(
        Parse results file content
    )pbdoc");

    m.def("calculate_cec2022_score", &calculate_cec2022_score, R"pbdoc(
        Calculate score
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

#ifdef VERSION_INFO
    m.attr("__version__") = MACRO_STRINGIFY(VERSION_INFO);
#else
    m.attr("__version__") = "dev";
#endif
}
