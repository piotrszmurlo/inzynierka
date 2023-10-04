#!/usr/bin/env bash

g++ test.cpp -std=c++17  -I ../../extern -o tests.out && ./tests.out
