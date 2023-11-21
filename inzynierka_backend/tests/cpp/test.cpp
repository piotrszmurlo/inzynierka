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

TEST_CASE("median uneven vector size", "[median]") {
    std::vector<double> vector;
    vector.push_back(1.3);
    vector.push_back(1.1);
    vector.push_back(1.5);
    REQUIRE(median(vector) == 1.3);
}

TEST_CASE("median even vector size", "[median]") {
    std::vector<double> vector;
    vector.push_back(1.3);
    vector.push_back(1.1);
    vector.push_back(1.5);
    vector.push_back(1.6);
    REQUIRE(median(vector) == 1.4);
}

TEST_CASE("median one element vector", "[median]") {
    std::vector<double> vector;
    vector.push_back(1.1);
    REQUIRE(median(vector) == 1.1);
}

TEST_CASE("median empty vector throws invalid argument exception", "[median]") {
    std::vector<double> vector;
    REQUIRE_THROWS_AS(median(vector), std::invalid_argument);
}

TEST_CASE("median uneven vector size function algorithm", "[median]") {
    FunctionTrialsVector vector;
    TrialsVector trialsVector1;
    trialsVector1.push_back(Trial("alg1", 1, 1, 1.3, 200));
    trialsVector1.push_back(Trial("alg1", 1, 2, 1.1, 200));
    trialsVector1.push_back(Trial("alg1", 1, 3, 1.5, 200));

    TrialsVector trialsVector2;
    trialsVector2.push_back(Trial("alg2", 1, 1, 10e-8, 200));
    trialsVector2.push_back(Trial("alg2", 1, 2, 10e-8, 200));
    trialsVector2.push_back(Trial("alg2", 1, 3, 10e-8, 200));
    trialsVector2.push_back(Trial("alg2", 1, 4, 10e-5, 200));
    trialsVector2.push_back(Trial("alg2", 1, 5, 10e-5, 200));

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
    trialsVector1.push_back(Trial("alg1", 2, 1, 99.9, 200));
    trialsVector1.push_back(Trial("alg1", 2, 2, 1.1, 300));
    trialsVector1.push_back(Trial("alg1", 1, 3, 1.5, 400));
    trialsVector1.push_back(Trial("alg1", 1, 4, 1.6, 500));

    TrialsVector trialsVector2;
    trialsVector2.push_back(Trial("alg2", 2, 1, 10e-2, 200));
    trialsVector2.push_back(Trial("alg2", 2, 2, 10e-2, 200));
    trialsVector2.push_back(Trial("alg2", 1, 3, 10e-4, 200));
    trialsVector2.push_back(Trial("alg2", 1, 4, 10e-4, 200));

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
    trialsVector1.push_back(Trial("alg1", 2, 1, 99.9, 200));
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
    trialsVector1.push_back(Trial("alg1", 1, 1, 13.27, 200));
    vector.push_back(trialsVector1);
    std::unordered_map<std::string, double> expected;
    expected["alg1"] = 13.27;
    REQUIRE(calculate_average(1, vector) == expected);
}

TEST_CASE("average many element vector function algorithm", "[average]") {
    FunctionTrialsVector vector;
    TrialsVector trialsVector1;
    trialsVector1.push_back(Trial("alg1", 1, 1, 22.22, 111));
    trialsVector1.push_back(Trial("alg1", 1, 2, 11.11, 222));
    trialsVector1.push_back(Trial("alg1", 1, 3, 33.33, 333));
    trialsVector1.push_back(Trial("alg1", 2, 1, 11.11, 111));
    trialsVector1.push_back(Trial("alg1", 2, 2, 12.22, 222));
    trialsVector1.push_back(Trial("alg1", 2, 3, 10e-8, 333));
    vector.push_back(trialsVector1);

    TrialsVector trialsVector2;
    trialsVector2.push_back(Trial("alg2", 1, 1, 1.1, 111));
    trialsVector2.push_back(Trial("alg2", 1, 2, 10e-8, 222));
    trialsVector2.push_back(Trial("alg2", 1, 3, 10e-8, 333));
    trialsVector2.push_back(Trial("alg2", 2, 1, 10e-8, 111));
    trialsVector2.push_back(Trial("alg2", 2, 2, 10e-8, 222));
    trialsVector2.push_back(Trial("alg2", 2, 3, 10e-8, 333));
    vector.push_back(trialsVector2);

    std::unordered_map<std::string, double> expected;
    expected["alg1"] = 14.99833335;
    expected["alg2"] = 0.18333341666;
    REQUIRE(fabs(calculate_average(3, vector)["alg1"] - expected["alg1"]) < EPSILON);
    REQUIRE(fabs(calculate_average(3, vector)["alg2"] - expected["alg2"]) < EPSILON);
    REQUIRE(expected.size() == 2);
}


