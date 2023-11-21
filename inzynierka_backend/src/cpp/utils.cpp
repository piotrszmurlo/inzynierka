#include <iostream>
#include <regex>
#include <sstream>
#include <iomanip>
#include <stdexcept>
#include <vector>
#include <numeric>
#include <algorithm>
#include <cmath>

#include "structs.cpp"

const double MIN_VALUE =  1e-8;

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
double median4sorted(const TrialsVector& sortedTrials) {
    int numberOfTrials = sortedTrials.size();
    if (numberOfTrials % 2 != 0) {
        return sortedTrials[numberOfTrials/2].finalError;
    } else {
        return ((sortedTrials[numberOfTrials/2].finalError) + (sortedTrials[(numberOfTrials/2) - 1].finalError))/2;
    }
}

double strict_stod(const std::string& s) {
    std::size_t pos;
    const auto result = std::stod(s, &pos);
    if (pos != s.size()) throw std::invalid_argument("Unexpected character found in data: " + s);
    return result;
}
