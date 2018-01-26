package net.muliba.changeskin

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ExploreByTouchHelper.INVALID_ID
import android.text.TextUtils
import net.muliba.changeskin.callback.DefaultPluginSkinChangingListener
import net.muliba.changeskin.callback.PluginSkinChangingListener
import net.muliba.changeskin.callback.SkinChangedListener
import net.muliba.changeskin.data.BackgroundSkinAttr
import net.muliba.changeskin.data.BaseSkinAttr
import net.muliba.changeskin.data.SrcSkinAttr
import net.muliba.changeskin.data.TextColorSkinAttr
import net.muliba.changeskin.util.PreferenceUtil
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File


/**
 * Created by fancylou on 10/25/17.
 */

class FancySkinManager private constructor() {

    companion object {
        private var INSTANCE: FancySkinManager? = null
        fun instance(): FancySkinManager {
            if (INSTANCE == null) {
                INSTANCE = FancySkinManager()
            }
            return INSTANCE!!
        }
    }

    private lateinit var mContext: Context
    private var mResources: Resources? = null
    private var mResourceManager: ResourceManager? = null
    private var usePlugin: Boolean = false
    private val mSkinChangedMaps = HashMap<Activity, SkinChangedListener>()
    private var needChangeStatusBarColor = true
    private val mSupportSkinAttrs = HashMap<String, BaseSkinAttr>()
    /**
     * 当前换肤后的资源数据
     */
    private var mCurrentSkinPackageName = ""
    private var mCurrentSkinPath = ""
    private var mCurrentSkinSuffix = ""


    fun withoutActivity(application: Application): FancySkinManager {
        init(application)
        FancySkinActivityLifeCycle.instance(application)
        return instance()
    }

    /**
     * 必须在Application onCreate 中初始化
     * @param changeStatusBarColor 换肤的时候是否同时替换状态栏的背景色  默认状态栏背景色是colorPrimaryDark  如果皮肤有suffix 就是colorPrimaryDark_suffix
     */
    fun init(context: Context, changeStatusBarColor: Boolean = true): FancySkinManager {
        mContext = context
        needChangeStatusBarColor = changeStatusBarColor
        //默认支持的换肤属性
        mSupportSkinAttrs.put(FancySkin.SUPPORT_SKIN_ATTR_BACKGROUND, BackgroundSkinAttr())
        mSupportSkinAttrs.put(FancySkin.SUPPORT_SKIN_ATTR_SRC, SrcSkinAttr())
        mSupportSkinAttrs.put(FancySkin.SUPPORT_SKIN_ATTR_TEXTCOLOR, TextColorSkinAttr())


        //如果有SkinPlugin 要初始化
        val prefSkinPath = PreferenceUtil.getPluginPath(mContext)
        val prefSkinPackageName = PreferenceUtil.getPluginPackName(mContext)
        mCurrentSkinSuffix = PreferenceUtil.getPluginSuffix(mContext)

        //默认本地资源
        mResourceManager = ResourceManager(mContext, mContext.resources!!, mContext.packageName!!, mCurrentSkinSuffix)

        if (TextUtils.isEmpty(prefSkinPath)) {
            return instance()
        }
        val skinPluginFile = File(prefSkinPath)
        if (!skinPluginFile.exists()) {
            return instance()
        }
        try {
            loadSkinPlugin(prefSkinPath, prefSkinPackageName, mCurrentSkinSuffix)
            mCurrentSkinPath = prefSkinPath
            mCurrentSkinPackageName = prefSkinPackageName
        } catch (e: Exception) {
            PreferenceUtil.clearPluginSkin(mContext)
        }
        return instance()
    }

    fun currentSkinSuffix() :String = mCurrentSkinSuffix
    fun currentSkinPath():String = mCurrentSkinPath
    fun currentSkinPackageName():String = mCurrentSkinPackageName

    /**
     * 添加扩展 支持换肤的属性
     */
    fun addSupportAttr(attrName:String, attr:BaseSkinAttr): FancySkinManager {
        mSupportSkinAttrs.put(attrName, attr)
        return instance()
    }

    /**
     * 当前属性是否支持换肤
     */
    fun isSupportAttrType(attrName: String):Boolean {
        return mSupportSkinAttrs.containsKey(attrName)
    }

    /**
     * 生成一个属性对象
     */
    fun createSupportAttr(attrName: String, originResId: Int, resName: String): BaseSkinAttr? {
        val attr = mSupportSkinAttrs[attrName]?: return null
        return attr.copy(attrName, originResId, resName)
    }


    /**
     * 应用内部换肤
     * @param suffix 资源后缀
     */
    fun changeSkinInner(suffix: String) {
        //清理当前的插件皮肤
        cleanPluginSkin()
        mCurrentSkinSuffix = suffix
        PreferenceUtil.putPluginSuffix(mContext, suffix)
        mResourceManager = ResourceManager(mContext, mContext.resources!!, mContext.packageName!!, mCurrentSkinSuffix)
        notifySkinChanged()
    }


