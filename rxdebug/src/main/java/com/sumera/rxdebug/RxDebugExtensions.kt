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

/**
 * A [io.reactivex.Observable] debug extension that will log an useful information from this stream
 *
 * Logged events: (doOnSubscribe, doOnNext, doOnError, doOnComplete, doOnTerminate, doOnDispose)
 *
 * @param tag Optional argument that will add an additional tag into the log
 */
fun <E> Observable<E>.debug(tag: String? = null): Observable<E> {
    val formattedTag = getFormattedTag(tag)
    return doOnNext { DebugLogger.log("OnNext", value = it, tag = formattedTag) }
            .doOnError { DebugLogger.log("OnError", error = it, tag = formattedTag)  }
            .doOnSubscribe { DebugLogger.log("OnSubscribe", tag = formattedTag) }
            .doOnDispose { DebugLogger.log("OnDispose", tag = formattedTag) }
            .doOnTerminate { DebugLogger.log("OnTerminate", tag = formattedTag) }
            .doOnComplete { DebugLogger.log("OnComplete", tag = formattedTag) }
}

/**
 * A [io.reactivex.Flowable] debug extension that will log an useful information from this stream
 *
 * Logged events: (doOnSubscribe, doOnNext, doOnError, doOnComplete, doOnTerminate, doOnCancel)
 *
 * @param tag Optional argument that will add an additional tag into the log
 */
fun <E> Flowable<E>.debug(tag: String? = null): Flowable<E> {
    val formattedTag = getFormattedTag(tag)
    return doOnNext { DebugLogger.log("OnNext", value = it, tag = formattedTag) }
            .doOnError { DebugLogger.log("OnError", error = it, tag = formattedTag)  }
            .doOnSubscribe { DebugLogger.log("OnSubscribe", tag = formattedTag) }
            .doOnTerminate { DebugLogger.log("OnTerminate", tag = formattedTag) }
            .doOnComplete { DebugLogger.log("OnComplete", tag = formattedTag) }
            .doOnCancel { DebugLogger.log("OnCancel", tag = formattedTag) }
}

/**
 * A [io.reactivex.Single] debug extension that will log an useful information from this stream
 *
 * Logged events: (doOnSubscribe, doOnSuccess, doOnError, doOnDispose)
 *
 * @param tag Optional argument that will add an additional tag into the log
 */
fun <E> Single<E>.debug(tag: String? = null): Single<E> {
    val formattedTag = getFormattedTag(tag)
    return doOnSuccess { DebugLogger.log("OnSuccess", value = it, tag = formattedTag) }
            .doOnError { DebugLogger.log("OnError", error = it, tag = formattedTag)  }
            .doOnSubscribe { DebugLogger.log("OnSubscribe", tag = formattedTag) }
            .doOnDispose { DebugLogger.log("OnDispose", tag = formattedTag) }
}

/**
 * A [io.reactivex.Maybe] debug extension that will log an useful information from this stream
 *
 * Logged events: (doOnSubscribe, doOnSuccess, doOnError, doOnComplete, doOnDispose)
 *
 * @param tag Optional argument that will add an additional tag into the log
 */
fun <E> Maybe<E>.debug(tag: String? = null): Maybe<E> {
    val formattedTag = getFormattedTag(tag)
    return doOnSuccess { DebugLogger.log("OnSuccess", value = it, tag = formattedTag) }
            .doOnError { DebugLogger.log("OnError", error = it, tag = formattedTag)  }
            .doOnSubscribe { DebugLogger.log("OnSubscribe", tag = formattedTag) }
            .doOnComplete { DebugLogger.log("OnComplete", tag = formattedTag) }
            .doOnDispose { DebugLogger.log("OnDispose", tag = formattedTag) }
}

/**
 * A [io.reactivex.Completable] debug extension that will log an useful information from this stream
 *
 * Logged events: (doOnSubscribe, doOnError, doOnComplete, doOnDispose)
 *
 * @param tag Optional argument that will add an additional tag into the log
 */
fun Completable.debug(tag: String? = null): Completable {
    val formattedTag = getFormattedTag(tag)
    return doOnError { DebugLogger.log("OnError", error = it, tag = formattedTag) }
            .doOnComplete { DebugLogger.log("OnComplete", tag = formattedTag) }
            .doOnSubscribe { DebugLogger.log("OnSubscribe", tag = formattedTag) }
            .doOnDispose { DebugLogger.log("OnDispose", tag = formattedTag) }
}

/**
 * Global settings of RxDebug extensions
 */
object RxDebug {

    internal val isLoggingEnabled: Boolean
        get() = isLoggingEnabledInternal

    private var isLoggingEnabledInternal = true

    /**
     * Globally enable/disable logs for [debug] methods
     *
     * @param isLoggindEnabled true if logging should be enabled, false otherwise
     */
    fun setLoggingEnabled(isLoggindEnabled: Boolean) {
        this.isLoggingEnabledInternal = isLoggindEnabled
    }
}

internal object StackTraceTagCreator {

    private const val MAX_TAG_LENGTH = 23

    private const val CALL_STACK_INDEX_FOR_METHOD_WITHOUT_TAG = 4

    private const val CALL_STACK_INDEX_FOR_METHOD_WITH_TAG = 3

    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

    fun getFormattedTag(tag: String?): String {
        return if (tag == null) {
            StackTraceTagCreator.getStacktraceTag(atIndex = CALL_STACK_INDEX_FOR_METHOD_WITHOUT_TAG)
        } else {
            val stackTraceTag = StackTraceTagCreator.getStacktraceTag(atIndex = CALL_STACK_INDEX_FOR_METHOD_WITH_TAG)
            "$stackTraceTag: $tag"
        }
    }

    private fun getStacktraceTag(atIndex: Int): String {
        val stackTrace = Throwable().stackTrace
        if (stackTrace.size <= atIndex) {
            throw IllegalStateException("Synthetic stacktrace didn't have enough elements: are you using proguard?")
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

    fun log(title: String, tag: String) {
        if (RxDebug.isLoggingEnabled.not()) {
            return
        }

        DebugLogger.logInternal(title, "", tag)
    }

    fun <E> log(title: String, value: E, tag: String) {
        if (RxDebug.isLoggingEnabled.not()) {
            return
        }

        val message = value.toString()
        DebugLogger.logInternal(title, message, tag)
    }

    fun log(title: String, error: Throwable, tag: String) {
        if (RxDebug.isLoggingEnabled.not()) {
            return
        }

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