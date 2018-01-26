package net.muliba.changeskin.appcompat

import android.content.Context
import android.support.v4.view.LayoutInflaterFactory
import android.support.v7.app.AppCompatActivity
import android.util.ArrayMap
import android.util.AttributeSet
import android.util.Log
import android.view.InflateException
import android.view.View
import net.muliba.changeskin.FancySkinManager
import net.muliba.changeskin.data.BaseSkinAttr
import net.muliba.changeskin.data.SkinView
import java.lang.reflect.Constructor


/**
 * Created by fancyLou on 2017/11/8.
 * Copyright © 2017 O2. All rights reserved.
 */


class FancySkinLayoutInflaterFactory(private val mContext: Context) : LayoutInflaterFactory {


    private val skinViewsMap = HashMap<View, SkinView>()


    override fun onCreateView(parent: View?, name: String, context: Context?, attrs: AttributeSet?): View? {
        var view: View? = null
        val skinAttrList = filterSkinAttr(attrs, context)
        if (skinAttrList.isEmpty()) {
            return null
        }

        if (context == null || attrs == null) {
            return null
        }
        if (mContext is AppCompatActivity) {
            view = mContext.delegate.createView(parent, name, context, attrs)
        }
        if (view == null) {
            view = createViewFromTag(context, name, attrs)
        }
        if (view != null) {
            val skinView = SkinView(view!!, skinAttrList)
            skinViewsMap.put(view!!, skinView)
            skinView.apply()
        }
        if (view == null ) {
            Log.i("oncreateView", "create view is null")
        }
        return view
    }

    fun applySkin() {
        skinViewsMap.entries.forEach { entry ->
            entry.value.apply()
        }
    }

    fun removeSkinView(view: View) {
        skinViewsMap.remove(view)
    }

    fun clean() {
        skinViewsMap.clear()
    }


    private fun createViewFromTag(context: Context, name: String, attrs: AttributeSet?): View? {
        var viewName = name
        if (viewName == "view") {
            viewName = attrs?.getAttributeValue(null, "class") ?: ""
        }
        try {
            if (viewName?.indexOf(".") == -1) {
                val viewWidget = createView(context, attrs, viewName, "android.widget.")
                if (viewWidget != null) {
                    return viewWidget
                }
                val viewView = createView(context, attrs, viewName, "android.view.")
                if (viewView != null) {
                    return viewView
                }
                val viewWebkit = createView(context, attrs, viewName, "android.webkit.")
                if (viewWebkit != null) {
                    return viewWebkit
                }
                return null
            } else {
                return createView(context, attrs, viewName, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }


    private val sConstructorMap = ArrayMap<String, Constructor<out View>>()

    @Throws(ClassNotFoundException::class, InflateException::class)
    private fun createView(context: Context, attrs: AttributeSet?, name: String, prefix: String?): View? {
        var constructor = sConstructorMap[name]

        try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                val clazz = context.classLoader.loadClass(
                        if (prefix != null) prefix + name else name).asSubclass(View::class.java)

                constructor = clazz.getConstructor(Context::class.java, AttributeSet::class.java)
                sConstructorMap.put(name, constructor)
            }
            constructor!!.isAccessible = true
            return constructor!!.newInstance(context, attrs)
        } catch (e: Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null
        }

    }

    private fun filterSkinAttr(attrs: AttributeSet?, context: Context?) : List<BaseSkinAttr> {
        if (attrs==null) return emptyList()
        val skinAttrArray = ArrayList<BaseSkinAttr>()
        for (index in 0 until attrs.attributeCount) {
            val attrName = attrs.getAttributeName(index)
            val attrValue = attrs.getAttributeValue(index)
            if (!FancySkinManager.instance().isSupportAttrType(attrName)) continue
            if (attrValue.startsWith("@")) {
                try {
                    val id = attrValue.substring(1).toInt()
                    if (id == 0) {
                        continue
                    }
                    val entryName = context?.resources?.getResourceEntryName(id)
                    if (entryName!=null) {
                        val skinAttr = FancySkinManager.instance().createSupportAttr(attrName, id, entryName)
                        if (skinAttr!=null) {
                            skinAttrArray.add(skinAttr)
                        }
                    }
                }catch (e: Exception){}
            }
        }
        return skinAttrArray
    }

}