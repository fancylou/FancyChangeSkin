package net.muliba.changeskin

import android.support.v4.app.Fragment

/**
 * Created by fancyLou on 2017/11/8.
 * Copyright © 2017 O2. All rights reserved.
 */

open class FancySkinFragment: Fragment() {

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity is FancySkinActivity) {
            if (view!=null) {
                (activity as FancySkinActivity).fancySkinLayoutInflater().removeSkinView(view!!)
            }
        }
    }
}