package com.sumera.rxdebug.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.sumera.rxdebug.debug
import io.reactivex.Observable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.without_tag_button).setOnClickListener {
            Observable.just("One", "Two", "Three")
                    .debug()
                    .subscribe()
        }

        findViewById<Button>(R.id.with_tag_button).setOnClickListener {
            Observable.just("One", "Two", "Three")
                    .debug(tag = "Words")
                    .subscribe()
        }

        findViewById<Button>(R.id.with_tag_converter_and_with_error).setOnClickListener {
            Observable.create<String> { source ->
                source.onNext("One")
                source.onNext("Two")
                source.onNext("Three")
                source.onError(RuntimeException("Test exception"))
            }
                    .debug("Words") { it.length }
                    .subscribe({}, {})
        }
    }
}
