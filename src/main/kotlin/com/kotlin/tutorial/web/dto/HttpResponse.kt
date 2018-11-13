package com.kotlin.tutorial.web.dto

/**
 * Created by tony on 2018/11/13.
 */
class HttpResponse<T>(val code: Int, val message:String, val data:T) {

    constructor(data: T):this(200,"success",data)
}