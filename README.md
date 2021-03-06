FloatingActionButtons Menu with ExtendedFloatingActionButton as submenus
___________________________________________________
Implementation of menu `FloatingActionButton`s from Design Support Library that follows [Material Design guidelines](https://material.io/guidelines/components/buttons-floating-action-button.html#buttons-floating-action-button-transitions)
and  `ExtendedFloatingActionButton` as submenus  [Extended Floating Action Button](https://material.io/develop/android/components/extended-floating-action-button/)
___________________________________________________

# Preview
Last Version 0.0.3

![FABs Menu Preview]<img src="https://github.com/wlTrunks/FabMenu/raw/master/preview/fab_menu.gif" width="240px" height="400px" />

___________________________________________________

# Dependencies
*   "com.google.android.material:material:1.4.0-beta01"
*   "androidx.constraintlayout:constraintlayout:2.1.0-beta02"

## How to implement
*   implementation 'com.lingdtkhe.fabmenu:fabmenu:0.0.+'


```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#301B1B"
    tools:context=".FabMenuActivity">

    <com.lingdtkhe.fabmenu.FabMenu
        android:id="@+id/fabMenu"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        app:collapse_icon="@drawable/ic_fab_close"
        app:expand_icon="@drawable/ic_fab_add"
        app:item_space="@dimen/space_item"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:menu_background_color="@color/colorAccent"
        app:menu_horizontal_bias="1"
        app:menu_image_tint="@android:color/white"
        app:submenu_text_color="@color/clickable_text"
        app:submenu_text_size="10sp" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

And call the methods in code:
```
        val fabMenu = findViewById<FabMenu>(R.id.fabMenu)

        fabMenu.addItem(
            R.drawable.ic_fab_add,
            R.string.submenu_1,
            R.color.submenu_background_color1,
            View.OnClickListener { /* action */ }
        )
```
___________________________________________________
## Attributes explanation

FAB attributes:
* `collapse_icon` --> Set icon when menu fab collapsed
* `expand_icon` --> Set icon when menu fab expanded
* `menu_background_color` --> Set FAB backgroundColor
* `menu_horizontal_bias` --> Set horizontal bias of FabMenu 1.0f to 0.0f
* `menu_image_tint` --> Set FabMenu icon color
* `submenu_text_color` --> Set Submenus text color
* `submenu_text_size` --> Set Submenus text size
* `submenu_image_size` --> Set Submenus icon size
