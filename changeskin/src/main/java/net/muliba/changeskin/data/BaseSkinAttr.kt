package net.muliba.changeskin.data

import android.view.View

/**
 * Created by fancyLou on 2017/11/10.
 * Copyright © 2017 O2. All rights reserved.
 */

open abstract class BaseSkinAttr(var attrName:String, var originResId: Int, var resName: String) {

    abstract fun apply(view: View)
    abstract fun copy(attrName:String, originResId: Int, resName: String): BaseSkinAttr

    override fun toString(): String {
        return "SkinAttr{attrName:$attrName, originResId:$originResId, resName:$resName }"
    }
}