#!/usr/bin/env bash

g++ tests/test.cpp -std=c++17  -I ../extern -o tests.out && ./tests.out