TEST_CASE("average empty vector function algorithm throws invalid argument exception", "[average]") {
    FunctionTrialsVector vector;
    REQUIRE_THROWS_AS(calculate_average(1, vector), std::invalid_argument);
    REQUIRE_THROWS_AS(calculate_average(0, vector), std::invalid_argument);
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
    REQUIRE(strict_stod(input) == 0.0000000081118969);
}

TEST_CASE("strict_stod scientific notation input 3", "[utils]") {
    std::string input = "4.7216000e+05";
    REQUIRE(strict_stod(input) == 472160.0);
}

TEST_CASE("Trial operator== same arguments", "[utils]") {
    Trial a = Trial("alg1", 1, 1, 5, 100);
    Trial b = Trial("alg1", 1, 1, 5, 100);
    REQUIRE(a == b);
}

TEST_CASE("Trial operator== both different", "[utils]") {
    Trial a = Trial("alg1", 1, 1, 1, 100);
    Trial b = Trial("alg2", 2, 2, 1.1, 211);
    REQUIRE_FALSE(a == b);
}

TEST_CASE("Trial operator== both equal", "[utils]") {
    Trial a = Trial("alg1", 1, 1, 1.1, 100);
    Trial b = Trial("alg2", 2, 2, 1.1, 100);
    REQUIRE(a == b);
}

TEST_CASE("Trial operator== same error different fes", "[utils]") {
    Trial a = Trial("alg1", 1, 1, 1.1, 100);
    Trial b = Trial("alg2", 2, 2, 1.1, 211);
    REQUIRE_FALSE(a == b);
}

TEST_CASE("Trial operator== different error same fes", "[utils]") {
    Trial a = Trial("alg1", 1, 1, 1.1, 100);
    Trial b = Trial("alg2", 2, 2, 1, 100);
    REQUIRE_FALSE(a == b);
}

TEST_CASE("Trial operator< same arguments", "[utils]") {
    Trial same1 = Trial("alg1", 1, 1, 5, 100);
    Trial same2 = Trial("alg1", 1, 1, 5, 100);
    REQUIRE_FALSE(same1 < same2);
}

TEST_CASE("Trial operator< same error different fes", "[utils]") {
    Trial better = Trial("alg1", 1, 1, 5, 100);
    Trial worse = Trial("alg2", 1, 1, 5, 200);
    REQUIRE_FALSE(better < worse);
    REQUIRE(worse < better);
}

TEST_CASE("Trial operator< different error same fes", "[utils]") {
    Trial better = Trial("alg1", 1, 1, 5, 100);
    Trial worse = Trial("alg2", 1, 1, 5.1, 100);
    REQUIRE_FALSE(better < worse);
    REQUIRE(worse < better);
}


TEST_CASE("Trial operator< same error same fes", "[utils]") {
    Trial same1 = Trial("alg1", 1, 1, 5, 100);
    Trial same2 = Trial("alg2", 1, 1, 5, 100);
    REQUIRE_FALSE(same1 < same2);
}

