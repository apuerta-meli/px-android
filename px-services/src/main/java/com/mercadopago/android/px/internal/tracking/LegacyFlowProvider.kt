package com.mercadopago.android.px.internal.tracking

import android.content.Context
import com.mercadopago.android.px.internal.util.JsonUtil

internal class LegacyFlowProvider(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)!!

    val flowId: String?
        get() = sharedPreferences.getString(PREF_FLOW_ID, null)

    val flowDetail: Map<String, Any>
        get() = JsonUtil.getStringMapFromJson(sharedPreferences.getString(PREF_FLOW_DETAIL, null))

    fun configure(flowId: String?, flowDetail: Map<String, Any>) {
        with(sharedPreferences.edit()) {
            putString(PREF_FLOW_ID, flowId)
            putString(PREF_FLOW_DETAIL, JsonUtil.toJson(flowDetail))
            apply()
        }
    }

    companion object {
        private const val SHARED_PREFERENCE_NAME = "com.mercadopago.checkout.store.tracking_legacy"
        private const val PREF_FLOW_ID = "PREF_FLOW_ID"
        private const val PREF_FLOW_DETAIL = "PREF_FLOW_DETAIL"
    }
}
