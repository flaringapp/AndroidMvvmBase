package com.flaringapp.data.network.modifiers

import com.flaringapp.data.network.modifiers.modifier.RequestModifier
import okhttp3.Request

class RequestDataCache {

    private val dataMap: MutableMap<Int, RequestModifier> = mutableMapOf()

    fun storeRequestModifier(request: Request, data: RequestModifier) {
        dataMap[identifyRequest(request)] = data
    }

    fun resolveRequestModifier(request: Request): RequestModifier? {
        return dataMap[identifyRequest(request)]
    }

    private fun identifyRequest(request: Request): Int {
        return (request.url.toString() + request.method).hashCode()
    }

}