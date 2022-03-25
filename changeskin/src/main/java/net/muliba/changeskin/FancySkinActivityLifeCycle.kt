package net.muliba.changeskin

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.core.view.LayoutInflaterCompat
import android.view.LayoutInflater
import android.view.WindowManager
import net.muliba.changeskin.appcompat.FancySkinLayoutInflaterFactory
import net.muliba.changeskin.callback.SkinChangedListener
import java.lang.Exception
import java.util.*


/**
 * Created by fancyLou on 2017/11/6.
 * Github@fancylou
 * email:FancyLou@outlook.com
 */

class FancySkinActivityLifeCycle : Application.ActivityLifecycleCallbacks {


    private val inflaterFactoryMap: WeakHashMap<Context, FancySkinLayoutInflaterFactory> = WeakHashMap()

    private constructor(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
        installLayoutFactory(application)
    }
    companion object {
        private var INSTANCE: FancySkinActivityLifeCycle? = null
        fun instance(application: Application): FancySkinActivityLifeCycle {
            if (INSTANCE==null) {
                INSTANCE = FancySkinActivityLifeCycle(application)
            }
            return INSTANCE!!
        }
    }



    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        installLayoutFactory(activity)
        changeStatusColor(activity)
        FancySkinManager.instance().addSkinChangedListener(activity, object : SkinChangedListener{
            override fun onSkinChanged() {
                getSkinLayoutInflaterFactory(activity).applySkin()
                changeStatusColor(activity)
            }
        })

    }
    override fun onActivityStarted(activity: Activity) {

    }
    override fun onActivityResumed(activity: Activity) {

    }
    override fun onActivityPaused(activity: Activity) {

    }
    override fun onActivityStopped(activity: Activity) {

    }
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {

    }
    override fun onActivityDestroyed(activity: Activity) {
        getSkinLayoutInflaterFactory(activity).clean()
        FancySkinManager.instance().removeSkinChangedListener(activity)
    }

    private fun installLayoutFactory(context: Context) {
        val layoutInflater = LayoutInflater.from(context)



        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            //反射
            try {
                val field = LayoutInflater::class.java.getDeclaredField("mFactorySet")
                field.isAccessible = true
                field.setBoolean(layoutInflater, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            LayoutInflaterCompat.setFactory2(layoutInflater, getSkinLayoutInflaterFactory(context))
        } else {
            try {
                val field = LayoutInflater::class.java.getDeclaredField("mFactory2")
                field.isAccessible = true
                field.set(layoutInflater, getSkinLayoutInflaterFactory(context))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
//
//        try {
//            val field = LayoutInflater::class.java.getDeclaredField("mFactorySet")
//            field.isAccessible = true
//            field.setBoolean(layoutInflater, false)
//            LayoutInflaterCompat.setFactory2(layoutInflater, getSkinLayoutInflaterFactory(context))
//        } catch (e: NoSuchFieldException) {
//            e.printStackTrace()
//        } catch (e: IllegalArgumentException) {
//            e.printStackTrace()
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        }

    }

    private fun getSkinLayoutInflaterFactory(context: Context): FancySkinLayoutInflaterFactory {
        if (inflaterFactoryMap.containsKey(context)){
            return inflaterFactoryMap[context]!!
        }
        val layoutInflater = FancySkinLayoutInflaterFactory(context)
        inflaterFactoryMap[context] = layoutInflater
        return layoutInflater
    }

    private fun changeStatusColor(activity: Activity?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val color = FancySkinManager.instance().getColorPrimaryDark()
            if (color != -1) {
                val window = activity?.window
                window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window?.statusBarColor = color
            }
        }
    }

}