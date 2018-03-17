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
            Observable.just("First", "Second", "Third")
                    .debug(tag = "Words")
                    .subscribe()
        }
    }
}
