package com.inzynierka

import react.ComponentClass
import react.Props

external interface SpinnerProps : Props {
    var size: Int
    var loading: Boolean
    var speedMultiplier: Float
    var color: String
}

val Spinner: ComponentClass<SpinnerProps> = io.kvision.require("react-spinners").PropagateLoader
