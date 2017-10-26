package net.muliba.changeskin.data

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import net.muliba.changeskin.FancySkin

/**
 * Created by fancylou on 10/25/17.
 */


data class SkinAttr(val attrType: SkinAttrType, val resName: String) {
    fun apply(view: View) {
        attrType.apply(view, resName)
    }
}

fun getSkinAttrs(attrs: AttributeSet?, context: Context) : List<SkinAttr> {
    val skinAttrs = ArrayList<SkinAttr>()
    if (attrs == null) {
        return skinAttrs
    }
    for (index in 0 until attrs.attributeCount) {
        val attrName = attrs.getAttributeName(index)
        val attrValue = attrs.getAttributeValue(index)
        val skinType: SkinAttrType = getSupportAttrType(attrName) ?: continue

        if (attrValue.startsWith("@")) {
            val id = attrValue.substring(1).toInt()
            val entryName = context.resources.getResourceEntryName(id)
            if (entryName.startsWith(FancySkin.SKIN_PREFFIX)) {
                skinAttrs.add(SkinAttr(skinType, entryName))
            }
        }
    }
    return skinAttrs
}

fun getSupportAttrType(attrName: String): SkinAttrType? {
    SkinAttrType.values().filter { type ->
        type.attrType.equals(attrName)
    }.map { return it }
    return null
}