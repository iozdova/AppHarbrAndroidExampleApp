package com.appharbr.kotlin.example.app.gam

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.appharbr.kotlin.example.app.R
import com.appharbr.kotlin.example.app.ui.theme.AppHarbrExampleAppTheme
import com.appharbr.sdk.engine.AdSdk
import com.appharbr.sdk.engine.AdStateResult
import com.appharbr.sdk.engine.AppHarbr
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class GamNativeAdActivity : ComponentActivity() {

    private val nativeAdState = mutableStateOf<NativeAd?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNativeAd()

        setContent {
            AppHarbrExampleAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Row() {
                        DisplayNativeAd()
                    }
                }
            }
        }
    }

    private fun requestNativeAd() {
        val adLoader = AdLoader.Builder(
            this,
            applicationContext.resources.getString(R.string.gam_native_ad_unit_id)
        ).forNativeAd { nativeAd: NativeAd ->

            val adResult = AppHarbr.shouldBlockNativeAd(AdSdk.GAM, nativeAd)
            when (adResult.adStateResult) {
                AdStateResult.BLOCKED -> {
                    nativeAdState.value = null
                    Log.e("LOG", "Native ad was blocked by appharbr")
                }
                else -> {}
            }

            if (isDestroyed) {
                nativeAdState.value?.destroy()
                return@forNativeAd
            }
            nativeAdState.value?.destroy()
            nativeAdState.value = nativeAd
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("LOG", "Handle the failure by logging, altering the UI, and so on.")
            }
        }).withNativeAdOptions(
            NativeAdOptions.Builder()
                // Methods in the NativeAdOptions.Builder class can be
                // used here to specify individual options settings.
                .build()
        ).build()

        adLoader.loadAd(AdManagerAdRequest.Builder().build())
    }

    @Composable
    private fun DisplayNativeAd() {
        nativeAdState.value?.let {
            Text(text = it.body ?: "Empty body")
        }
    }

}