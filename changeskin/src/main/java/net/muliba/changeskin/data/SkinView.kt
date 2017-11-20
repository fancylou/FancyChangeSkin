package net.muliba.changeskin.data

import android.view.View

/**
 * Created by fancylou on 10/25/17.
 */

data class SkinView(val view:View, val skinAttrList: List<BaseSkinAttr>) {
    fun apply() {
        skinAttrList.map { skinAttr ->
            skinAttr.apply(view)
        }
    }
}