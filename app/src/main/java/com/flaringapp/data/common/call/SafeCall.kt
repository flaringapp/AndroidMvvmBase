package com.flaringapp.data.common.call

import com.flaringapp.app.common.withMainContext

suspend fun <D> safeCall(handler: SafeCallHandler, callResult: CallResult<D>): D? {
    return safeCall(handler) { callResult }
}

suspend fun <D> safeCall(
    handler: SafeCallHandler,
    enableLogging: Boolean = true,
    callResult: CallResult<D>
): D? {
    return safeCall(handler, enableLogging) { callResult }
}

suspend fun <D> safeCall(handler: SafeCallHandler, action: suspend () -> CallResult<D>): D? {
    return safeCall(handler, true, action)
}

suspend fun <D> safeCall(
    handler: SafeCallHandler,
    enableLogging: Boolean = true,
    action: suspend () -> CallResult<D>
): D? {
    return try {
        val result = action()
        processRequestResult(handler, result)
    } catch (e: Exception) {
        val isErrorHandled = withMainContext {
            handler.handleSafeCallError(e)
        }
        if (isErrorHandled) return null
        if (enableLogging) e.printStackTrace()
        handler.showErrorOnMain(e)
        null
    }
}

private suspend fun <D> processRequestResult(handler: SafeCallHandler, result: CallResult<D>): D? {
    if (result is CallResult.Error) {
        val isErrorHandled = withMainContext {
            handler.handleCallResultError(result)
        }
        if (isErrorHandled) return null
        handler.showErrorOnMain(result.exception)
        return null
    }

    return (result as CallResult.Success<D>).data
}

private suspend fun SafeCallHandler.showErrorOnMain(error: Throwable?) {
    if (error == null) return
    withMainContext {
        showError(error)
    }
}