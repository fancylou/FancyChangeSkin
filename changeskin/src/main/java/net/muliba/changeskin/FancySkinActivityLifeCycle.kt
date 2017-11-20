package net.muliba.changeskin

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v7.app.AppCompatCallback
import android.support.v7.app.AppCompatDelegate
import android.support.v7.view.ActionMode

/**
 * Created by fancyLou on 2017/11/6.
 * Github@fancylou
 * email:FancyLou@outlook.com
 */

class FancySkinActivityLifeCycle : Application.ActivityLifecycleCallbacks, AppCompatCallback {

    companion object {
        private var INSTANCE: FancySkinActivityLifeCycle? = null
        fun instance(): FancySkinActivityLifeCycle {
            if (INSTANCE==null) {
                INSTANCE = FancySkinActivityLifeCycle()
            }
            return INSTANCE!!
        }
    }

    override fun onWindowStartingSupportActionMode(callback: ActionMode.Callback?): ActionMode?  = null
    override fun onSupportActionModeStarted(mode: ActionMode?) {
    }
    override fun onSupportActionModeFinished(mode: ActionMode?) {
    }

    lateinit private var delegate: AppCompatDelegate
    init {

    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        delegate = AppCompatDelegate.create(activity, this)
    }
    override fun onActivityStarted(activity: Activity?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onActivityResumed(activity: Activity?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onActivityPaused(activity: Activity?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onActivityStopped(activity: Activity?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onActivityDestroyed(activity: Activity?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}