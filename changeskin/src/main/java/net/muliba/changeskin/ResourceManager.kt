package net.muliba.changeskin

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.TextUtils

/**
 * Created by fancylou on 10/25/17.
 */


class ResourceManager(res:Resources, pkg:String, suffix:String) {

    private val DEFTYPE_DRAWABLE = "drawable"
    private val DEFTYPE_MIPMAP = "mipmap"
    private val DEFTYPE_COLOR = "color"

    private var mResources:Resources = res
    private var mSkinPackageName = pkg
    private var mSkinSuffix = if(TextUtils.isEmpty(suffix)) "" else suffix

    private fun appendSuffix(name:String) :String {
        var mName = name
        if (!TextUtils.isEmpty(mSkinSuffix)) {
            mName = "${mName}_$mSkinSuffix"
        }
        return mName
    }

    fun getDrawable(name: String): Drawable? {
        val trueName = appendSuffix(name)
        try {
            var id =mResources.getIdentifier(trueName, DEFTYPE_DRAWABLE, mSkinPackageName)
            if (id == 0) {
                id = mResources.getIdentifier(trueName, DEFTYPE_COLOR, mSkinPackageName)
            }
            if (id == 0) {
                id = mResources.getIdentifier(trueName, DEFTYPE_MIPMAP, mSkinPackageName)
            }
            return  mResources.getDrawable(id)

        }catch (e: Resources.NotFoundException) {
            return null
        }
    }

    fun getColor(name: String): Int {
        val trueName = appendSuffix(name)
        try {
            val id = mResources.getIdentifier(trueName, DEFTYPE_COLOR, mSkinPackageName)
            return mResources.getColor(id)
        }catch (e: Resources.NotFoundException) {
            return -1
        }
    }

    fun getColorStateList(name: String): ColorStateList? {
        val trueName = appendSuffix(name)
        try {
            val id = mResources.getIdentifier(trueName, DEFTYPE_COLOR, mSkinPackageName)
            return mResources.getColorStateList(id)
        }catch (e: Resources.NotFoundException) {
            return null
        }
    }
}