TEST_CASE("calculate cec2022 without equal trials", "[cec2022]") {
    FunctionTrialsVector vector;
    TrialsVector trialsVector1;
    trialsVector1.push_back(Trial("alg1", 1, 1, 5, 100));
    trialsVector1.push_back(Trial("alg1", 1, 2, 3, 100));
    trialsVector1.push_back(Trial("alg1", 1, 3, 2, 100));

    trialsVector1.push_back(Trial("alg2", 1, 1, 1, 100));
    trialsVector1.push_back(Trial("alg2", 1, 2, 6, 150));
    trialsVector1.push_back(Trial("alg2", 1, 3, 3, 50));

    vector.push_back(trialsVector1);

    ScoreRankingEntry expectedAlg1 = ScoreRankingEntry(10, "alg1", std::nullopt, 7);
    std::vector<ScoreRankingEntry> result = calculate_cec2022_scores(3, 10, vector);

    for (int i = 0; i < 2; ++i) {
        if (result[i].algorithmName == "alg1") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == std::nullopt);
            REQUIRE(result[i].score == 7);
        } else if (result[i].algorithmName == "alg2") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == std::nullopt);
            REQUIRE(result[i].score == 8);
        } else {
            FAIL("Unexpected result");
        }
    }

    REQUIRE(vector.size() == 1);
    REQUIRE(trialsVector1.size() == 6);
}

TEST_CASE("calculate cec2022 only equal trials", "[cec2022]") {
    FunctionTrialsVector vector;
    TrialsVector trialsVector1;
    trialsVector1.push_back(Trial("alg1", 1, 1, 3, 100));
    trialsVector1.push_back(Trial("alg1", 1, 2, 3, 100));
    trialsVector1.push_back(Trial("alg1", 1, 3, 3, 100));

    trialsVector1.push_back(Trial("alg2", 1, 1, 3, 100));
    trialsVector1.push_back(Trial("alg2", 1, 2, 3, 100));
    trialsVector1.push_back(Trial("alg2", 1, 3, 3, 100));

    vector.push_back(trialsVector1);

    std::vector<ScoreRankingEntry> result = calculate_cec2022_scores(3, 10, vector);

    for (int i = 0; i < 2; ++i) {
        if (result[i].algorithmName == "alg1") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == std::nullopt);
            REQUIRE(result[i].score == 7.5);
        } else if (result[i].algorithmName == "alg2") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == std::nullopt);
            REQUIRE(result[i].score == 7.5);
        } else {
            FAIL("Unexpected result");
        }
    }

    REQUIRE(vector.size() == 1);
    REQUIRE(trialsVector1.size() == 6);
}

TEST_CASE("calculate cec2022 some equal trials in same algorithm", "[cec2022]") {
    FunctionTrialsVector vector;
    TrialsVector trialsVector1;
    trialsVector1.push_back(Trial("alg1", 1, 1, 1, 100));
    trialsVector1.push_back(Trial("alg1", 1, 2, 1, 100));
    trialsVector1.push_back(Trial("alg1", 1, 3, 3, 150));

    trialsVector1.push_back(Trial("alg2", 1, 1, 3, 100));
    trialsVector1.push_back(Trial("alg2", 1, 2, 2, 100));
    trialsVector1.push_back(Trial("alg2", 1, 3, 2, 100));

    vector.push_back(trialsVector1);

    std::vector<ScoreRankingEntry> result = calculate_cec2022_scores(3, 10, vector);

    for (int i = 0; i < 2; ++i) {
        if (result[i].algorithmName == "alg1") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == std::nullopt);
            REQUIRE(result[i].score == 9);
        } else if (result[i].algorithmName == "alg2") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == std::nullopt);
            REQUIRE(result[i].score == 6);
        } else {
            FAIL("Unexpected result");
        }
    }
    REQUIRE(vector.size() == 1);
    REQUIRE(trialsVector1.size() == 6);
}

TEST_CASE("calculate cec2022 some equal trials in different algorithm", "[cec2022]") {
    FunctionTrialsVector vector;
    TrialsVector trialsVector1;
    trialsVector1.push_back(Trial("alg1", 1, 1, 1, 100));
    trialsVector1.push_back(Trial("alg1", 1, 2, 1, 100));
    trialsVector1.push_back(Trial("alg1", 1, 3, 3, 150));

    trialsVector1.push_back(Trial("alg2", 1, 1, 3, 100));
    trialsVector1.push_back(Trial("alg2", 1, 2, 1, 100));
    trialsVector1.push_back(Trial("alg2", 1, 3, 2, 100));

    vector.push_back(trialsVector1);

    std::vector<ScoreRankingEntry> result = calculate_cec2022_scores(3, 10, vector);

    for (int i = 0; i < 2; ++i) {
        if (result[i].algorithmName == "alg1") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == std::nullopt);
            REQUIRE(result[i].score == 8);
        } else if (result[i].algorithmName == "alg2") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == std::nullopt);
            REQUIRE(result[i].score == 7);
        } else {
            FAIL("Unexpected result");
        }
    }


    REQUIRE(vector.size() == 1);
    REQUIRE(trialsVector1.size() == 6);
}

