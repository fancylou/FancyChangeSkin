package net.muliba.changeskin.callback

import android.util.Log

/**
 * Created by fancyLou on 2017/11/22.
 * Copyright © 2017 O2. All rights reserved.
 */

class DefaultPluginSkinChangingListener : PluginSkinChangingListener {
    override fun onStart() {
    }

    override fun onError(e: Exception) {
        Log.e("changeSkin", "onError", e)
    }

    override fun onCompleted() {
    }
}