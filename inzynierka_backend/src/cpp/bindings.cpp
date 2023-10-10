#include <pybind11/pybind11.h>
#include <pybind11/stl.h>
#include <pybind11/iostream.h>
#include <pybind11/stl_bind.h>
#include <iostream>
#include <regex>
#include <sstream>
#include <iomanip>
#include <stdexcept>
#include <vector>
#include "funcs.cpp"

PYBIND11_MAKE_OPAQUE(FunctionTrialsVector);
PYBIND11_MAKE_OPAQUE(TrialsVector);

namespace py = pybind11;


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
           calculate_median
           calculate_friedman_test_scores
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

    m.def("calculate_median", &calculate_median, R"pbdoc(
        Calculate median
    )pbdoc");

    m.def("calculate_friedman_test_scores", &calculate_friedman_test_scores, R"pbdoc(
        Calculate friedman test scores
    )pbdoc");

    py::class_<FunctionAlgorithmTrial>(m, "FunctionAlgorithmTrial")
        .def(py::init<const std::string&, const int&, const int&, const double&, const int&>())
        .def("__repr__",
            [](const FunctionAlgorithmTrial &trial) {
                return "<extensions.FunctionAlgorithmTrial "
                + trial.algorithmName + " " 
                + std::to_string(trial.functionNumber) + " " 
                + std::to_string(trial.trialNumber) + " "
                + std::to_string(trial.finalError) + " "
                + std::to_string(trial.numberOfEvaluations) + " "
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
