#include <pybind11/pybind11.h>

#define STRINGIFY(x) #x
#define MACRO_STRINGIFY(x) STRINGIFY(x)
#define MIN_VALUE 1e-8
#include <iostream>
#include <regex>
#include <sstream>

using namespace std;

string parse_results(string input) {
    string adjusted_delimiter_input = regex_replace(input, regex("[^\\S\r\n]+|,"), " ");
    stringstream ss(adjusted_delimiter_input);
    stringstream result("");
    string word;
    double value;
    while (ss >> word) {
        value = stod(word);
        if (value < MIN_VALUE) {
            value = MIN_VALUE;
        }
        result << value << " ";
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
    )pbdoc";

    m.def("parse_results", &parse_results, R"pbdoc(
        Parse results file content
    )pbdoc");

#ifdef VERSION_INFO
    m.attr("__version__") = MACRO_STRINGIFY(VERSION_INFO);
#else
    m.attr("__version__") = "dev";
#endif
}
