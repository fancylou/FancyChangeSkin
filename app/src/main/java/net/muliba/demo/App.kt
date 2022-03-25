package net.muliba.demo

import android.app.Application
import net.muliba.changeskin.FancySkinManager

/**
 * Created by fancylou on 10/25/17.
 */


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FancySkinManager.instance().withoutActivity(this)
    }
}