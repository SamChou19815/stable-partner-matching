@file:JvmName(name = "WebApp")

import com.developersam.web.get
import com.developersam.web.initServer
import spark.Spark.path

/**
 * [initializeApiHandlers] initializes a list of handlers.
 */
private fun initializeApiHandlers() {
    get(path = "/") { "OK" } // Used for health check
    get(path = "/apis/echo") { "OK" } // Used for health check
    // path("/apis/public", ::initializePublicApiHandlers)
    // path("/apis/user", ::initializeUserApiHandlers)
}

/*
 * ------------------------------------------------------------------------------------------
 * Part 2: Main
 * ------------------------------------------------------------------------------------------
 */

/**
 * [main] is the entry point.
 *
 * @param args these info will be ignored right now.
 */
fun main(args: Array<String>): Unit = initServer(initFunction = ::initializeApiHandlers)