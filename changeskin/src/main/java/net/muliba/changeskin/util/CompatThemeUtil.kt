package net.muliba.changeskin.util

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi

/**
 * Created by fancyLou on 2017/11/6.
 * Github@fancylou
 * email:FancyLou@outlook.com
 */

object CompatThemeUtil {

    fun getResId(context:Context, attrs:IntArray) : Int {
        var resId = 0
        val typeArray = context.obtainStyledAttributes(attrs)
        if (typeArray!=null) {
            resId = typeArray.getResourceId(0, 0)
            typeArray.recycle()
        }
        return resId
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun getStatusBarColorResId(context: Context): Int =
        getResId(context, intArrayOf(android.R.attr.statusBarColor))

    fun getColorPrimaryResId(context: Context): Int =
            getResId(context, intArrayOf(android.support.v7.appcompat.R.attr.colorPrimary))

    fun getColorPrimaryDarkResId(context: Context): Int =
            getResId(context, intArrayOf(android.support.v7.appcompat.R.attr.colorPrimaryDark))

    fun getColorAccentResId(context: Context): Int =
            getResId(context, intArrayOf(android.support.v7.appcompat.R.attr.colorAccent))

    fun getTextColorPrimaryResId(context: Context): Int =
            getResId(context, intArrayOf(android.R.attr.textColorPrimary))

    fun getWindowBackgroundResId(context: Context): Int =
            getResId(context, intArrayOf(android.R.attr.windowBackground))
}