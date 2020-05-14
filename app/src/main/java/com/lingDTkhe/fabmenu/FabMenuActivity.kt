package com.lingDTkhe.fabmenu

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lingdtkhe.fabmenu.FabMenu
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class FabMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        changeLocale()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fab_menu)
        /**
         * Simple example how to use
         */
        val fabMenu = findViewById<FabMenu>(R.id.fabMenu)
        fabMenu.addItem(
            R.drawable.ic_fab_add,
            R.string.submenu_1,
            R.color.submenu_background_color1,
            View.OnClickListener { toast("1") }
        )
        fabMenu.addItem(
            R.drawable.ic_fab_add,
            R.string.submenu_2,
            R.color.submenu_background_color2,
            View.OnClickListener { /* action */ }
        )
        fabMenu.addItem(
            R.drawable.ic_fab_add,
            R.string.submenu_3,
            R.color.submenu_background_color3,
            View.OnClickListener { toast("3") }
        )
        val timer = Timer()
        var addBias = 0.1f
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                     if (fabMenu.getMenuHorizontalBias() >= 1f) {
                        addBias = -(addBias)
                    }
                    if (fabMenu.getMenuHorizontalBias() <= 0f) {
                        addBias = +(addBias)
                    }
                    println("addBias $addBias")
                    println("getMenuHorizontalBias ${fabMenu.getMenuHorizontalBias()}")
                    fabMenu.setHorizontalBias(fabMenu.getMenuHorizontalBias()+addBias)
                }
            }
        }, 3000, 2000)
    }

    fun Activity.toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    /**
     * Make more secure.Separate storage change "storage" to "config_storage"
     */
    fun Context.changeLocale(): Context {
        val lang = "he"
        val res = resources
        val configuration = res.configuration
        val newLocale = Locale(lang)
        var context: Context = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            configuration.setLocale(newLocale)
            val localeList = LocaleList(newLocale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            context = createConfigurationContext(configuration)
            configuration.setLocale(newLocale)
        } else {
            configuration.setLocale(newLocale)
            res.updateConfiguration(configuration, res.displayMetrics)
        }
        return context
    }
}
