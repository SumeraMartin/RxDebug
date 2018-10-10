![Bitrise Status](https://app.bitrise.io/app/a1b4cf0e7fbe4f37/status.svg?token=6OMQfWN2YTSCYO9SXU-v3w&branch=master)

# RxDebug
Simple kotlin extension for logging useful information from RxJava2 streams.

The debug extension will automatically figure out from which class it's being called and use that class name as its tag.

## Usage
```Kotlin
Observable.just("One", "Two", "Three")
        .debug()  // The name of an enclosing class will be used as a tag
        .subscribe()
```

This code snippet will produce the following output to the log:

<img src="images/log_without_tag.png" width="800">

OR

```Kotlin
Observable.just("One", "Two", "Three")
        .debug(tag = "Words") // The name of an enclosing class and "Words" will be used as a tag
        .subscribe()
```

This code snippet will produce the following output to the log:

<img src="images/log_with_tag.png" width="800">

In order to have a clearer log messages RxDebug allows you to transform onNext/onSuccess values that are added to the log:

```Kotlin
Observable.just("One", "Two", "Three")
        .debug(tag = "Words length") { it.substring(0, 2) } // The log for onNext values will contain "On", "Tw", "Th"
        .subscribe()
```

RxDebug supports all RxJava2 stream types _(Observable, Flowable, Single, Maybe, Completable)_

## Setup

In order to disable debug logs globally:

```Kotlin
RxDebug.setLoggingEnabled(false) // Logging is enabled by default
```

## Download
```groovy
implementation 'com.sumera.rxdebug:rxdebug:1.1.1'
```
