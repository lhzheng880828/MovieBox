package com.calvin.box.movie

import java.net.MalformedURLException

class Uri {
    protected var uri: String? = null

    /**
     * Returns the URI's scheme.
     */
    var scheme: String? = null
        protected set

    /**
     * Returns the host from the URI's authority part, or null
     * if no host is provided.  If the host is an IPv6 literal, the
     * delimiting brackets are part of the returned value (see
     * [java.net.URI.getHost]).
     */
    var host: String? = null
        protected set

    /**
     * Returns the port from the URI's authority part, or -1 if
     * no port is provided.
     */
    var port: Int = -1
        protected set
    protected var hasAuthority: Boolean = false

    /**
     * Returns the URI's path.  The path is never null.  Note that a
     * slash following the authority part (or the scheme if there is
     * no authority part) is part of the path.  For example, the path
     * of "http://host/a/b" is "/a/b".
     */
    var path: String = ""
        protected set

    /**
     * Returns the URI's query part, or null if no query is provided.
     * Note that a query always begins with a leading "?".
     */
    var query: String = ""
        protected set


    /**
     * Creates a Uri object given a URI string.
     */
    constructor(uri: String) {
        init(uri)
    }

    /**
     * Creates an uninitialized Uri object. The init() method must
     * be called before any other Uri methods.
     */
    protected constructor()

    /**
     * Initializes a Uri object given a URI string.
     * This method must be called exactly once, and before any other Uri
     * methods.
     */
    @Throws(MalformedURLException::class)
    protected fun init(uri: String) {
        this.uri = uri
        parse(uri)
    }

    /**
     * Returns the URI as a string.
     */
    override fun toString(): String {
        return uri!!
    }

    /*
     * Parses a URI string and sets this object's fields accordingly.
     */
    @Throws(MalformedURLException::class)
    private fun parse(uri: String) {
        var i: Int // index into URI

        i = uri.indexOf(':') // parse scheme
        if (i < 0) {
            throw MalformedURLException("Invalid URI: $uri")
        }
        scheme = uri.substring(0, i)
        i++ // skip past ":"

        hasAuthority = uri.startsWith("//", i)
        if (hasAuthority) {                             // parse "//host:port"
            i += 2 // skip past "//"
            var slash = uri.indexOf('/', i)
            if (slash < 0) {
                slash = uri.length
            }
            if (uri.startsWith("[", i)) {               // at IPv6 literal
                val brac = uri.indexOf(']', i + 1)
                if (brac < 0 || brac > slash) {
                    throw MalformedURLException("Invalid URI: $uri")
                }
                host = uri.substring(i, brac + 1) // include brackets
                i = brac + 1 // skip past "[...]"
            } else {                                    // at host name or IPv4
                val colon = uri.indexOf(':', i)
                val hostEnd = if ((colon < 0 || colon > slash)
                ) slash
                else colon
                if (i < hostEnd) {
                    host = uri.substring(i, hostEnd)
                }
                i = hostEnd // skip past host
            }

            if ((i + 1 < slash) &&
                uri.startsWith(":", i)
            ) {       // parse port
                i++ // skip past ":"
                port = uri.substring(i, slash).toInt()
            }
            i = slash // skip to path
        }
        val qmark = uri.indexOf('?', i) // look for query
        if (qmark < 0) {
            path = uri.substring(i)
        } else {
            path = uri.substring(i, qmark)
            query = uri.substring(qmark)
        }
    } /*
    // Debug
    public static void main(String args[]) throws MalformedURLException {
        for (int i = 0; i < args.length; i++) {
            Uri uri = new Uri(args[i]);

            String h = (uri.getHost() != null) ? uri.getHost() : "";
            String p = (uri.getPort() != -1) ? (":" + uri.getPort()) : "";
            String a = uri.hasAuthority ? ("//" + h + p) : "";
            String q = (uri.getQuery() != null) ? uri.getQuery() : "";

            String str = uri.getScheme() + ":" + a + uri.getPath() + q;
            if (! uri.toString().equals(str)) {
                System.out.println(str);
            }
            System.out.println(h);
        }
    }
*/
}