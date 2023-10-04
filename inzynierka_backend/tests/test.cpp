#include <iostream>
#include <regex>
#include <sstream>
#include <iomanip>
#include <stdexcept>
#include <vector>
#include <cstdint>
#define CATCH_CONFIG_MAIN  // This tells Catch to provide a main() - only do this in one cpp file
#include <catch2/catch.hpp>
#include "../src/funcs.cpp"

TEST_CASE("median_calculate uneven vector size", "[median]") {
    std::vector<double> vector;
    vector.push_back(1.3);
    vector.push_back(1.1);
    vector.push_back(1.5);
    REQUIRE(median(vector) == 1.3 );
}

TEST_CASE("median_calculate even vector size", "[median]") {
    std::vector<double> vector;
    vector.push_back(1.3);
    vector.push_back(1.1);
    vector.push_back(1.5);
    vector.push_back(1.6);
    REQUIRE(median(vector) == 1.4 );
}

TEST_CASE("median_calculate one element vector", "[median]") {
    std::vector<double> vector;
    vector.push_back(1.1);
    REQUIRE(median(vector) == 1.1 );
}

TEST_CASE("median_calculate empty vector", "[median]") {
    std::vector<double> vector;
    REQUIRE_THROWS_AS(median(vector), std::invalid_argument);
}
