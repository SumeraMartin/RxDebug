package com.sumera.rxdebug

import android.os.Build
import android.util.Log
import com.sumera.rxdebug.StackTraceTagCreator.getFormattedTag
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.io.PrintWriter
import java.io.StringWriter
import java.util.regex.Pattern

fun <E> Observable<E>.debug(tag: String? = null): Observable<E> {
    val formattedTag = getFormattedTag(tag)
    return doOnNext { DebugLogger.log("OnNext", value = it, tag = formattedTag) }
            .doOnError { DebugLogger.log("OnError", error = it, tag = formattedTag)  }
            .doOnSubscribe { DebugLogger.log("OnSubscribe", tag = formattedTag) }
            .doOnDispose { DebugLogger.log("OnDispose", tag = formattedTag) }
            .doOnTerminate { DebugLogger.log("OnTerminate", tag = formattedTag) }
            .doOnComplete { DebugLogger.log("OnComplete", tag = formattedTag) }
}

fun <E> Flowable<E>.debug(tag: String? = null): Flowable<E> {
    val formattedTag = getFormattedTag(tag)
    return doOnNext { DebugLogger.log("OnNext", value = it, tag = formattedTag) }
            .doOnError { DebugLogger.log("OnError", error = it, tag = formattedTag)  }
            .doOnSubscribe { DebugLogger.log("OnSubscribe", tag = formattedTag) }
            .doOnTerminate { DebugLogger.log("OnTerminate", tag = formattedTag) }
            .doOnComplete { DebugLogger.log("OnComplete", tag = formattedTag) }
            .doOnCancel { DebugLogger.log("OnCancel", tag = formattedTag) }
}

fun <E> Single<E>.debug(tag: String? = null): Single<E> {
    val formattedTag = getFormattedTag(tag)
    return doOnSuccess { DebugLogger.log("OnSuccess", value = it, tag = formattedTag) }
            .doOnError { DebugLogger.log("OnError", error = it, tag = formattedTag)  }
            .doOnSubscribe { DebugLogger.log("OnSubscribe", tag = formattedTag) }
            .doOnDispose { DebugLogger.log("OnDispose", tag = formattedTag) }
}

fun <E> Maybe<E>.debug(tag: String? = null): Maybe<E> {
    val formattedTag = getFormattedTag(tag)
    return doOnSuccess { DebugLogger.log("OnSuccess", value = it, tag = formattedTag) }
            .doOnError { DebugLogger.log("OnError", error = it, tag = formattedTag)  }
            .doOnSubscribe { DebugLogger.log("OnSubscribe", tag = formattedTag) }
            .doOnComplete { DebugLogger.log("OnComplete", tag = formattedTag) }
            .doOnDispose { DebugLogger.log("OnDispose", tag = formattedTag) }
}

fun Completable.debug(tag: String? = null): Completable {
    val formattedTag = getFormattedTag(tag)
    return doOnError { DebugLogger.log("OnError", error = it, tag = formattedTag) }
            .doOnComplete { DebugLogger.log("OnComplete", tag = formattedTag) }
            .doOnSubscribe { DebugLogger.log("OnSubscribe", tag = formattedTag) }
            .doOnDispose { DebugLogger.log("OnDispose", tag = formattedTag) }
}

internal object StackTraceTagCreator {

    private const val MAX_TAG_LENGTH = 23
    private const val CALL_STACK_INDEX = 3
    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

    fun getFormattedTag(tag: String?): String {
        return if (tag == null) {
            StackTraceTagCreator.getStacktraceTag(atIndex = 4)
        } else {
            val stackTraceTag = StackTraceTagCreator.getStacktraceTag(atIndex = 3)
            "$stackTraceTag: $tag"
        }
    }

    private fun getStacktraceTag(atIndex: Int): String {
        val stackTrace = Throwable().stackTrace
        if (stackTrace.size <= atIndex) {
            throw IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?")
        }
        return createStackElementTag(stackTrace[atIndex])
    }

    private fun createStackElementTag(element: StackTraceElement): String {
        var tag = element.className
        val m = StackTraceTagCreator.ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
            tag = tag.substring(0, tag.lastIndexOf('$'))
        }
        tag = tag.substring(tag.lastIndexOf('.') + 1)

        // Tag length limit was removed in API 24.
        return if (tag.length <= StackTraceTagCreator.MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tag
        } else {
            tag.substring(0, StackTraceTagCreator.MAX_TAG_LENGTH)
        }
    }
}

internal object DebugLogger {

    private const val MAX_LOG_LENGTH = 4000

    fun <E> log(title: String, value: E, tag: String) {
        DebugLogger.logObject(title, value, tag)
    }

    fun log(title: String, tag: String) {
        DebugLogger.logTitle(title, tag)
    }

    fun log(title: String, error: Throwable, tag: String) {
        DebugLogger.logError(title, error, tag)
    }

    private fun logTitle(title: String, tag: String) {
        DebugLogger.logInternal(title, "", tag)
    }

    private fun <E> logObject(title: String, value: E, tag: String) {
        val message = value.toString()
        DebugLogger.logInternal(title, message, tag)
    }

    private fun logError(title: String, error: Throwable, tag: String) {
        val message = getStackTraceString(error)
        DebugLogger.logInternal(title, message, tag)
    }

    private fun logInternal(title: String, message: String, tag: String) {
        val formattedTitle = if (message.isEmpty()) title else "$title: "
        val maxLength = MAX_LOG_LENGTH - formattedTitle.length
        if (message.length < maxLength) {
            Log.d(tag, "$formattedTitle$message")
            return
        }

        // Split by line, then ensure each line can fit into Log's maximum length.
        var i = 0
        val length = message.length
        while (i < length) {
            var newline = message.indexOf('\n', i)
            newline = if (newline != -1) newline else length
            do {
                val end = Math.min(newline, i + maxLength)
                val part = message.substring(i, end)
                Log.d(tag, "$formattedTitle$part")
                i = end
            } while (i < newline)
            i++
        }
    }

    private fun getStackTraceString(t: Throwable): String {
        val sw = StringWriter(256)
        val pw = PrintWriter(sw, false)
        t.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }
}