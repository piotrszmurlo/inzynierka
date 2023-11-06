package com.inzynierka.data

import io.kvision.types.KFile

val KFile.actualFileContentOnly: String?
    get() = content?.split(",")?.get(1)

val Exception?.parsedRemoteExceptionMessage: String?
    get() = this?.message?.substringAfterLast("\"detail\":")?.substringBeforeLast("}\"")