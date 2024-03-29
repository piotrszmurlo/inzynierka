import base64
import pytest
from src.dependencies.parser import parse_remote_filename, ParseError, parse_remote_results_file

def test_parse_simple_file_name():
    file_name = 'IUMOEAII_1_10.txt'
    assert ('IUMOEAII', 1, 10) == parse_remote_filename(file_name)


def test_parse_file_name_with_underscores():
    file_name = 'S_LSHADE_DP_3_20.txt'
    assert ('S_LSHADE_DP', 3, 20) == parse_remote_filename(file_name)


def test_parse_file_name_dat_extension():
    file_name = 'S_LSHADE_DP_3_20.dat'
    assert ('S_LSHADE_DP', 3, 20) == parse_remote_filename(file_name)


def test_parse_file_name_no_extension():
    file_name = 'S_LSHADE_DP_3_20'
    with pytest.raises(ParseError):
        parse_remote_filename(file_name)


def test_parse_file_name_jpg_extension():
    file_name = 'S_LSHADE_DP_3_20.jpg'
    with pytest.raises(ParseError):
        parse_remote_filename(file_name)


def test_parse_file_name_bad_dimensions_and_function_number():
    file_name = 'BAD_NAME_A_B.txt'
    with pytest.raises(ParseError):
        parse_remote_filename(file_name)


def test_parse_file_name_bad_dimensions():
    file_name = 'BAD_DIMENSION_5_B.txt'
    with pytest.raises(ParseError):
        parse_remote_filename(file_name)


def test_parse_file_name_bad_function_numbers():
    file_name = 'BAD_DIMENSION_A_10.txt'
    with pytest.raises(ParseError):
        parse_remote_filename(file_name)


def test_parse_file_name_missing_segments():
    file_name = 'MISSING_FUNCTION_NUMBER_10.txt'
    with pytest.raises(ParseError):
        parse_remote_filename(file_name)


def test_parse_file_name_missing_segments2():
    file_name = 'MISSING_FUNCTION_NUMBER_.txt'
    with pytest.raises(ParseError):
        parse_remote_filename(file_name)


def test_parse_file_name_missing_segments3():
    file_name = 'MISSING_10_.txt'
    with pytest.raises(ParseError):
        parse_remote_filename(file_name)


def test_parse_file_name_missing_segments4():
    file_name = 'MISSING_10.txt'
    with pytest.raises(ParseError):
        parse_remote_filename(file_name)


def test_parse_remote_results_file_correct_input(request):
    file_name = 'jSObinexpEig_3_20.txt'
    with open(f"{request.config.rootdir}/tests/test_data/correct_input/jSObinexpEig_3_20.txt", 'rb') as input_file, \
         open(f"{request.config.rootdir}/tests/test_data/processed_input/jSObinexpEig_3_20_parsed_contents.txt", 'r') as parsed_file:
        b64_contents = base64.b64encode(input_file.read())
        expected_parsed_contents = parsed_file.read()
        assert ('jSObinexpEig', 3, 20, expected_parsed_contents) == parse_remote_results_file(file_name, b64_contents, 30)


def test_parse_remote_results_file_incorrect_characters_in_data(request):
    file_name = 'CHARACTERS_IN_DATA_2_20.txt'
    with open(f"{request.config.rootdir}/tests/test_data/incorrect_input/CHARACTERS_IN_DATA_2_20.txt", 'rb') as input_file:
        b64_contents = base64.b64encode(input_file.read())
        with pytest.raises(ParseError):
            parse_remote_results_file(file_name, b64_contents, 30)
