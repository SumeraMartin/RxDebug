![Bitrise Status Batch](https://www.bitrise.io/app/a1b4cf0e7fbe4f37/status.svg?token=6OMQfWN2YTSCYO9SXU-v3w&branch=master)

# RxDebug
Simple kotlin extension for logging useful information from RxJava2 streams.

The debug extension will automatically figure out from which class it's being called and use that class name as its tag.

## Usage
```Kotlin
Observable.just("One", "Two", "Three")
        .debug()
        .subscribe()
```

This will produce the following output to log:

<img src="images/log_without_tag.png" width="800">

OR

```Kotlin
Observable.just("One", "Two", "Three")
        .debug(tag = "Words")
        .subscribe()
```

This will produce the following output to log:

<img src="images/log_with_tag.png" width="800">

In order to have a clearer log messages RxDebug allows you to transform onNext/onSuccess values for log messages

```Kotlin
Observable.just("One", "Two", "Three")
        .debug(tag = "Words length") { it.length }
        .subscribe()
```

RxDebug supports all RxJava2 stream types _(Observable, Flowable, Single, Maybe, Completable)_

## Setup

In order to disable debug logs globally:

```Kotlin
RxDebug.setLoggingEnabled(false) // true is default value
```

## Download
```groovy
implementation 'com.sumera.rxdebug:rxdebug:1.0.0'
```
