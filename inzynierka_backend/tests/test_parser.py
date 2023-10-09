import base64

import pytest

from main import ROOT_DIR

from src import parse_remote_file_name, ParseError, parse_remote_results_file

TEST_DATA_DIR = f"{ROOT_DIR}/tests/test_data"


def test_parse_simple_file_name():
    file_name = 'IUMOEAII_1_10.txt'
    assert ('IUMOEAII', 1, 10) == parse_remote_file_name(file_name)


def test_parse_file_name_with_underscores():
    file_name = 'S_LSHADE_DP_3_20.txt'
    assert ('S_LSHADE_DP', 3, 20) == parse_remote_file_name(file_name)


def test_parse_file_name_dat_extension():
    file_name = 'S_LSHADE_DP_3_20.dat'
    assert ('S_LSHADE_DP', 3, 20) == parse_remote_file_name(file_name)


def test_parse_file_name_no_extension():
    file_name = 'S_LSHADE_DP_3_20'
    with pytest.raises(ParseError):
        parse_remote_file_name(file_name)


def test_parse_file_name_jpg_extension():
    file_name = 'S_LSHADE_DP_3_20.jpg'
    with pytest.raises(ParseError):
        parse_remote_file_name(file_name)


def test_parse_file_name_bad_dimensions_and_function_number():
    file_name = 'BAD_NAME_A_B.txt'
    with pytest.raises(ParseError):
        parse_remote_file_name(file_name)


def test_parse_file_name_bad_dimensions():
    file_name = 'BAD_DIMENSION_5_B.txt'
    with pytest.raises(ParseError):
        parse_remote_file_name(file_name)


def test_parse_file_name_bad_function_numbers():
    file_name = 'BAD_DIMENSION_A_10.txt'
    with pytest.raises(ParseError):
        parse_remote_file_name(file_name)


def test_parse_file_name_missing_segments():
    file_name = 'MISSING_FUNCTION_NUMBER_10.txt'
    with pytest.raises(ParseError):
        parse_remote_file_name(file_name)


def test_parse_file_name_missing_segments2():
    file_name = 'MISSING_FUNCTION_NUMBER_.txt'
    with pytest.raises(ParseError):
        parse_remote_file_name(file_name)


def test_parse_file_name_missing_segments3():
    file_name = 'MISSING_10_.txt'
    with pytest.raises(ParseError):
        parse_remote_file_name(file_name)


def test_parse_file_name_missing_segments4():
    file_name = 'MISSING_10.txt'
    with pytest.raises(ParseError):
        parse_remote_file_name(file_name)


def test_parse_remote_results_file_correct_input():
    file_name = 'jSObinexpEig_3_20.txt'
    with open(f"{TEST_DATA_DIR}/correct_input/jSObinexpEig_3_20.txt", 'rb') as input_file, \
         open(f"{TEST_DATA_DIR}/processed_input/jSObinexpEig_3_20_parsed_contents", 'r') as parsed_file:
        b64_contents = base64.b64encode(input_file.read())
        expected_parsed_contents = parsed_file.read()
        assert ('jSObinexpEig', 3, 20, expected_parsed_contents) == parse_remote_results_file(file_name, b64_contents)


def test_parse_remote_results_file_incorrect_characters_in_data():
    file_name = 'CHARACTERS_IN_DATA_2_20.txt'
    with open(f"{TEST_DATA_DIR}/incorrect_input/CHARACTERS_IN_DATA_2_20.txt", 'rb') as input_file:
        b64_contents = base64.b64encode(input_file.read())
        with pytest.raises(ParseError):
            parse_remote_results_file(file_name, b64_contents)
