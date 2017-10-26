package net.muliba.changeskin.callback

import android.util.Log

/**
 * Created by fancylou on 10/25/17.
 */

interface PluginSkinChangingListener {
    fun onStart()
    fun onError(e:Exception)
    fun onCompleted()
}

class DefaultPluginSkinChangingListener : PluginSkinChangingListener {
    override fun onStart() {
    }

    override fun onError(e: Exception) {
        Log.e("changeSkin", "onError", e)
    }

    override fun onCompleted() {
    }
}