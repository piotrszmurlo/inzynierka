#!/usr/bin/env bash

g++ tests/cpp/test.cpp -std=c++17  -I ../extern -o tests/cpp/tests.out && ./tests/cpp/tests.out && python3 -m pipenv run test
