package net.muliba.changeskin.data

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
        override fun apply(view: View, resName: String) {
            val drawable = getResourceManager()?.getDrawable(resName)
            if (drawable!=null) {
                view.background = drawable
            }
        }

    }
    ,
    COLOR("textColor") {
        override fun apply(view: View, resName: String) {
            getResourceManager()?.let { manager->
                val color = manager.getColor(resName)
                if (color != -1) {
                    (view as TextView).setTextColor(color)
                }

            }
        }
    },
    SRC("src") {
        override fun apply(view: View, resName: String) {
            getResourceManager()?.let { manager ->
                val drawable = manager.getDrawable(resName)
                if(drawable!=null) {
                    (view as ImageView).setImageDrawable(drawable)
                }

            }
        }
    };

    abstract fun apply(view: View, resName: String)

    fun  getResourceManager(): ResourceManager?  = FancySkinManager.instance().getResourceManager()
}