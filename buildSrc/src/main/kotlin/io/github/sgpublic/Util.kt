package io.github.sgpublic

import com.google.gson.JsonObject
import com.google.gson.Gson
import com.google.gson.JsonArray
import org.gradle.api.Project
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private val HttpClient by lazy {
    java.net.http.HttpClient.newHttpClient()
}

fun NetJsonObject(url: String, converter: (String) -> String = { it }): JsonObject {
    val json = fetchRemote(url).let(converter)
    return Gson().fromJson(json, JsonObject::class.java)
        ?: throw IllegalStateException("Failed to parse json! content: $json")
}

fun NetJsonArray(url: String, converter: (String) -> String = { it }): JsonArray {
    val json = fetchRemote(url).let(converter)
    return Gson().fromJson(json, JsonArray::class.java)
        ?: throw IllegalStateException("Failed to parse json! content: $json")
}

fun fetchRemote(url: String): String {
    return try {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build()
        val resp: HttpResponse<String> = HttpClient.send(
            request, HttpResponse.BodyHandlers.ofString()
        )
        resp.body()
    } catch (e: Throwable) {
        throw IllegalStateException("Failed to read remote resource.")
    }
}

fun commandLine(command: String): String {
    return Runtime.getRuntime().exec(command)
        .inputStream.reader().readText().trim()
}

fun command(vararg command: String): String {
    return command.joinToString(" &&\\\n ")
}

fun aptInstall(vararg pkg: String): String {
    return "apt-get install -y ${pkg.joinToString(" ")}"
}

fun rm(vararg file: String): String {
    return "rm -rf ${file.joinToString(" ")}"
}
