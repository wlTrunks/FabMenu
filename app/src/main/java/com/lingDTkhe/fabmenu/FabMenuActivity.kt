package com.lingDTkhe.fabmenu

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lingdtkhe.fabmenu.FabMenu
import kotlinx.android.synthetic.main.activity_fab_menu.*

class FabMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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
    }

    fun Activity.toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
