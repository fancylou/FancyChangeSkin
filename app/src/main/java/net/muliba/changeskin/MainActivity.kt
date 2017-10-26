package net.muliba.changeskin

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import net.muliba.changeskin.callback.PluginSkinChangingListener
import java.io.File

class MainActivity : FancySkinActivity() {

    val mDatas = arrayOf("Activity", "Service", "Activity", "Service", "Activity",
            "Activity", "Service", "Activity", "Service", "Activity",
            "Activity", "Service", "Activity", "Service", "Activity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.title = "测试皮肤"
        setSupportActionBar(toolbar)

        id_listview.adapter = object : ArrayAdapter<String>(this, -1, mDatas) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                var itemView = convertView
                if (itemView == null) {
                    itemView = LayoutInflater.from(this@MainActivity).inflate(R.layout.item_list, parent, false)
                }
                itemView!!
                val t = itemView.findViewById<TextView>(R.id.item_tv)
                t.text = getItem(position)+"_$position"
                return itemView
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_inner_red -> {
                FancySkinManager.instance().changeSkinInner("red")
            }
            R.id.menu_clear -> {
                FancySkinManager.instance().cleanSkin()
            }
            R.id.menu_out -> {
                val filePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "app-release.apk"
                FancySkinManager.instance().changeSkin(skinPath = filePath, skinPackageName = "net.muliba.pluginskinpurple", callback = object : PluginSkinChangingListener {
                    override fun onStart() {
                        Log.e("MainActivity", "onStart.............")
                    }

                    override fun onError(e: Exception) {
                        Log.e("MainActivity", "onError.............", e)
                    }

                    override fun onCompleted() {
                        Log.e("MainActivity", "onCompleted.............")
                    }
                })
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
