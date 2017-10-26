package net.muliba.changeskin

import android.app.Application

/**
 * Created by fancylou on 10/25/17.
 */


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FancySkinManager.instance().init(this)
    }
}