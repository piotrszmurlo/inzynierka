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
PYBIND11_MAKE_OPAQUE(AllErrorsVector);

namespace py = pybind11;


PYBIND11_MODULE(python_extensions, m) {
    m.doc() = R"pbdoc(
        Pybind11 C++ extensions
        -----------------------
        Contains functions for parsing CEC'22 benchmark results files and calculating rankings based on them.
        .. currentmodule:: python_extensions

        .. autosummary::
           :toctree: _generate

           parse_results
           calculate_cec2022_scores
           calculate_friedman_scores
           calculate_statistics_entries
           calculate_revisited_ranking
           calculate_ecdf_data
    )pbdoc";

    m.def("parse_cec2022_results", &parse_cec2022_results, R"pbdoc(
        Parse CEC'22 results file content. Returns data in adjusted format.
        Throws std::invalid_argument_exception if there are errors in data
    )pbdoc");

    m.def("calculate_cec2022_scores", &calculate_cec2022_scores, R"pbdoc(
        Calculate scores based on official CEC'22 benchmark evaluation criteria.
    )pbdoc");

    m.def("calculate_friedman_scores", &calculate_friedman_scores, R"pbdoc(
        Calculate friedman scores
    )pbdoc");

    m.def("calculate_statistics_entries", &calculate_statistics_entries, R"pbdoc(
        Calculates statistics ranking data
    )pbdoc");

    m.def("calculate_revisited_ranking", &calculate_revisited_ranking, R"pbdoc(
        Calculates revisited ranking data
    )pbdoc");


    m.def("calculate_ecdf_data", &calculate_ecdf_data, R"pbdoc(
        Returns data necessary for plotting ECDFs.
    )pbdoc");

    py::class_<Trial>(m, "Trial")
        .def(py::init<const std::string, const int, const int, const double, const int>())
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

        py::class_<AllErrors>(m, "AllErrors")
        .def(py::init<const std::string, const int, const int, std::vector<std::vector<double>>&, std::vector<int>>());

    py::class_<StatisticsRankingEntry>(m, "StatisticsRankingEntry")
        .def(py::init<const int, const std::string, const int, const double, const double, const double, const double, const double, const int>())
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

    py::class_<ScoreRankingEntry>(m, "ScoreRankingEntry")
        .def(py::init<const int, const std::string, std::optional<FunctionNumber>,const double>())
        .def_readwrite("dimension", &ScoreRankingEntry::dimension)
        .def_readwrite("algorithm_name", &ScoreRankingEntry::algorithmName)
        .def_readwrite("function_number", &ScoreRankingEntry::functionNumber)
        .def_readwrite("score", &ScoreRankingEntry::score);

    py::class_<RevisitedRankingEntry>(m, "RevisitedRankingEntry")
        .def(py::init<const int, const std::string, const int, const double, const double, const double, const double>())
        .def_readwrite("dimension", &RevisitedRankingEntry::dimension)
        .def_readwrite("algorithm_name", &RevisitedRankingEntry::algorithmName)
        .def_readwrite("functionNumber", &RevisitedRankingEntry::functionNumber)
        .def_readwrite("successfulTrialsPercentage", &RevisitedRankingEntry::successfulTrialsPercentage)
        .def_readwrite("thresholdsAchievedPercentage", &RevisitedRankingEntry::thresholdsAchievedPercentage)
        .def_readwrite("budgetLeftPercentage", &RevisitedRankingEntry::budgetLeftPercentage)
        .def_readwrite("score", &RevisitedRankingEntry::score);

    py::class_<EcdfEntry>(m, "EcdfEntry")
        .def(py::init<const int, const std::string, const int, std::vector<double>&, std::vector<double>&>())
        .def_readwrite("dimension", &EcdfEntry::dimension)
        .def_readwrite("algorithm_name", &EcdfEntry::algorithmName)
        .def_readwrite("functionNumber", &EcdfEntry::functionNumber)
        .def_readwrite("thresholds_achieved_fraction", &EcdfEntry::thresholdAchievedFractions)
        .def_readwrite("function_evaluations", &EcdfEntry::functionEvaluations);

    py::bind_vector<FunctionTrialsVector>(m, "FunctionTrialsVector");
    py::bind_vector<TrialsVector>(m, "TrialsVector");
    py::bind_vector<AllErrorsVector>(m, "AllErrorsVector");
    py::bind_vector<BasicRankingInput>(m, "BasicRankingInput");

#ifdef VERSION_INFO
    m.attr("__version__") = MACRO_STRINGIFY(VERSION_INFO);
#else
    m.attr("__version__") = "dev";
#endif
}