TEST_CASE("TrialsVector is properly sorted reverse", "[utils]") {
    TrialsVector trialsVector1;
    trialsVector1.push_back(Trial("alg1", 1, 1, 1, 100));
    trialsVector1.push_back(Trial("alg1", 1, 2, 1, 100));
    trialsVector1.push_back(Trial("alg1", 1, 3, 3, 150));
    trialsVector1.push_back(Trial("alg2", 1, 1, 3, 100));
    trialsVector1.push_back(Trial("alg2", 1, 2, 7, 1));
    trialsVector1.push_back(Trial("alg2", 1, 3, 2, 100));

    std::sort(trialsVector1.rbegin(), trialsVector1.rend());
    REQUIRE(trialsVector1[0].finalError == 1);
    REQUIRE(trialsVector1[0].numberOfEvaluations == 100);

    REQUIRE(trialsVector1[1].finalError == 1);
    REQUIRE(trialsVector1[1].numberOfEvaluations == 100);

    REQUIRE(trialsVector1[2].finalError == 2);
    REQUIRE(trialsVector1[2].numberOfEvaluations == 100);

    REQUIRE(trialsVector1[3].finalError == 3);
    REQUIRE(trialsVector1[3].numberOfEvaluations == 100);

    REQUIRE(trialsVector1[4].finalError == 3);
    REQUIRE(trialsVector1[4].numberOfEvaluations == 150);

    REQUIRE(trialsVector1[5].finalError == 7);
    REQUIRE(trialsVector1[5].numberOfEvaluations == 1);
}

TEST_CASE("TrialsVector is properly sorted", "[utils]") {
    TrialsVector trialsVector1;
    trialsVector1.push_back(Trial("alg1", 1, 1, 1, 100));
    trialsVector1.push_back(Trial("alg1", 1, 2, 1, 100));
    trialsVector1.push_back(Trial("alg1", 1, 3, 3, 150));
    trialsVector1.push_back(Trial("alg2", 1, 1, 3, 100));
    trialsVector1.push_back(Trial("alg2", 1, 2, 7, 1));
    trialsVector1.push_back(Trial("alg2", 1, 3, 2, 100));

    std::sort(trialsVector1.begin(), trialsVector1.end());

    REQUIRE(trialsVector1[0].finalError == 7);
    REQUIRE(trialsVector1[0].numberOfEvaluations == 1);

    REQUIRE(trialsVector1[1].finalError == 3);
    REQUIRE(trialsVector1[1].numberOfEvaluations == 150);
    REQUIRE(trialsVector1[2].finalError == 3);
    REQUIRE(trialsVector1[2].numberOfEvaluations == 100);

    REQUIRE(trialsVector1[3].finalError == 2);
    REQUIRE(trialsVector1[3].numberOfEvaluations == 100);

    REQUIRE(trialsVector1[4].finalError == 1);
    REQUIRE(trialsVector1[5].finalError == 1);

    REQUIRE(trialsVector1[5].numberOfEvaluations == 100);
    REQUIRE(trialsVector1[4].numberOfEvaluations == 100);

}



