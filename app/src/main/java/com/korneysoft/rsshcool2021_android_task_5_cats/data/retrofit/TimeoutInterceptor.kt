package com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException

interface TimeoutListener {
    fun onConnectionTimeout()
}

const val TAG = "T5-TimeoutInterceptor"

class TimeoutInterceptor(private val timeoutListener: TimeoutListener? = null) : Interceptor,
    TimeoutListener {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (isConnectionTimedOut(chain)) {
            onConnectionTimeout()
            throw SocketTimeoutException()
        }
        return chain.proceed(chain.request())
    }

    private fun isConnectionTimedOut(chain: Interceptor.Chain): Boolean {
        try {
            val response = chain.proceed(chain.request())
            val content = response.toString()
            response.close()
            Log.d(TAG, "isConnectionTimedOut() => $content")
        } catch (e: SocketTimeoutException) {
            return true
        }
        return false
    }

    override fun onConnectionTimeout() {
        if (timeoutListener != null) timeoutListener.onConnectionTimeout()
    }
}
