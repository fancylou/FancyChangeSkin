package net.muliba.changeskin.data

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Created by fancylou on 10/25/17.
 */


data class SkinAttr(val attrType: SkinAttrType,val originResId: Int, val resName: String) {
    fun apply(view: View) {
        attrType.apply(view, originResId, resName)
    }
}

fun getSkinAttrs(attrs: AttributeSet?, context: Context?) : List<SkinAttr> {
    val skinAttrs = ArrayList<SkinAttr>()
    if (attrs == null) {
        return skinAttrs
    }
    for (index in 0 until attrs.attributeCount) {
        val attrName = attrs.getAttributeName(index)
        val attrValue = attrs.getAttributeValue(index)
        val skinType: SkinAttrType = getSupportAttrType(attrName) ?: continue
        if (attrValue.startsWith("@")) {
            try {
                val id = attrValue.substring(1).toInt()
                if (id == 0) {
                    continue
                }
                val entryName = context?.resources?.getResourceEntryName(id)
                if (entryName!=null) {
                    skinAttrs.add(SkinAttr(skinType, id, entryName))
                }
            }catch (e: Exception){}
        }
    }
    return skinAttrs
}

fun getSupportAttrType(attrName: String): SkinAttrType? {
    SkinAttrType.values().filter { type ->
        type.attrType == attrName
    }.map { return it }
    return null
}