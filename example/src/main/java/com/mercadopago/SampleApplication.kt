package com.mercadopago

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.react.ReactFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.mercadopago.android.px.BuildConfig
import com.mercadopago.android.px.addons.*
import com.mercadopago.android.px.di.Dependencies.Companion.instance
import com.mercadopago.android.px.font.FontConfigurator
import com.mercadopago.android.px.internal.util.HttpClientUtil


class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {
        Fresco.initialize(this)
        Stetho.initializeWithDefaults(this)
        SoLoader.init(this, false)


        val client = AndroidFlipperClient.getInstance(this)
        client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
        client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
        client.addPlugin(DatabasesFlipperPlugin(this))
        client.addPlugin(SharedPreferencesFlipperPlugin(this))
        client.addPlugin(CrashReporterPlugin.getInstance())

        val networkFlipperPlugin = NetworkFlipperPlugin()

        // Create client base, add interceptors
        val baseClient = HttpClientUtil.createBaseClient(this, 10, 10, 10)
                .addInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin, true))
                .addNetworkInterceptor(StethoInterceptor())


        client.addPlugin(networkFlipperPlugin)
        client.start()



        // customClient: client with TLS protocol setted
        val customClient = HttpClientUtil.enableTLS12(baseClient)
            .build()
        HttpClientUtil.setCustomClient(customClient)
        instance.initialize(applicationContext)
        val escManagerBehaviour: ESCManagerBehaviour = FakeEscManagerBehaviourImpl()
        with(PXBehaviourConfigurer()) {
            if (BuildConfig.DEBUG) {
                with(MockSecurityBehaviour(escManagerBehaviour))
            }
            with(escManagerBehaviour)
            with(FakeLocaleBehaviourImpl)
        }.configure()
        FontConfigurator.configure()
    }

    companion object {
        var localeTag = "en-US"
    }
}
