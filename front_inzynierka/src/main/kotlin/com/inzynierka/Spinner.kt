package com.inzynierka

import io.kvision.core.Container
import io.kvision.react.react
import io.kvision.utils.px
import react.ComponentClass
import react.Props

external interface SpinnerProps : Props {
    var size: Int
    var loading: Boolean
    var color: String
}

val Spinner: ComponentClass<SpinnerProps> = io.kvision.require("react-spinners").PropagateLoader

fun Container.withLoadingSpinner(isLoading: Boolean, element: Container.() -> Unit) {
    if (isLoading) {
        val spinner = react {
            Spinner {
                loading = true
                size = 20
                color = "#0d6efd"
            }
        }
        spinner.padding = 64.px
    } else {
        element()
    }
}
