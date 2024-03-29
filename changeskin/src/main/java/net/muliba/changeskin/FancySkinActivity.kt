package net.muliba.changeskin

import android.os.Build
import android.os.Bundle
import androidx.core.view.LayoutInflaterCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import net.muliba.changeskin.appcompat.FancySkinLayoutInflaterFactory
import net.muliba.changeskin.callback.SkinChangedListener


/**
 * Created by fancylou on 10/25/17.
 */

@Deprecated("不用继承这个Activity， 使用withoutActivity()")
open class FancySkinActivity : AppCompatActivity(), SkinChangedListener {

    private val layoutInflaterFactory: FancySkinLayoutInflaterFactory by lazy { FancySkinLayoutInflaterFactory(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory2(layoutInflater, layoutInflaterFactory)
        super.onCreate(savedInstanceState)
        changeStatusColor()
        FancySkinManager.instance().addSkinChangedListener(this, this)
    }


    override fun onDestroy() {
        super.onDestroy()
        layoutInflaterFactory.clean()
        FancySkinManager.instance().removeSkinChangedListener(this)
    }

    override fun onSkinChanged() {
        layoutInflaterFactory.applySkin()
        changeStatusColor()
    }

    fun fancySkinLayoutInflater(): FancySkinLayoutInflaterFactory {
        return layoutInflaterFactory
    }


    private fun changeStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val color = FancySkinManager.instance().getColorPrimaryDark()
            if (color != -1) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = color
            }
        }
    }
}