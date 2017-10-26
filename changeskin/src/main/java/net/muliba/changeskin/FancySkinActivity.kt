package net.muliba.changeskin

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.support.v4.view.LayoutInflaterCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.AttributeSet
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import net.muliba.changeskin.callback.SkinChangedListener
import net.muliba.changeskin.data.SkinAttr
import net.muliba.changeskin.data.SkinView
import net.muliba.changeskin.data.getSkinAttrs
import java.lang.reflect.Constructor


/**
 * Created by fancylou on 10/25/17.
 */


open class FancySkinActivity : AppCompatActivity(), SkinChangedListener, LayoutInflater.Factory2 {

    override fun onCreate(savedInstanceState: Bundle?) {
        val layoutInflater = LayoutInflater.from(this)
        LayoutInflaterCompat.setFactory2(layoutInflater, this)
        super.onCreate(savedInstanceState)
        changeStatusColor()
        FancySkinManager.instance().addSkinChangedListener(this)
    }


//        var mCreateViewMethod: Method? = null
    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        var view: View? = null
        try {
//            if (mCreateViewMethod==null) {
//                val method = delegate.javaClass.getMethod("createView", View::class.java, String::class.java, Context::class.java, AttributeSet::class.java)
//                mCreateViewMethod = method
//            }
//            val o = mCreateViewMethod?.invoke(delegate, parent, name, context, attrs)
            val o = delegate.createView(parent, name, context, attrs)
            if (o != null) {
                view = o
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (context == null || TextUtils.isEmpty(name)) {
            return view
        }
        if (view == null) {
            view = createViewFromTag(context, name!!, attrs)
        }
        val skinAttrList = getSkinAttrs(attrs, context)
        injectSkin(view, skinAttrList)
        return view
    }


    private fun injectSkin(view: View?, skinAttrList: List<SkinAttr>) {
        if (skinAttrList.isNotEmpty() && view != null) {
            var skinViewList = FancySkinManager.instance().getSkinViews(this)
            if (skinViewList == null) {
                skinViewList = ArrayList<SkinView>()
            }
            FancySkinManager.instance().addSkinView(this, skinViewList)
            skinViewList.add(SkinView(view, skinAttrList))

            if (FancySkinManager.instance().needChangeSkin()) {
                FancySkinManager.instance().applySkin(this)
            }
        }
    }

    private fun createViewFromTag(context: Context, name: String, attrs: AttributeSet?): View? {
        var viewName = name
        if (viewName.equals("view")) {
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


    val sConstructorMap = ArrayMap<String, Constructor<out View>>()
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


    override fun onDestroy() {
        super.onDestroy()
        FancySkinManager.instance().removeSkinChangedListener(this)
    }

    override fun onSkinChanged() {
        FancySkinManager.instance().applySkin(this)
        changeStatusColor()
    }


    private fun changeStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val color = FancySkinManager.instance().getColorPrimaryDark()
            if (color != -1) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = FancySkinManager.instance().getColorPrimaryDark()
            }
        }
    }
}