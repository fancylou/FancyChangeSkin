package net.muliba.changeskin

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.util.Log

/**
 * Created by fancylou on 10/25/17.
 */


class ResourceManager(private val mContext: Context, private val mResources: Resources, private val mSkinPackageName: String, private val mSkinSuffix: String = "") {

    private val DEFTYPE_DRAWABLE = "drawable"
    private val DEFTYPE_MIPMAP = "mipmap"
    private val DEFTYPE_COLOR = "color"


    private fun appendSuffix(name: String): String {
        var mName = name
        if (!TextUtils.isEmpty(mSkinSuffix)) {
            mName = "${mName}_$mSkinSuffix"
        }
        return mName
    }

    fun getColorResIdByName(name: String): Int {
        return try {
            val trueName = appendSuffix(name)
            mResources.getIdentifier(trueName, DEFTYPE_COLOR, mSkinPackageName)
        }catch (e: Resources.NotFoundException) {
            0
        }

    }

    fun getDrawable(originResId: Int, name: String): Drawable? {
        try {
            val originDrawable = ContextCompat.getDrawable(mContext, originResId)
            val trueName = appendSuffix(name)
            var id = mResources.getIdentifier(trueName, DEFTYPE_DRAWABLE, mSkinPackageName)
            if (name=="radio_background_primary") {
                Log.i("drawable", "drawable left id:$id")
            }
            if (id == 0) {
                id = mResources.getIdentifier(trueName, DEFTYPE_COLOR, mSkinPackageName)
            }
            if (id == 0) {
                id = mResources.getIdentifier(trueName, DEFTYPE_MIPMAP, mSkinPackageName)
            }
            if (id != 0) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mResources.getDrawable(id, null)
                } else {
                    mResources.getDrawable(id)
                }
            }
            return originDrawable
        } catch (e: Resources.NotFoundException) {
            return null
        }
    }


    fun getColor(originResId: Int, name: String): Int {
        try {
            val originColor = ContextCompat.getColor(mContext, originResId)
            val trueName = appendSuffix(name)
            val id = mResources.getIdentifier(trueName, DEFTYPE_COLOR, mSkinPackageName)
            if (id != 0) {
                return mResources.getColor(id)
            }
            return originColor
        } catch (e: Resources.NotFoundException) {
            return -1
        }
    }

    fun getColorStateList(originResId: Int, name: String): ColorStateList? {
        try {
            val originColor = ContextCompat.getColorStateList(mContext, originResId)
            val trueName = appendSuffix(name)
            val id = mResources.getIdentifier(trueName, DEFTYPE_COLOR, mSkinPackageName)
            if (id != 0) {
                return mResources.getColorStateList(id)
            }
            return originColor
        } catch (e: Resources.NotFoundException) {
            return null
        }
    }

    fun getColorByResId(resId: Int): Int =
            try {
                if (resId == 0) {
                    -1
                } else {
                    mResources.getColor(resId)
                }
            } catch (e: Resources.NotFoundException) {
                -1
            }
}