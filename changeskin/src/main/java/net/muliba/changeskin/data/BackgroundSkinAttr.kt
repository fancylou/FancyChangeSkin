package net.muliba.changeskin.data

import android.view.View
import net.muliba.changeskin.FancySkinManager

/**
 * Created by fancyLou on 2017/11/10.
 * Copyright © 2017 O2. All rights reserved.
 */
class BackgroundSkinAttr(attrName:String = "", originResId: Int = 0, resName: String = "" ): BaseSkinAttr(attrName, originResId, resName) {

    override fun apply(view: View) {
        val bottom = view.paddingBottom
        val top = view.paddingTop
        val right = view.paddingRight
        val left = view.paddingLeft
        /**
         * 资源是color的时候 通过getDrawable获取到drawable 去setBackground 会有颜色获取不正确的情况  ColorDrawable 里面的mBaseColor 和 mUseColor两个值不一致
         * 所以如果是color资源，通过getColor获取color值 setBackgroundColor
         */
        val isColorId = FancySkinManager.instance().getResourceManager()?.getColorResIdByName(resName) ?: 0
        if (isColorId!=0) {
            val color = FancySkinManager.instance().getResourceManager()?.getColorByResId(isColorId) ?: -1
            if (color != -1){
                try {
                    view.setBackgroundColor(color)
                } catch (e: Exception) {
                }
            }
        }else {
            val drawable = FancySkinManager.instance().getResourceManager()?.getDrawable(originResId, resName)
            if (drawable!=null) {
                try {
                    view.background = drawable
                } catch (e: Exception) {
                }
            }
        }
        view.setPadding(left, top, right, bottom)

    }

    override fun copy(attrName: String, originResId: Int, resName: String): BaseSkinAttr {
        return BackgroundSkinAttr(attrName, originResId, resName)
    }
}