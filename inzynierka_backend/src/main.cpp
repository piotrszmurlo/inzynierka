#include <pybind11/pybind11.h>
#include <pybind11/eigen.h>

#define STRINGIFY(x) #x
#define MACRO_STRINGIFY(x) STRINGIFY(x)
#define MIN_VALUE 1e-8
#include <iostream>
#include <regex>
#include <sstream>
#include <iomanip>
#include <vector>

using namespace std;

struct FunctionAlgorithmTrial {
    FunctionAlgorithmTrial(const string &algorithmName, const int &functionNumber, const int &trialNumber, const double finalError, int numberOfEvaluations):
        algorithmName(algorithmName), functionNumber(functionNumber), trialNumber(trialNumber), finalError(finalError), numberOfEvaluations(numberOfEvaluations) {}
    string algorithmName;
    int functionNumber;
    int trialNumber;
    double finalError;
    int numberOfEvaluations;
};

double calculate_average(string input) {
    Eigen::MatrixXd m(2,2);
    return 2.33;
}

Eigen::MatrixXd scale_by_2(Eigen::MatrixXd v) {
    v *= 2;
    return v;
}

int calculate_cec2022_score(vector<FunctionAlgorithmTrial> input) {
    
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


namespace py = pybind11;

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

    m.def("calculate_average", &calculate_average, R"pbdoc(
        calculate average from file content
    )pbdoc");

    m.def("scale_by_2", &scale_by_2, R"pbdoc(
        calculate average from file dcontent
    )pbdoc");
    
    py::class_<FunctionAlgorithmTrial>(m, "FunctionAlgorithmTrial")
        .def(py::init<const string&, const int&, const double&, const int&>());

#ifdef VERSION_INFO
    m.attr("__version__") = MACRO_STRINGIFY(VERSION_INFO);
#else
    m.attr("__version__") = "dev";
#endif
}
