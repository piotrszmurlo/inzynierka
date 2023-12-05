package com.inzynierka.ui

import io.kvision.i18n.gettext
import io.kvision.i18n.tr

object StringResources {
    var COMPARE_TWO_ALGORITHMS_TAB_LABEL = tr("Compare two algorithms")
    var FRIEDMAN_TAB_LABEL = tr("Friedman")
    var CEC2022_TAB_LABEL = tr("CEC 2022")
    var MEDIAN_TAB_LABEL = tr("Median")
    var MEAN_TAB_LABEL = tr("Mean")
    var ECDF_TAB_LABEL = tr("ECDF")
    var REVISITED_TAB_LABEL = tr("Revisited")

    var COMBINED_RANKING_TABLE_HEADER = tr("Combined")
    var CEC2022_RANKING_TABLE_HEADER = tr("CEC 2022 score")
    var COMBINED_CEC2022_RANKING_TABLE_HEADER = tr("Combined CEC 2022 score")
    var CEC2022_RANKING_DESCRIPTION = tr("This ranking is based on CEC'22 official evaluation criteria.")
    var FRIEDMAN_RANKING_DESCRIPTION = tr("Friedman ranking.")
    var ECDF_DESCRIPTION = tr("Empirical Cumulative Distribution Functions.")
    var REVISITED_DESCRIPTION =
        tr("Ranking method proposed in \"Revisiting CEC 2022 ranking for bound constrained single objective optimization\" article")
    var RANK = tr("Rank")
    var ALGORITHM = tr("Algorithm")
    var ALGORITHM_NAME = tr("Algorithm name")
    var SUCCESSFUL_TRIALS = tr("Successful trials")
    var THRESHOLDS_ACHIEVED = tr("Thresholds achieved")
    var BUDGET_LEFT = tr("Budget left")
    var SCORE = tr("Score")
    var AVERAGE = tr("Average")
    var MEAN = tr("Mean")
    var MEDIAN = tr("Median")
    var STDEV = tr("Stdev")
    var BEST = tr("Best")
    var WORST = tr("Worst")

    var ECDF_Y_AXIS_LABEL = tr("Fraction of thresholds achieved")
    var ECDF_X_AXIS_LABEL = tr("f-evaluations / dimension")
    var PER_FUNCTION = tr("Per function")
    var AVERAGED = tr("Averaged")

    var SELECT_DIMENSION = tr("Select dimension")
    var SELECT_ALGORITHMS = tr("Select algorithms")
    var SELECT_FILES = tr("Select files")
    var UPLOAD_FILES = tr("Upload files")
    var SELECTED_FILES = tr("Selected Files: ")
    var UPLOAD_FILE_TAB_TITLE = tr("Select all CEC'22 results files for one algorithm to upload")
    var RESULTS_TABLE = tr("Results table")
    var FUNCTION_NUMBER = tr("Function Number")
    var RESULT = tr("Result")
    var EQUAL = tr("Equal")
    var SUMMED_UP_RESULTS = tr("Summed up results")
    var SUMMED_UP_WINS = tr("Sum of wins")
    var COMPARE = tr("Compare")
    var PAIR_TEST_TITLE = tr("Compare algorithm using Wilcoxon signed-rank test")
    var TOGGLE_NOTATION = tr("Toggle Notation")
    var SELECT_PRECISION_AND_NOTATION = tr("Select precision and notation")
    var UPLOAD_RESULTS_LABEL = tr("Upload Results")
    var BROWSE_RANKINGS_LABEL = tr("Browse rankings")
    var NAVBAR_TITLE = tr("AE comparison")
    var ADMIN_CONSOLE_LABEL = tr("Admin console")
    var LOGIN_LABEL = tr("Log in")
    var REGISTER_LABEL_PROMPT = tr("Create new account")
    var LOGIN_FAILED_TOAST = tr("Failed to log in. Check credentials.")
    var REGISTER_SUCCESS_TOAST = tr("Successfully registered.")
    var REGISTER_ERROR_TOAST = tr("Failed to register. Email already in use.")
    var EMAIL_PLACEHOLDER = tr("example@email.com")
    var PASSWORD = tr("Password")
    var REGISTER_LABEL = tr("Register")
    var OVERWRITE_FILES_DIALOG_TEXT = tr("Selected files already exist. Overwrite these files?")
    var PASSWORD_TOOLTIP = tr("Minimum 8 characters, no whitespace allowed")
    var LOGOUT_LABEL = tr("Log out")
    var DELETE_ALGORITHM_BUTTON_LABEL = tr("Delete algorithm data")
    var DELETE_ALGORITHM_DESCRIPTION = tr("Select an algorithm to delete its' data")
    var PROMOTE_USERS_DESCRIPTION = tr("Provide user's email to promote for site admin")

    var TOAST_FAILED_TO_LOAD_RANKING = tr("Failed to load ranking data")
    var TOAST_FAILED_TO_LOAD_ADMIN_CONSOLE = tr("Failed to load admin console data")
    var TOAST_FAILED_TO_PROMOTE_USER = tr("User with given email not found")
    var TOAST_PROMOTE_USER_SUCCESS = tr("User promoted to admin successfully")
    var TOAST_FAILED_TO_DELETE_ALGORITHM_DATA = tr("Failed to delete algorithm data")
    var TOAST_DELETE_ALGORITHM_DATA_SUCCESS = tr("Sucessfully deleted algorithm data")
    var TOAST_FILE_UPLOAD_COMPLETED = tr("Files upload completed")
    var TOAST_MAXIMUM_FILE_SIZE_EXCEEDED = tr("Maximum file size exceeded")
    var PROMOTE_USER_BUTTON_LABEL = tr("Promote user to admin")
    fun FILE_UPLOAD_ERROR(message: String?) = gettext("Files upload failed: %1", message)

    fun DIMENSION_FUNCTION_GROUP_EQUALS(dim: Int, functionGroup: String) =
        gettext("Dimension = %1, Function Group: %2", dim, functionGroup)

    fun DIMENSION_FUNCTION_COMBINED(dim: Int) =
        gettext("Dimension = %1, All functions combined", dim)

    fun DIMENSION_FUNCTION_NUMBER_EQUALS(dim: Int, functionNumber: Int) =
        gettext("Dimension = %1, Function Number = %2", dim, functionNumber)

    fun DIMENSION_EQUALS(dim: Int) = gettext("Dimension = %1", dim)

}
