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

TEST_CASE("median_calculate test", "[median]") {
    std::vector<double> a;
    a.push_back(1.3);
    a.push_back(1.1);
    a.push_back(1.5);
    REQUIRE(median(a) == 1.3 );
}
