#include <iostream>
#include <regex>
#include <sstream>
#include <iomanip>
#include <stdexcept>
#include <vector>
#include <cstdint>
#define CATCH_CONFIG_MAIN  // This tells Catch to provide a main() - only do this in one cpp file
#include <catch2/catch.hpp>
#include "../../src/cpp/funcs.cpp"

double EPSILON = 10e-8;

TEST_CASE("median uneven vector size", "[median]") {
    std::vector<double> vector;
    vector.push_back(1.3);
    vector.push_back(1.1);
    vector.push_back(1.5);
    REQUIRE(median(vector) == 1.3 );
}

TEST_CASE("median even vector size", "[median]") {
    std::vector<double> vector;
    vector.push_back(1.3);
    vector.push_back(1.1);
    vector.push_back(1.5);
    vector.push_back(1.6);
    REQUIRE(median(vector) == 1.4 );
}

TEST_CASE("median one element vector", "[median]") {
    std::vector<double> vector;
    vector.push_back(1.1);
    REQUIRE(median(vector) == 1.1 );
}

TEST_CASE("median empty vector throws invalid argument exception", "[median]") {
    std::vector<double> vector;
    REQUIRE_THROWS_AS(median(vector), std::invalid_argument);
}

TEST_CASE("median uneven vector size function algorithm", "[median]") {
    FunctionTrialsVector vector;
    TrialsVector trialsVector1;
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 1, 1, 1.3, 200));
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 1, 2, 1.1, 200));
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 1, 3, 1.5, 200));

    TrialsVector trialsVector2;
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 1, 1, 10e-8, 200));
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 1, 2, 10e-8, 200));
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 1, 3, 10e-8, 200));
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 1, 4, 10e-5, 200));
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 1, 5, 10e-5, 200));

    vector.push_back(trialsVector1);
    vector.push_back(trialsVector2);

    std::unordered_map<std::string, double> expected;
    expected["alg1"] = 1.3;
    expected["alg2"] = 10e-8;

    REQUIRE(calculate_median(vector) == expected);
}

TEST_CASE("median even vector size function algorithm", "[median]") {
    FunctionTrialsVector vector;
    TrialsVector trialsVector1;
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 2, 1, 99.9, 200));
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 2, 2, 1.1, 300));
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 1, 3, 1.5, 400));
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 1, 4, 1.6, 500));

    TrialsVector trialsVector2;
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 2, 1, 10e-2, 200));
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 2, 2, 10e-2, 200));
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 1, 3, 10e-4, 200));
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 1, 4, 10e-4, 200));

    vector.push_back(trialsVector1);
    vector.push_back(trialsVector2);

    std::unordered_map<std::string, double> expected;
    expected["alg1"] = 1.55;
    expected["alg2"] = 0.0505;

    REQUIRE(calculate_median(vector) == expected);
}

TEST_CASE("median one element vector function algorithm", "[median]") {
    FunctionTrialsVector vector;
    TrialsVector trialsVector1;
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 2, 1, 99.9, 200));
    vector.push_back(trialsVector1);
    std::unordered_map<std::string, double> expected;
    expected["alg1"] = 99.9;
    REQUIRE(calculate_median(vector) == expected);
}

TEST_CASE("median empty vector function algorithm throws invalid argument exception", "[median]") {
    FunctionTrialsVector vector;
    REQUIRE_THROWS_AS(calculate_median(vector), std::invalid_argument);
}


TEST_CASE("average one element vector function algorithm", "[average]") {
    FunctionTrialsVector vector;
    TrialsVector trialsVector1;
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 1, 1, 13.27, 200));
    vector.push_back(trialsVector1);
    std::unordered_map<std::string, double> expected;
    expected["alg1"] = 13.27;
    REQUIRE(calculate_average(1, 1, vector) == expected);
}

TEST_CASE("average many element vector function algorithm", "[average]") {
    FunctionTrialsVector vector;
    TrialsVector trialsVector1;
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 1, 1, 22.22, 111));
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 1, 2, 11.11, 222));
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 1, 3, 33.33, 333));
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 2, 1, 11.11, 111));
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 2, 2, 12.22, 222));
    trialsVector1.push_back(FunctionAlgorithmTrial("alg1", 2, 3, 10e-8, 333));
    vector.push_back(trialsVector1);

    TrialsVector trialsVector2;
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 1, 1, 1.1, 111));
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 1, 2, 10e-8, 222));
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 1, 3, 10e-8, 333));
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 2, 1, 10e-8, 111));
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 2, 2, 10e-8, 222));
    trialsVector2.push_back(FunctionAlgorithmTrial("alg2", 2, 3, 10e-8, 333));
    vector.push_back(trialsVector2);

    std::unordered_map<std::string, double> expected;
    expected["alg1"] = 14.99833335;
    expected["alg2"] = 0.18333341666;
    REQUIRE(fabs(calculate_average(2, 3, vector)["alg1"] - expected["alg1"]) < EPSILON);
    REQUIRE(fabs(calculate_average(2, 3, vector)["alg2"] - expected["alg2"]) < EPSILON);
    REQUIRE(expected.size() == 2);
}


TEST_CASE("average empty vector function algorithm throws invalid argument exception", "[average]") {
    FunctionTrialsVector vector;
    REQUIRE_THROWS_AS(calculate_average(1, 1, vector), std::invalid_argument);
    REQUIRE_THROWS_AS(calculate_average(0, 0, vector), std::invalid_argument);
}

TEST_CASE("strict_stod double input", "[utils]") {
    std::string input = "1.5";
    REQUIRE(strict_stod(input) == 1.5 );
}

TEST_CASE("strict_stod scientific notation input", "[utils]") {
    std::string input = "5.4204580e+00";
    REQUIRE(strict_stod(input) == 5.4204580 );
}

TEST_CASE("strict_stod scientific notation input 2", "[utils]") {
    std::string input = "8.1118969e-09";
    REQUIRE(strict_stod(input) == 0.0000000081118969 );
}

TEST_CASE("strict_stod scientific notation input 3", "[utils]") {
    std::string input = "4.7216000e+05";
    REQUIRE(strict_stod(input) == 472160.0 );
}

