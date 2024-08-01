package com.calvin.box.movie.nano.process

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession

interface Process {
    fun isRequest(session: IHTTPSession, path: String): Boolean

    fun doResponse(
        session: IHTTPSession,
        path: String,
        files: Map<String, String>
    ): NanoHTTPD.Response
}
