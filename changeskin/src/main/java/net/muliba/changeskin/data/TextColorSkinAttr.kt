package net.muliba.changeskin.data

import android.view.View
import android.widget.TextView
import net.muliba.changeskin.FancySkinManager

/**
 * Created by fancyLou on 2017/11/10.
 * Copyright © 2017 O2. All rights reserved.
 */

class TextColorSkinAttr(attrName:String = "", originResId: Int = 0, resName: String = "" ): BaseSkinAttr(attrName, originResId, resName) {
    override fun apply(view: View) {
        FancySkinManager.instance().getResourceManager()?.let { manager->
            val color = manager.getColor(originResId, resName)
            if (color != -1) {
                try {
                    (view as TextView).setTextColor(color)
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun copy(attrName: String, originResId: Int, resName: String): BaseSkinAttr {
        return TextColorSkinAttr(attrName, originResId, resName)
    }
}