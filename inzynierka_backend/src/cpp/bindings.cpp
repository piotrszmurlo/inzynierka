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
PYBIND11_MAKE_OPAQUE(BasicRankingInput);

namespace py = pybind11;


PYBIND11_MODULE(python_extensions, m) {
    m.doc() = R"pbdoc(
        Pybind11 C++ extensions
        -----------------------

        .. currentmodule:: python_extensions

        .. autosummary::
           :toctree: _generate

           parse_results
           calculate_cec2022_scores
           calculate_average
           calculate_median
           calculate_friedman_scores
    )pbdoc";

    m.def("parse_results", &parse_results, R"pbdoc(
        Parse results file content
    )pbdoc");

    m.def("calculate_cec2022_scores", &calculate_cec2022_scores, R"pbdoc(
        Calculate score
    )pbdoc");


    m.def("calculate_average", &calculate_average, R"pbdoc(
        Calculate average
    )pbdoc");

    m.def("calculate_median", &calculate_median, R"pbdoc(
        Calculate median
    )pbdoc");

    m.def("calculate_friedman_scores", &calculate_friedman_scores, R"pbdoc(
        Calculate friedman scores
    )pbdoc");

    m.def("calculate_statistics_entries", &calculate_statistics_entries, R"pbdoc(
        calculate_statistics_entries
    )pbdoc");

    py::class_<Trial>(m, "Trial")
        .def(py::init<const std::string&, const int&, const int&, const double&, const int&>())
        .def("__repr__",
            [](const Trial &trial) {
                return "<extensions.Trial "
                + trial.algorithmName + " " 
                + std::to_string(trial.functionNumber) + " " 
                + std::to_string(trial.trialNumber) + " "
                + std::to_string(trial.finalError) + " "
                + std::to_string(trial.numberOfEvaluations) + " "
                 + ">";
            }
        );

    py::class_<StatisticsRankingEntry>(m, "StatisticsRankingEntry")
        .def(py::init<const int&, const std::string&, const int&, const double&, const double&, const double&, const double&, const double&, const int&>())
        .def_readwrite("dimension", &StatisticsRankingEntry::dimension)
        .def_readwrite("algorithm_name", &StatisticsRankingEntry::algorithmName)
        .def_readwrite("function_number", &StatisticsRankingEntry::functionNumber)
        .def_readwrite("mean", &StatisticsRankingEntry::mean)
        .def_readwrite("median", &StatisticsRankingEntry::median)
        .def_readwrite("stdev", &StatisticsRankingEntry::stdev)
        .def_readwrite("min", &StatisticsRankingEntry::min)
        .def_readwrite("max", &StatisticsRankingEntry::max)
        .def_readwrite("number_of_evaluations", &StatisticsRankingEntry::numberOfEvaluations)
        .def("__repr__",
            [](const StatisticsRankingEntry &entry) {
                return "<extensions.StatisticsRankingEntry "
                + entry.algorithmName + " " 
                + std::to_string(entry.dimension) + " " 
                + std::to_string(entry.functionNumber) + " "
                + std::to_string(entry.mean) + " "
                + std::to_string(entry.median) + " "
                + std::to_string(entry.stdev) + " "
                + std::to_string(entry.min) + " "
                + std::to_string(entry.max) + " "
                + std::to_string(entry.numberOfEvaluations) + " "
                + ">";
            }
        );

    py::bind_vector<FunctionTrialsVector>(m, "FunctionTrialsVector");
    py::bind_vector<TrialsVector>(m, "TrialsVector");
    py::bind_vector<BasicRankingInput>(m, "BasicRankingInput");

#ifdef VERSION_INFO
    m.attr("__version__") = MACRO_STRINGIFY(VERSION_INFO);
#else
    m.attr("__version__") = "dev";
#endif
}
