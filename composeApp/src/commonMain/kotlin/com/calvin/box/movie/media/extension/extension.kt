package com.calvin.box.movie.media.extension

import com.calvin.box.movie.media.util.formatInterval
import com.calvin.box.movie.media.util.formatMinSec

fun Int.formatMinSec(): String {
    return formatMinSec(this)
}

fun Int.formattedInterval(): Int {
    return  formatInterval(this)
}