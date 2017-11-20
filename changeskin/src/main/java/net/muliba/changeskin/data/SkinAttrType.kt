package net.muliba.changeskin.data

import android.support.v4.view.ViewCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import net.muliba.changeskin.FancySkinManager
import net.muliba.changeskin.ResourceManager

/**
 * Created by fancylou on 10/25/17.
 */

enum class SkinAttrType(val attrType: String) {

    BACKGROUND("background") {
        override fun apply(view: View, originResId: Int, resName: String) {
            val drawable = getResourceManager()?.getDrawable(originResId, resName)
            if (drawable!=null) {
                ViewCompat.setBackground(view, drawable)
            }
        }

    }
    ,
    COLOR("textColor") {
        override fun apply(view: View, originResId: Int, resName: String) {
            getResourceManager()?.let { manager->
                val color = manager.getColor(originResId, resName)
                if (color != -1) {
                    (view as TextView).setTextColor(color)
                }

            }
        }
    },
    SRC("src") {
        override fun apply(view: View, originResId: Int, resName: String) {
            getResourceManager()?.let { manager ->
                val drawable = manager.getDrawable(originResId, resName)
                if(drawable!=null) {
                    (view as ImageView).setImageDrawable(drawable)
                }

            }
        }
    };

    abstract fun apply(view: View, originResId: Int, resName: String)

    fun  getResourceManager(): ResourceManager?  = FancySkinManager.instance().getResourceManager()
}