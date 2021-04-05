package com.sscorp.stocks

import android.app.Application
import android.content.Context

// appContext for database
class App : Application() {

    companion object {
        private lateinit var appContext: Context

        fun setContext(context: Context) {
            appContext = context
        }

        fun getAppContext(): Context = appContext
    }
}