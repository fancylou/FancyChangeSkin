package net.muliba.changeskin.util

import android.content.Context
import android.content.SharedPreferences
import net.muliba.changeskin.FancySkin

/**
 * Created by fancylou on 10/25/17.
 */


object PreferenceUtil {
    fun pref(context: Context): SharedPreferences =
            context.getSharedPreferences(FancySkin.SHAREDPREFERENCE_FILE, Context.MODE_PRIVATE)

    fun clearPluginSkin(context: Context) {
        pref(context).edit{
            putString(FancySkin.PLUGIN_PATH_KEY, "")
            putString(FancySkin.PLUGIN_PACKAGE_NAME_KEY, "")
            putString(FancySkin.PLUGIN_SUFFIX_KEY, "")
        }
    }

    fun putPluginPath(context: Context, path:String) {
        pref(context).edit {
            putString(FancySkin.PLUGIN_PATH_KEY, path)
        }
    }
    fun getPluginPath(context: Context): String = pref(context).getString(FancySkin.PLUGIN_PATH_KEY, "") ?: ""

    fun putPluginPackName(context: Context, pkg:String) {
        pref(context).edit{
            putString(FancySkin.PLUGIN_PACKAGE_NAME_KEY, pkg)
        }
    }
    fun getPluginPackName(context: Context) : String = pref(context).getString(FancySkin.PLUGIN_PACKAGE_NAME_KEY, "") ?: ""

    fun putPluginSuffix(context: Context, suffix:String) {
        pref(context).edit {
            putString(FancySkin.PLUGIN_SUFFIX_KEY, suffix)
        }
    }
    fun getPluginSuffix(context: Context):String = pref(context).getString(FancySkin.PLUGIN_SUFFIX_KEY, "") ?: ""
}

/**
 * 扩展
 **/
inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.func()
    editor.apply()
}
