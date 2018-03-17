package com.sumera.rxdebug

import android.util.Log
import io.github.plastix.rxschedulerrule.RxSchedulerRule
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import org.robolectric.shadows.ShadowLog.LogItem

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
internal class TestClass {

    @JvmField @Rule var testRule = RxSchedulerRule()

    private val tagName = "TestClass"
    private val testException = RuntimeException("TestException")

    //region Observable tests

    @Test
    fun `Debug without tag for observable should log all required methods`() {
        //WHEN
        Observable.just("A", "B", "C")
                .debug()
                .subscribe()

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, "OnNext: A")
                .hasDebugMessage(tagName, "OnNext: B")
                .hasDebugMessage(tagName, "OnNext: C")
                .hasDebugMessage(tagName, "OnTerminate")
                .hasDebugMessage(tagName, "OnComplete")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug without tag for error observable should log all required methods`() {
        //WHEN
        Observable.error<RuntimeException>(testException)
                .debug()
                .subscribe({}, {})

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, testException)
                .hasDebugMessage(tagName, "OnTerminate")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for observable should log all required methods`() {
        //WHEN
        Observable.just("A", "B", "C")
                .debug(tag = "TEST TAG")
                .subscribe()

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, "OnNext: A")
                .hasDebugMessage(expectedTag, "OnNext: B")
                .hasDebugMessage(expectedTag, "OnNext: C")
                .hasDebugMessage(expectedTag, "OnTerminate")
                .hasDebugMessage(expectedTag, "OnComplete")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for error observable should log all required methods`() {
        //WHEN
        Observable.error<RuntimeException>(testException)
                .debug(tag = "TEST TAG")
                .subscribe({}, {})

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, testException)
                .hasDebugMessage(expectedTag, "OnTerminate")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug without tag for disposed observable should log all required methods`() {
        //WHEN
        Observable.create<String> {}
                .debug()
                .subscribe({}, {})
                .dispose()

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, "OnDispose")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for disposed observable should log all required methods`() {
        //WHEN
        Observable.create<String> {}
                .debug(tag = "TEST TAG")
                .subscribe({}, {})
                .dispose()

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, "OnDispose")
                .hasNoMoreMessages()
    }

    //endregion

    //region Flowable tests

    @Test
    fun `Debug without tag for flowable should log all required methods`() {
        //WHEN
        Flowable.just("A", "B", "C")
                .debug()
                .subscribe()

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, "OnNext: A")
                .hasDebugMessage(tagName, "OnNext: B")
                .hasDebugMessage(tagName, "OnNext: C")
                .hasDebugMessage(tagName, "OnTerminate")
                .hasDebugMessage(tagName, "OnComplete")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug without tag for error flowable should log all required methods`() {
        //WHEN
        Flowable.error<RuntimeException>(testException)
                .debug()
                .subscribe({}, {})

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, testException)
                .hasDebugMessage(tagName, "OnTerminate")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for flowable should log all required methods`() {
        //WHEN
        Flowable.just("A", "B", "C")
                .debug(tag = "TEST TAG")
                .subscribe()

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, "OnNext: A")
                .hasDebugMessage(expectedTag, "OnNext: B")
                .hasDebugMessage(expectedTag, "OnNext: C")
                .hasDebugMessage(expectedTag, "OnTerminate")
                .hasDebugMessage(expectedTag, "OnComplete")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for error flowable should log all required methods`() {
        //WHEN
        Flowable.error<RuntimeException>(testException)
                .debug(tag = "TEST TAG")
                .subscribe({}, {})

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, testException)
                .hasDebugMessage(expectedTag, "OnTerminate")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for disposed flowable should log all required methods`() {
        //WHEN
        Flowable.create<String>({}, BackpressureStrategy.BUFFER)
                .debug(tag = "TEST TAG")
                .subscribe()
                .dispose()

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, "OnCancel")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug without tag for disposed flowable should log all required methods`() {
        //WHEN
        Flowable.create<String>({}, BackpressureStrategy.BUFFER)
                .debug()
                .subscribe()
                .dispose()

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, "OnCancel")
                .hasNoMoreMessages()
    }

    //endregion

    //region Single tests

    @Test
    fun `Debug without tag for success single should log all required methods`() {
        //WHEN
        Single.just("A")
                .debug()
                .subscribe()

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, "OnSuccess: A")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug without tag for error single should log all required methods`() {
        //WHEN
        Single.error<RuntimeException>(testException)
                .debug()
                .subscribe({}, {})

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, testException)
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for success single should log all required methods`() {
        //WHEN
        Single.just("A")
                .debug(tag = "TEST TAG")
                .subscribe()

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, "OnSuccess: A")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for error single should log all required methods`() {
        //WHEN
        Single.error<RuntimeException>(testException)
                .debug(tag = "TEST TAG")
                .subscribe({}, {})

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, testException)
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug without tag for disposed single should log all required methods`() {
        //WHEN
        Single.create<String> {  }
                .debug()
                .subscribe()
                .dispose()

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, "OnDispose")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for disposed single should log all required methods`() {
        //WHEN
        Single.create<String> {  }
                .debug(tag = "TEST TAG")
                .subscribe()
                .dispose()

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, "OnDispose")
                .hasNoMoreMessages()
    }

    //endregion

    //region Completable tests

    @Test
    fun `Debug without tag for completed completable should log all required methods`() {
        //WHEN
        Completable.complete()
                .debug()
                .subscribe()

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, "OnComplete")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug without tag for error completable should log all required methods`() {
        //WHEN
        Completable.error(testException)
                .debug()
                .subscribe({}, {})

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, testException)
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for completed completable should log all required methods`() {
        //WHEN
        Completable.complete()
                .debug(tag = "TEST TAG")
                .subscribe()

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, "OnComplete")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for error completable should log all required methods`() {
        //WHEN
        Completable.error(testException)
                .debug(tag = "TEST TAG")
                .subscribe({}, {})

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, testException)
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug without tag for disposed completable should log all required methods`() {
        //WHEN
        Completable.create {  }
                .debug()
                .subscribe()
                .dispose()

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, "OnDispose")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for disposed completable should log all required methods`() {
        //WHEN
        Completable.create {  }
                .debug(tag = "TEST TAG")
                .subscribe()
                .dispose()

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, "OnDispose")
                .hasNoMoreMessages()
    }

    //endregion

    //region Maybe tests

    @Test
    fun `Debug without tag for success maybe should log all required methods`() {
        //WHEN
        Maybe.just("A")
                .debug()
                .subscribe()

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, "OnSuccess: A")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug without tag for error maybe should log all required methods`() {
        //WHEN
        Maybe.error<RuntimeException>(testException)
                .debug()
                .subscribe({}, {})

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, testException)
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug without tag for empty maybe should log all required methods`() {
        //WHEN
        Maybe.empty<String>()
                .debug()
                .subscribe()

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, "OnComplete")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for success maybe should log all required methods`() {
        //WHEN
        Maybe.just("A")
                .debug(tag = "TEST TAG")
                .subscribe()

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, "OnSuccess: A")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for error maybe should log all required methods`() {
        //WHEN
        Maybe.error<RuntimeException>(testException)
                .debug(tag = "TEST TAG")
                .subscribe({}, {})

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, testException)
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for empty maybe should log all required methods`() {
        //WHEN
        Maybe.empty<String>()
                .debug(tag = "TEST TAG")
                .subscribe()

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, "OnComplete")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug without tag for disposed maybe should log all required methods`() {
        //WHEN
        Maybe.create<String> {  }
                .debug()
                .subscribe()
                .dispose()

        //THEN
        assertLog()
                .hasDebugMessage(tagName, "OnSubscribe")
                .hasDebugMessage(tagName, "OnDispose")
                .hasNoMoreMessages()
    }

    @Test
    fun `Debug with tag for disposed maybe should log all required methods`() {
        //WHEN
        Maybe.create<String> {  }
                .debug(tag = "TEST TAG")
                .subscribe()
                .dispose()

        //THEN
        val expectedTag = "$tagName: TEST TAG"
        assertLog()
                .hasDebugMessage(expectedTag, "OnSubscribe")
                .hasDebugMessage(expectedTag, "OnDispose")
                .hasNoMoreMessages()
    }

    //endregion

    //region Class name truncation tests

    internal class ClassWithVeryVeryVeryLongName {
        fun executeTest() {
            Single.just("A").debug().subscribe()
        }
    }

    @Config(sdk = [23])
    @Test
    fun `Debug with long class name for api 23 should log only part of class name`() {
        // WHEN
        ClassWithVeryVeryVeryLongName().executeTest()

        // THEN
        val tag = "TestClass\$ClassWithVery"
        assertLog()
                .hasDebugMessage(tag, "OnSubscribe")
                .hasDebugMessage(tag, "OnSuccess: A")
                .hasNoMoreMessages()
    }

    @Config(sdk = [24])
    @Test
    fun `Debug with long class name for api 24 should log whole class name`() {
        // WHEN
        ClassWithVeryVeryVeryLongName().executeTest()

        // THEN
        val tag = "TestClass\$ClassWithVeryVeryVeryLongName"
        assertLog()
                .hasDebugMessage(tag, "OnSubscribe")
                .hasDebugMessage(tag, "OnSuccess: A")
                .hasNoMoreMessages()
    }

    //endregion

    //region Long message tests

    @Test
    fun `Debug with long message should split this message into separate lines`() {
        // WHEN
        val titleLength = "OnSuccess: ".length
        var testText = "A".repeat(3000)
        testText += "\n"
        testText += "B".repeat(4000 - titleLength)
        testText += "C".repeat(3000)
        testText += "D".repeat(1000 - titleLength)
        testText += "D".repeat(2000 )
        Single.just(testText).debug().subscribe()

        // THEN
        val tag = "TestClass"
        assertLog()
                .hasDebugMessage(tag, "OnSubscribe")
                .hasDebugMessage(tag, "OnSuccess: " + "A".repeat(3000))
                .hasDebugMessage(tag, "OnSuccess: " + "B".repeat(4000 - titleLength))
                .hasDebugMessage(tag, "OnSuccess: " + "C".repeat(3000) + "D".repeat(1000 - titleLength))
                .hasDebugMessage(tag, "OnSuccess: " + "D".repeat(2000))
                .hasNoMoreMessages()
    }

    //endregion

    //region Anonymous classes tests

    @Config(sdk = [24])
    @Test
    fun `Debug tag for anonymous classes is removed`() {
        Runnable {
            Single.just("A").debug().subscribe()
            Runnable {
                Single.just("B").debug().subscribe()
            }.run()
        }.run()

        val tag = "TestClass"
        assertLog()
                .hasDebugMessage(tag, "OnSubscribe")
                .hasDebugMessage(tag, "OnSuccess: A")
                .hasDebugMessage(tag, "OnSubscribe")
                .hasDebugMessage(tag, "OnSuccess: B")
                .hasNoMoreMessages()
    }

    @Config(sdk = [24])
    @Test
    fun `Debug tag with custom tag for anonymous classes is removed`() {
        Runnable {
            Single.just("A").debug(tag = "TEST TAG1").subscribe()
            Runnable {
                Single.just("B").debug(tag = "TEST TAG2").subscribe()
            }.run()
        }.run()

        val tag1 = "TestClass: TEST TAG1"
        val tag2 = "TestClass: TEST TAG2"
        assertLog()
                .hasDebugMessage(tag1, "OnSubscribe")
                .hasDebugMessage(tag1, "OnSuccess: A")
                .hasDebugMessage(tag2, "OnSubscribe")
                .hasDebugMessage(tag2, "OnSuccess: B")
                .hasNoMoreMessages()
    }

    class InnerTestClass {
        class InnerInnerTestClass {
            fun executeTest() {
                Runnable {
                    Single.just("A").debug().subscribe()
                    Runnable {
                        Single.just("B").debug().subscribe()
                    }.run()
                }.run()
            }
        }

        fun executeTest() {
            Runnable {
                Single.just("A").debug().subscribe()
                Runnable {
                    Single.just("B").debug().subscribe()
                }.run()
            }.run()
        }
    }

    @Config(sdk = [24])
    @Test
    fun `Debug tag for anonymous inner class is removed`() {
        InnerTestClass().executeTest()

        val tag = "TestClass\$InnerTestClass"
        assertLog()
                .hasDebugMessage(tag, "OnSubscribe")
                .hasDebugMessage(tag, "OnSuccess: A")
                .hasDebugMessage(tag, "OnSubscribe")
                .hasDebugMessage(tag, "OnSuccess: B")
                .hasNoMoreMessages()
    }

    @Config(sdk = [24])
    @Test
    fun `Debug tag for anonymous inner class with inner class is removed`() {
        InnerTestClass.InnerInnerTestClass().executeTest()

        val tag = "TestClass\$InnerTestClass\$InnerInnerTestClass"
        assertLog()
                .hasDebugMessage(tag, "OnSubscribe")
                .hasDebugMessage(tag, "OnSuccess: A")
                .hasDebugMessage(tag, "OnSubscribe")
                .hasDebugMessage(tag, "OnSuccess: B")
                .hasNoMoreMessages()
    }

    //endregion

    private fun assertLog(): LogAssert {
        return LogAssert(ShadowLog.getLogs())
    }

    private class LogAssert constructor(private val items: List<LogItem>) {
        private var index = 0

        fun hasDebugMessage(tag: String, message: String): LogAssert {
            return hasMessage(Log.DEBUG, tag, message)
        }

        fun hasDebugMessage(tag: String, throwable: Throwable): LogAssert {
            return hasError(Log.DEBUG, tag, throwable)
        }

        private fun hasError(priority: Int, tag: String, throwable: Throwable): LogAssert {
            val item = items[index++]
            assertEquals(priority, item.type)
            assertEquals(tag, item.tag)
            Assert.assertTrue(item.msg.contains("java.lang.RuntimeException: TestException"))
            Assert.assertTrue(item.msg.contains("TestClass"))
            return this
        }

        private fun hasMessage(priority: Int, tag: String, message: String): LogAssert {
            val item = items[index++]
            assertEquals(priority, item.type)
            assertEquals(tag, item.tag)
            assertEquals(message, item.msg)
            return this
        }

        fun hasNoMoreMessages() {
            assertEquals(items.size, index)
        }
    }
}