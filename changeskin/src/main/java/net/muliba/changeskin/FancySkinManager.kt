package net.muliba.changeskin

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.text.TextUtils
import android.util.Log
import net.muliba.changeskin.callback.DefaultPluginSkinChangingListener
import net.muliba.changeskin.callback.PluginSkinChangingListener
import net.muliba.changeskin.callback.SkinChangedListener
import net.muliba.changeskin.data.SkinView
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

    private val TAG = "FancySkinManager"
    private var mContext: Context? = null
    private var mResources: Resources? = null
    private var mResourceManager: ResourceManager? = null
    private var usePlugin: Boolean = false
    private val mSkinChangedListeners = ArrayList<SkinChangedListener>()
    private val mSkinViewMaps = HashMap<SkinChangedListener, ArrayList<SkinView>>()
    private var needChangeStatusBarColor = true
    /**
     * 当前换肤后的资源数据
     */
    private var mCurrentSkinPackageName = ""
    private var mCurrentSkinPath = ""
    private var mCurrentSkinSuffix = ""


    /**
     * 必须在Application onCreate 中初始化
     * @param changeStatusBarColor 换肤的时候是否同时替换状态栏的背景色  默认状态栏背景色是colorPrimaryDark  如果皮肤有suffix 就是colorPrimaryDark_suffix
     */
    fun init(context: Context, changeStatusBarColor: Boolean = true) {
        mContext = context
        needChangeStatusBarColor = changeStatusBarColor
        //如果有SkinPlugin 要初始化
        val prefSkinPath = PreferenceUtil.getPluginPath(mContext!!)
        val prefSkinPackageName = PreferenceUtil.getPluginPackName(mContext!!)
        mCurrentSkinSuffix = PreferenceUtil.getPluginSuffix(mContext!!)
        if (TextUtils.isEmpty(prefSkinPath)) {
            return
        }
        val skinPluginFile = File(prefSkinPath)
        if (!skinPluginFile.exists()) {
            return
        }
        try {
            loadSkinPlugin(prefSkinPath, prefSkinPackageName, mCurrentSkinSuffix)
            mCurrentSkinPath = prefSkinPath
            mCurrentSkinPackageName = prefSkinPackageName
        } catch (e: Exception) {
            PreferenceUtil.clearPluginSkin(mContext!!)
            Log.e(TAG, "loadSkinPlugin error", e)
        }
    }

    /**
     * 应用内部换肤
     * @param suffix 资源后缀
     */
    fun changeSkinInner(suffix: String) {
        //清理当前的插件皮肤
        cleanPluginSkin()
        mCurrentSkinSuffix = suffix
        PreferenceUtil.putPluginSuffix(mContext!!, suffix)
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
        if (skinPath.equals(mCurrentSkinPath) && skinPackageName.equals(mCurrentSkinPackageName)) {
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

    fun cleanSkin() {
        cleanPluginSkin()
        notifySkinChanged()
    }

    fun getColorPrimaryDark(): Int {
        if (needChangeStatusBarColor) {
            getResourceManager()?.let { manager ->
                return manager.getColor(FancySkin.STATUS_BAR_BACKGROUND_COLOR_RESOURCE_NAME)
            }
        }
        return -1
    }

    /**
     * 加载皮肤
     */
    fun applySkin(listener: SkinChangedListener) {
        getSkinViews(listener)?.map { skinView ->
            skinView.apply()
        }
    }


    fun addSkinChangedListener(listener: SkinChangedListener) {
        mSkinChangedListeners.add(listener)
    }
    fun addSkinView(listener: SkinChangedListener, skinViews: ArrayList<SkinView>) {
        mSkinViewMaps.put(listener, skinViews)
    }
    fun getSkinViews(listener: SkinChangedListener): ArrayList<SkinView>? {
        return mSkinViewMaps[listener]
    }
    fun removeSkinChangedListener(listener: SkinChangedListener) {
        mSkinChangedListeners.remove(listener)
        mSkinViewMaps.remove(listener)
    }

    fun getResourceManager(): ResourceManager? {
        if (!usePlugin) {
            mResourceManager = ResourceManager(mContext?.resources!!, mContext?.packageName!!, mCurrentSkinSuffix)
        }
        return mResourceManager
    }



    private fun updatePluginInfo(skinPath: String, skinPackageName: String, suffix: String) {
        PreferenceUtil.putPluginPath(mContext!!, skinPath)
        PreferenceUtil.putPluginPackName(mContext!!, skinPackageName)
        PreferenceUtil.putPluginSuffix(mContext!!, suffix)
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
        mSkinChangedListeners.forEach(SkinChangedListener::onSkinChanged)
    }

    private fun cleanPluginSkin() {
        mCurrentSkinPath = ""
        mCurrentSkinPackageName = ""
        mCurrentSkinSuffix = ""
        usePlugin = false
        PreferenceUtil.clearPluginSkin(mContext!!)
    }


    private fun loadSkinPlugin(prefSkinPath: String, prefSkinPackageName: String, suffix: String) {
        val assetManager = AssetManager::class.java.newInstance()
        val addAssetPath = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
        addAssetPath.invoke(assetManager, prefSkinPath)

        val superRes = mContext?.resources
        mResources = Resources(assetManager, superRes?.displayMetrics, superRes?.configuration)
        mResourceManager = ResourceManager( mResources!!, prefSkinPackageName, suffix)
        usePlugin = true
    }

    fun needChangeSkin(): Boolean  = usePlugin || !TextUtils.isEmpty(mCurrentSkinSuffix)


}