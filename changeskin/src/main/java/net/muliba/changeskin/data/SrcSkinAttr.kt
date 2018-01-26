package net.muliba.changeskin.data

import android.view.View
import android.widget.ImageView
import net.muliba.changeskin.FancySkinManager

/**
 * Created by fancyLou on 2017/11/10.
 * Copyright © 2017 O2. All rights reserved.
 */
class SrcSkinAttr(attrName:String = "", originResId: Int = 0, resName: String = "" ): BaseSkinAttr(attrName, originResId, resName) {

    override fun apply(view: View) {
        FancySkinManager.instance().getResourceManager()?.let { manager ->
            val drawable = manager.getDrawable(originResId, resName)
            if(drawable!=null) {
                try {
                    (view as ImageView).setImageDrawable(drawable)
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun copy(attrName: String, originResId: Int, resName: String): BaseSkinAttr {
        return SrcSkinAttr(attrName, originResId, resName)
    }
}