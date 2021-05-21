package com.lingdtkhe.fabmenu

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.layout_fabmenu.view.*

/**
 * Created by Ling Dam Tkhe https://github.com/wlTrunks
 */
class FabMenu @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyles: Int = 0
) : ConstraintLayout(context, attrs, defStyles) {

    private var parentView: View
    private var spaceBetweenItem = 0
    private var menuBackgroundColor: Int = Color.WHITE
    private var menuImageTint: Int = Color.WHITE
    private var menuHorizontalBias = 1.0f

    private var subMenuImageSize: Int = 0
    private var subMenuTextSize: Float = 0f

    @ColorRes
    private var subMenuTextColor: Int = android.R.color.white

    private var expandIcon: Drawable? = null
    private var collapseIcon: Drawable? = null

    private var speedAnimation = DEFAULT_ANIMATION_DURATION

    private var isSubItemShow = false
    private var isMenuClickable = true

    private var subMenuItems = arrayListOf<ExtendedFloatingActionButton>()

    /**
     * Prevent main fab button clickable until the end of animation
     */
    private val animationListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {
            isMenuClickable = true
        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationStart(p0: Animator?) {
            isMenuClickable = false
        }
    }

    init {
        parentView = LayoutInflater.from(context).inflate(R.layout.layout_fabmenu, this, true)
        val a = context.obtainStyledAttributes(attrs, R.styleable.FabMenu, defStyles, 0)
        spaceBetweenItem =
            a.getDimension(R.styleable.FabMenu_item_space, DEFAULT_ITEM_SPACE).toInt()

        if (a.hasValue(R.styleable.FabMenu_expand_icon)) {
            expandIcon = a.getDrawable(R.styleable.FabMenu_expand_icon)
            expandIcon?.callback = this
        }

        if (a.hasValue(R.styleable.FabMenu_collapse_icon)) {
            collapseIcon = a.getDrawable(R.styleable.FabMenu_collapse_icon)
            collapseIcon?.callback = this
        }
        menuImageTint = a.getColor(R.styleable.FabMenu_menu_image_tint, Color.WHITE)
        menuHorizontalBias = a.getFloat(R.styleable.FabMenu_menu_horizontal_bias, 1.0f)
        menuBackgroundColor = a.getColor(R.styleable.FabMenu_menu_background_color, Color.WHITE)

        subMenuTextColor =
            a.getResourceId(R.styleable.FabMenu_submenu_text_color, android.R.color.white)
        subMenuImageSize =
            a.getDimensionPixelSize(
                R.styleable.FabMenu_submenu_image_size,
                resources.getDimension(R.dimen.submenu_size).toInt()
            )
        subMenuTextSize = a.getDimension(
            R.styleable.FabMenu_submenu_text_size,
            resources.getDimension(R.dimen.submenu_text_size)
        )
        a.recycle()

        setupMenuButton()
    }

    /**
     * Set duration of the animation for the show and hide submenus items
     * @param duration millisecond
     */
    fun setDuration(duration: Long) {
        speedAnimation = duration
    }

    /**
     * Add submenus
     * @param resId icon for submenu item
     * @param stringRes title for submenu
     * @param backgroundColor customize background color
     * @param clickListener callback on submenu clicked
     */
    fun addItem(
        @DrawableRes resId: Int,
        @StringRes stringRes: Int,
        backgroundColor: Int = android.R.color.holo_blue_dark,
        clickListener: OnClickListener
    ) {
        val fab = with(ExtendedFloatingActionButton(context)) {
            id = View.generateViewId()
            parentView.constraintLayout.addView(this)
            textSize = subMenuTextSize / resources.displayMetrics.density
            ContextCompat.getColorStateList(context, subMenuTextColor)?.let {
                setTextColor(it)
            }
            iconTint = ContextCompat.getColorStateList(context, android.R.color.white)
            setIconResource(resId)
            iconSize = subMenuImageSize
            setText(stringRes)
            supportBackgroundTintList =
                ContextCompat.getColorStateList(context, backgroundColor)
            setOnClickListener(clickListener)

            subMenuItems.add(this)
            visibility = View.GONE
            shrink()
            this
        }
        setLayoutParamsSubmenu(fab)
    }

    fun hideMenu() {
        isSubItemShow = false
        parentView.menuFab.setImageDrawable(expandIcon)
        animateHideItems()
    }

    fun showMenu() {
        isSubItemShow = true
        parentView.menuFab.setImageDrawable(collapseIcon)
        animateShowItems()
    }

    /**
     * Set click listeners to submenus item
     * @param listener interface listener [FabMenu.OnItemClickListener]
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        for (i in 0 until subMenuItems.size) {
            subMenuItems[(subMenuItems.size - 1) - i].setOnClickListener {
                hideMenu()
                listener.onItemClick(i)
            }
        }
    }

    /**
     * Returns submenus item
     * @param position position in submenu
     * @return ExtendedFloatingActionButton of submenu or null
     */
    fun getSubMenu(position: Int): ExtendedFloatingActionButton? =
        if (subMenuItems.isEmpty() || position >= subMenuItems.size) null
        else
            subMenuItems[position]

    /**
     * Returns main fab
     * @return FloatingActionButton
     */
    fun getMainMenu(): FloatingActionButton = parentView.menuFab

    fun setHorizontalBias(bias: Float) {
        menuHorizontalBias = bias
        setFabMenuHorizontalBias()
        subMenuItems.forEach { setLayoutParamsSubmenu(it) }
    }

    fun getMenuHorizontalBias(): Float = menuHorizontalBias

    private fun setLayoutParamsSubmenu(fab: ExtendedFloatingActionButton) {
        val set = ConstraintSet()
        set.clone(constraintLayout)
        set.clear(fab.id)
        val margin = resources.getDimension(R.dimen.margin) / resources.displayMetrics.density
        set.connect(fab.id, ConstraintSet.TOP, menuFab.id, ConstraintSet.TOP, 0)
        when {
            menuHorizontalBias > 0.5f -> {
                set.connect(
                    fab.id,
                    ConstraintSet.END,
                    menuFab.id,
                    ConstraintSet.END,
                    margin.toInt() / 2
                )
            }
            menuHorizontalBias < 0.5f -> {
                set.connect(
                    fab.id,
                    ConstraintSet.START,
                    menuFab.id,
                    ConstraintSet.START,
                    margin.toInt() / 2
                )
            }
            else -> {
                set.connect(fab.id, ConstraintSet.START, menuFab.id, ConstraintSet.START, 0)
                set.connect(fab.id, ConstraintSet.END, menuFab.id, ConstraintSet.END, 0)
            }
        }
        set.applyTo(constraintLayout)
    }

    /**
     * Setup menu fab
     */
    private fun setupMenuButton() {
        with(parentView.menuFab) {
            val params = layoutParams as LayoutParams
            params.horizontalBias = menuHorizontalBias
            layoutParams = params
            supportBackgroundTintList = ColorStateList.valueOf(menuBackgroundColor)
            supportImageTintList = ColorStateList.valueOf(menuImageTint)
            setImageDrawable(expandIcon)
            setOnClickListener {
                if (!isMenuClickable) {
                    return@setOnClickListener
                }
                if (!isSubItemShow) {
                    showMenu()
                } else {
                    hideMenu()
                }
            }
        }
    }

    private fun setFabMenuHorizontalBias() {
        with(parentView.menuFab) {
            val params = layoutParams as ConstraintLayout.LayoutParams
            params.horizontalBias = menuHorizontalBias
            layoutParams = params
        }
    }

    /**
     * Animation of show submenus items
     */
    private fun animateShowItems() {
        var itemSize =
            resources.getDimensionPixelSize(com.google.android.material.R.dimen.design_fab_size_mini)
        itemSize += spaceBetweenItem
        val animatorSet = AnimatorSet()
        var animationBuilder: AnimatorSet.Builder? = null
        for (i in 0 until subMenuItems.size) {
            val item = subMenuItems[(subMenuItems.size - 1) - i]
            val itemAnimation = setItemShowAnimation(item, -(itemSize.toFloat() * (i + 1)))
            if (i != 0) {
                itemAnimation.startDelay = (speedAnimation / 3) * i
            }
            animationBuilder =
                animationBuilder?.run { with(itemAnimation) } ?: animatorSet.play(itemAnimation)
            itemAnimation.addListener(SubmenuShowAnimationListener(item))
        }
        animatorSet.addListener(animationListener)
        animatorSet.start()
    }

    /**
     * Animation of hide submenus items
     */
    private fun animateHideItems() {
        var itemSize =
            resources.getDimensionPixelSize(com.google.android.material.R.dimen.design_fab_size_mini)

        itemSize += spaceBetweenItem
        val animatorSet = AnimatorSet()
        var animationBuilder: AnimatorSet.Builder? = null
        for (i in 0 until subMenuItems.size) {
            val item = subMenuItems[i]
            val itemAnimation = setItemHideAnimation(item, itemSize.toFloat())
            itemAnimation.addListener(SubmenuHideAnimationListener(item))
            if (i != 0) {
                itemAnimation.startDelay = (speedAnimation / 3) * i
            }
            animationBuilder =
                animationBuilder?.run { with(itemAnimation) } ?: animatorSet.play(itemAnimation)
        }
        animatorSet.addListener(animationListener)
        animatorSet.start()
    }

    /**
     * Set animation show to view
     * @param target view to hide
     * @param translationY value to move vertically
     * @return Return Animator set for custom settings
     */
    private fun setItemShowAnimation(target: View, translationY: Float): AnimatorSet =
        AnimatorSet().also {
            it.play(provideObjectAnimatorTranslationY(target, translationY))
                .with(provideObjectAnimatorScale(target, SCALE_X, 1.0f))
                .with(provideObjectAnimatorScale(target, SCALE_Y, 1.0f))
                .with(provideObjectAnimatorAlpha(target, 0f, 1f))
        }

    /**
     * Set animation hide to view
     * @param target view to hide
     * @param translationY value to move vertically
     * @return Return Animator set for custom settings
     */
    private fun setItemHideAnimation(target: View, translationY: Float): AnimatorSet =
        AnimatorSet().also {
            it.play(provideObjectAnimatorTranslationY(target, translationY))
                .with(provideObjectAnimatorAlpha(target, 1f, 0f))
                .with(provideObjectAnimatorScale(target, SCALE_X, 0.0f))
                .with(provideObjectAnimatorScale(target, SCALE_Y, 0.0f))
        }

    /**
     * Provide ObjectAnimator for the hide and the show animation translation submenus
     * @param target submenu item ExtendedFloatingActionButton
     * @param values values that to animate vertically
     */
    private fun provideObjectAnimatorTranslationY(
        target: View,
        values: Float
    ): ObjectAnimator = ObjectAnimator.ofFloat(
        target,
        TRANSLATION_Y,
        values
    ).apply {
        duration = speedAnimation
    }

    /**
     * Provide ObjectAnimator for the hide and the show animation scale submenus
     * @param target submenu item ExtendedFloatingActionButton
     * @param propertyName [FabMenu.SCALE_X] or [FabMenu.SCALE_Y]
     * @param values values that to animate
     */
    private fun provideObjectAnimatorScale(
        target: View,
        propertyName: String,
        values: Float
    ): ObjectAnimator =
        ObjectAnimator.ofFloat(target, propertyName, values)
            .apply {
                duration = speedAnimation
            }

    /**
     * Provide ObjectAnimator for the hide and the show animation alpha submenus
     * @param target submenu item ExtendedFloatingActionButton
     * @param from set alpha from value
     * @param to set alha to value
     */
    private fun provideObjectAnimatorAlpha(target: View, from: Float, to: Float): ObjectAnimator =
        ObjectAnimator.ofFloat(target, ALPHA, from, to)
            .apply {
                duration = speedAnimation
            }

    /**
     * Implementation animation hide listener
     */
    class SubmenuHideAnimationListener(private val view: ExtendedFloatingActionButton) :
        Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            view.visibility = View.GONE
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
            view.shrink()
        }
    }

    /**
     * Implementation animation show listener
     */
    class SubmenuShowAnimationListener(private val view: ExtendedFloatingActionButton) :
        Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            view.extend()
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
            view.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val SCALE_X = "scaleX"
        private const val SCALE_Y = "scaleY"
        private const val ALPHA = "alpha"
        private const val TRANSLATION_Y = "translationY"

        const val DEFAULT_ITEM_SPACE = 20f
        const val DEFAULT_ANIMATION_DURATION = 200L
    }

    /**
     * Fab menu item click listener by position
     */
    interface OnItemClickListener {
        /**
         * Sub menu item click
         * @param position position in the list of submenus
         */
        fun onItemClick(position: Int)
    }
}