TEST_CASE("calculate friedman without equal trials", "[friedman]") {
    BasicRankingInput input;
    TrialsVector trialsVector1;
    TrialsVector trialsVector2;
    std::unordered_map<Dimension, std::unordered_map<AlgorithmName, TrialsVector>> functionInput;

    trialsVector1.push_back(Trial("alg1", 1, 1, 5, 100));
    trialsVector1.push_back(Trial("alg1", 1, 2, 3, 100));
    trialsVector1.push_back(Trial("alg1", 1, 3, 2, 100));

    trialsVector2.push_back(Trial("alg2", 1, 1, 1, 100));
    trialsVector2.push_back(Trial("alg2", 1, 2, 6, 150));
    trialsVector2.push_back(Trial("alg2", 1, 3, 3, 50));

    functionInput[10]["alg1"] = trialsVector1;
    functionInput[10]["alg2"] = trialsVector2;

    input.push_back(functionInput);

    std::vector<ScoreRankingEntry> result = calculate_friedman_scores(3, input);

    for (int i = 0; i < 2; ++i) {
        if (result[i].algorithmName == "alg1") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == 1);
            REQUIRE(result[i].score - (3 + 2 / double(3)) < EPSILON);
        } else if (result[i].algorithmName == "alg2") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == 1);
            REQUIRE(result[i].score - (3 + 1 / double(3)) < EPSILON);
        } else {
            FAIL("Unexpected result");
        }
    }

    REQUIRE(input.size() == 1);
    REQUIRE(trialsVector1.size() == 3);
    REQUIRE(trialsVector2.size() == 3);
}


TEST_CASE("calculate friedman with equal only trials", "[friedman]") {
    BasicRankingInput input;
    TrialsVector trialsVector1;
    TrialsVector trialsVector2;
    std::unordered_map<Dimension, std::unordered_map<AlgorithmName, TrialsVector>> functionInput;

    trialsVector1.push_back(Trial("alg1", 1, 1, 3, 100));
    trialsVector1.push_back(Trial("alg1", 1, 2, 3, 100));
    trialsVector1.push_back(Trial("alg1", 1, 3, 3, 100));

    trialsVector2.push_back(Trial("alg2", 1, 1, 3, 100));
    trialsVector2.push_back(Trial("alg2", 1, 2, 3, 100));
    trialsVector2.push_back(Trial("alg2", 1, 3, 3, 100));

    functionInput[10]["alg1"] = trialsVector1;
    functionInput[10]["alg2"] = trialsVector2;

    input.push_back(functionInput);

    std::vector<ScoreRankingEntry> result = calculate_friedman_scores(3, input);

    for (int i = 0; i < 2; ++i) {
        if (result[i].algorithmName == "alg1") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == 1);
            REQUIRE(result[i].score == 1.5);
        } else if (result[i].algorithmName == "alg2") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == 1);
            REQUIRE(result[i].score == 1.5);
        } else {
            FAIL("Unexpected result");
        }
    }

    REQUIRE(input.size() == 1);
    REQUIRE(trialsVector1.size() == 3);
    REQUIRE(trialsVector2.size() == 3);

}

TEST_CASE("calculate friedman with some equal trials in different algorithm", "[friedman]") {
    BasicRankingInput input;
    TrialsVector trialsVector1;
    TrialsVector trialsVector2;
    std::unordered_map<Dimension, std::unordered_map<AlgorithmName, TrialsVector>> functionInput;
    
    trialsVector1.push_back(Trial("alg1", 1, 1, 1, 100));
    trialsVector1.push_back(Trial("alg1", 1, 2, 1, 100));
    trialsVector1.push_back(Trial("alg1", 1, 3, 3, 150));

    trialsVector2.push_back(Trial("alg2", 1, 1, 3, 100));
    trialsVector2.push_back(Trial("alg2", 1, 2, 1, 100));
    trialsVector2.push_back(Trial("alg2", 1, 3, 2, 100));

    functionInput[10]["alg1"] = trialsVector1;
    functionInput[10]["alg2"] = trialsVector2;

    input.push_back(functionInput);

    std::vector<ScoreRankingEntry> result = calculate_friedman_scores(3, input);

    for (int i = 0; i < 2; ++i) {
        if (result[i].algorithmName == "alg1") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == 1);
            REQUIRE(result[i].score - (3 + 1/double(3)) < EPSILON);
        } else if (result[i].algorithmName == "alg2") {
            REQUIRE(result[i].dimension == 10);
            REQUIRE(result[i].functionNumber == 1);
            REQUIRE(result[i].score - (3 + 2/double(3)) < EPSILON);
        } else {
            FAIL("Unexpected result");
        }
    }

    REQUIRE(input.size() == 1);
    REQUIRE(trialsVector1.size() == 3);
    REQUIRE(trialsVector2.size() == 3);
}