package net.muliba.changeskin.callback

/**
 * Created by fancylou on 10/25/17.
 */

interface PluginSkinChangingListener {
    fun onStart()
    fun onError(e:Exception)
    fun onCompleted()
}
