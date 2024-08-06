package com.calvin.box.movie

import android.content.Context
import com.calvin.box.movie.bean.Style
import com.calvin.box.movie.utils.ResUtil
import kotlin.math.abs

object Product {
    val deviceType: Int
        get() = 1

    fun getColumn(context:Context): Int {
        var count = if (ResUtil.isLand(context)) 7 else 5
        count += (if (ResUtil.isPad) 1 else 0)
        return abs(Setting.getSize() - count)
    }

    fun getColumn(context: Context, style: Style): Int {
        return if (style.isLand()) getColumn(context) - 1 else getColumn(context)
    }

    fun getSpec(context: Context): IntArray {
        return getSpec(context, Style.rect())
    }

    fun getSpec(context:Context, style: Style): IntArray {
        val column = getColumn(context, style)
        var space: Int = ResUtil.dp2px(32) + ResUtil.dp2px(16 * (column - 1))
        if (style.isOval()) space += ResUtil.dp2px(column * 16)
        return getSpec(context, space, column, style)
    }

    fun getSpec(context:Context, space: Int, column: Int): IntArray {
        return getSpec(context, space, column, Style.rect())
    }

    private fun getSpec(
        context:Context,
        space: Int,
        column: Int,
        style: Style
    ): IntArray {
        val base: Int = ResUtil.getScreenWidth() - space
        val width = base / column
        val height = (width / style.getRatio()).toInt()
        return intArrayOf(width, height)
    }

    val ems: Int
        get() = (ResUtil.getScreenWidth() / ResUtil.sp2px(20)).coerceAtMost(25)
}
