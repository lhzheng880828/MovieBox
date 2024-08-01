package com.calvin.box.movie.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Display
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.calvin.box.movie.ContextProvider

object ResUtil {
    val displayMetrics: DisplayMetrics
        get() = getContext().resources.displayMetrics

    fun getWindowManager(context: Context): WindowManager {
        return context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun hasNavigationBar(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val display: Display = getWindowManager(context).getDefaultDisplay()
            val size = Point()
            val realSize = Point()
            display.getSize(size)
            display.getRealSize(realSize)
            return realSize.x != size.x || realSize.y != size.y
        } else {
            val menu = ViewConfiguration.get(context).hasPermanentMenuKey()
            val back: Boolean = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            return !(menu || back)
        }
    }

    fun getNavigationBarHeight(context: Context): Int {
        if (!hasNavigationBar(context)) return 0
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    val screenWidth: Int
        get() = displayMetrics.widthPixels

    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    val screenWidthNav: Int
        get() = displayMetrics.widthPixels + getNavigationBarHeight(getContext() as Context)

    val screenHeight: Int
        get() = displayMetrics.heightPixels

    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    val screenHeightNav: Int
        get() = displayMetrics.heightPixels + getNavigationBarHeight(getContext() as Context)

    fun isEdge(e: MotionEvent, edge: Int): Boolean {
        return e.getRawX() < edge || e.getRawX() > screenWidthNav - edge || e.getRawY() < edge || e.getRawY() > screenHeightNav - edge
    }

    fun isLand(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    val isPad: Boolean
        get() = (getContext() as Context).getResources().getConfiguration().smallestScreenWidthDp >= 600

    fun sp2px(sp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), displayMetrics)
            .toInt()
    }

    fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics)
            .toInt()
    }

    fun getDrawable(resId: String?): Int {
        return getContext().resources.getIdentifier(resId, "drawable", getContext().getPackageName())
    }

    fun getString(@StringRes resId: Int): String {
        return getContext().resources.getString(resId)
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return getContext().resources.getString(resId, formatArgs)
    }

    fun getStringArray(@ArrayRes resId: Int): Array<String> {
        return getContext().resources.getStringArray(resId)
    }

    fun getTypedArray(@ArrayRes resId: Int): TypedArray {
        return getContext().resources.obtainTypedArray(resId)
    }

    fun getDrawable(@DrawableRes resId: Int): Drawable? {
        return ContextCompat.getDrawable(getContext(), resId)
    }

    fun getAnim(@AnimRes resId: Int): Animation {
        return AnimationUtils.loadAnimation(getContext(), resId)
    }

    fun getDisplay(activity: Activity): Display? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) activity.display else activity.windowManager.defaultDisplay
    }

    fun getTextWidth(content: String?, size: Int): Int {
        val paint = Paint()
        paint.textSize = sp2px(size).toFloat()
        return paint.measureText(content).toInt()
    }
    
    private fun getContext():Context{
       return ContextProvider.context as Context
    }
}
