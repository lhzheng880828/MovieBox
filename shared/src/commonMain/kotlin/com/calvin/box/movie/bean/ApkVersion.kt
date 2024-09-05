package com.calvin.box.movie.bean

import kotlinx.serialization.Serializable

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/9/5
 */
@Serializable
data class ApkVersion(val name:String="", val code:Int=0, val desc:String="")