    /**
     * 使用插件换肤
     * @param skinPath 插件地址，apk地址
     * @param skinPackageName 插件包名
     * @param suffix 资源后缀，默认为空
     */
    fun changeSkin(skinPath: String, skinPackageName: String, suffix: String = "", callback: PluginSkinChangingListener = DefaultPluginSkinChangingListener()) {
        callback.onStart()
        checkPluginParamsThrow(skinPath, skinPackageName)
        if (skinPath == mCurrentSkinPath && skinPackageName == mCurrentSkinPackageName) {
            return
        }
        /**
         * 后台线程加载 apk皮肤资源插件包
         */
        doAsync {
            try {
                loadSkinPlugin(skinPath, skinPackageName, suffix)
            }catch (e: Exception) {
                callback.onError(e)
            }
            uiThread {
                try {
                    updatePluginInfo(skinPath, skinPackageName, suffix)
                    notifySkinChanged()
                    callback.onCompleted()
                }catch (e: Exception) {
                    callback.onError(e)
                }
            }
        }
    }

    fun resetDefaultSkin() {
        cleanPluginSkin()
        mResourceManager = ResourceManager(mContext, mContext.resources!!, mContext.packageName!!, mCurrentSkinSuffix)
        notifySkinChanged()
    }

    fun getColorPrimaryDark(): Int {
        if (needChangeStatusBarColor) {
            getResourceManager()?.let { manager ->
                return manager.getColor(getColorPrimaryDarkResId(), FancySkin.STATUS_BAR_BACKGROUND_COLOR_RESOURCE_NAME)
            }
        }
        return try{ContextCompat.getColor(mContext, getColorPrimaryDarkResId())}catch (e:Resources.NotFoundException){-1}
    }
    /**
     * 获取换肤后的资源 找不到就找默认的资源
     */
    fun getColor(context: Context, resId: Int): Int {
        val entryName = context.resources.getResourceEntryName(resId)
        return if (getResourceManager() == null) {
            -1
        }else {
            getResourceManager()!!.getColor(resId, entryName)
        }

    }

    /**
     * 获取换肤后的资源 找不到就找默认的资源
     */
    fun getDrawable(context: Context, resId: Int): Drawable? {
        val entryName = context.resources.getResourceEntryName(resId)
        return if (getResourceManager() == null) {
            null
        }else {
            getResourceManager()!!.getDrawable(resId, entryName)
        }
    }

    fun addSkinChangedListener(activity: Activity, listener: SkinChangedListener) {
        mSkinChangedMaps.put(activity, listener)
    }

    fun removeSkinChangedListener(activity: Activity) {
        mSkinChangedMaps.remove(activity)
    }

    fun getResourceManager(): ResourceManager? = mResourceManager

    private fun updatePluginInfo(skinPath: String, skinPackageName: String, suffix: String) {
        PreferenceUtil.putPluginPath(mContext, skinPath)
        PreferenceUtil.putPluginPackName(mContext, skinPackageName)
        PreferenceUtil.putPluginSuffix(mContext, suffix)
        mCurrentSkinPath = skinPath
        mCurrentSkinPackageName = skinPackageName
        mCurrentSkinSuffix = suffix
    }

    private fun checkPluginParamsThrow(skinPath: String, skinPackageName: String) {
        if (TextUtils.isEmpty(skinPath) || TextUtils.isEmpty(skinPackageName)) {
            throw IllegalArgumentException("skinPath or skinPackageName is null!")
        }
    }

    private fun notifySkinChanged() {
        mSkinChangedMaps.entries.forEach{ action ->
            action.value.onSkinChanged()
        }
    }

    private fun cleanPluginSkin() {
        mCurrentSkinPath = ""
        mCurrentSkinPackageName = ""
        mCurrentSkinSuffix = ""
        usePlugin = false
        PreferenceUtil.clearPluginSkin(mContext)
    }


    private val APPCOMPAT_COLOR_PRIMARY_ATTRS = intArrayOf(android.support.v7.appcompat.R.attr.colorPrimary)
    private val APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS = intArrayOf(android.support.v7.appcompat.R.attr.colorPrimaryDark)
    private val APPCOMPAT_COLOR_ACCENT_ATTRS = intArrayOf(android.support.v7.appcompat.R.attr.colorAccent)

    private fun getColorPrimaryDarkResId():Int = getResId(APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS)
    private fun getResId(attrs: IntArray): Int {
        val a = mContext.obtainStyledAttributes(attrs)
        val resId = a?.getResourceId(0, INVALID_ID)
        a?.recycle()
        return resId?:0
    }

    /**
     * 加载皮肤包
     * @param prefSkinPath 皮肤包存放路径
     * @param prefSkinPackageName 皮肤包的packageName
     * @param suffix 皮肤包内的资源是否需要后后缀
     */
    private fun loadSkinPlugin(prefSkinPath: String, prefSkinPackageName: String, suffix: String) {
        val assetManager = AssetManager::class.java.newInstance()
        val addAssetPath = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
        addAssetPath.invoke(assetManager, prefSkinPath)

        val superRes = mContext.resources
        mResources = Resources(assetManager, superRes?.displayMetrics, superRes?.configuration)
        mResourceManager = ResourceManager(mContext, mResources!!, prefSkinPackageName, suffix)
        usePlugin = true
    }

    fun needChangeSkin(): Boolean  = usePlugin || !TextUtils.isEmpty(mCurrentSkinSuffix)


}