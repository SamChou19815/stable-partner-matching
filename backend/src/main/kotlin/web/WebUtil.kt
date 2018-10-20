package web

import web.gson.CursorTypeAdapter
import web.gson.KeyTypeAdapter
import com.google.cloud.datastore.Cursor
import com.google.cloud.datastore.Key
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import spark.Request
import spark.ResponseTransformer
import spark.Route
import spark.Spark
import spark.kotlin.halt
import kotlin.system.measureTimeMillis

/**
 * A default global Gson for the entire app.
 */
val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Key::class.java, KeyTypeAdapter)
        .registerTypeAdapter(Cursor::class.java, CursorTypeAdapter)
        .create()

/**
 * [Transformer] transforms the response to correct form.
 */
object Transformer : ResponseTransformer {
    /**
     * Render the object into expected string format.
     */
    override fun render(model: Any?): String = when (model) {
        Unit -> ""; is String -> model; else -> gson.toJson(model)
    }
}

/**
 * [toJson] converts the body of the `Request` to a parsed json object in this Request receiver.
 */
inline fun <reified T> Request.toJson(): T = gson.fromJson(body(), T::class.java)

/**
 * [queryParamsForKey] returns the key from the given query params in this Request receiver.
 * If the data with [name] is not a key, it will end with a 400 error.
 */
fun Request.queryParamsForKey(name: String): Key =
        queryParams(name)?.let(block = Key::fromUrlSafe) ?: badRequest()

/**
 * [queryParamsForCursor] returns the cursor from the given query params in this Request receiver.
 * If the data with [name] is not a cursor, it will end with a 400 error.
 */
fun Request.queryParamsForCursor(name: String): Cursor =
        queryParams(name)?.let(block = Cursor::fromUrlSafe) ?: badRequest()

/**
 * Registers a GET handler with [path] and a user given function [f].
 */
inline fun get(path: String, crossinline f: Request.() -> Any?): Unit =
        Spark.get(path, Route { request, _ -> request.f() }, Transformer)

/**
 * Registers a POST handler with [path] and a user given function [f].
 */
inline fun post(path: String, crossinline f: Request.() -> Any?): Unit =
        Spark.post(path, Route { request, _ -> request.f() }, Transformer)

/**
 * Registers a DELETE handler with [path] and a user function [f].
 */
inline fun delete(path: String, crossinline f: Request.() -> Any?): Unit =
        Spark.delete(path, Route { request, _ -> request.f() }, Transformer)

/**
 * [badRequest] is used to indicate a bad request.
 */
fun badRequest(): Nothing = throw halt(code = 400)

/**
 * [initServer] initializes the server by [initFunction] at [port].
 */
inline fun initServer(port: Int = 8080, crossinline initFunction: () -> Unit) {
    val initTime = measureTimeMillis {
        Spark.port(port)
        initFunction()
    }
    println("Initialized in ${initTime}ms.")
}
