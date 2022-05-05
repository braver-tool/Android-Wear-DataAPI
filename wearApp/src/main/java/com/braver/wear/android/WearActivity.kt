/*
 *  Created by https://github.com/braver-tool on 05/05/22, 12:10 PM
 *  Copyright (c) 2022 . All rights reserved.
 */

package com.braver.wear.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.braver.wear.android.databinding.ActivityWearBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataApi
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import java.util.*

class WearActivity : Activity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    private val TAG: String = "###Braver_Wear_Side"
    private lateinit var mainBinding: ActivityWearBinding
    private var mGoogleApiClient: GoogleApiClient? = null

    companion object {
        const val PATH_FOR_WEAR = "/path_for_wear"
        const val PATH_FOR_MOBILE = "/path_for_mobile"
        const val EXTRA_CURRENT_TIME = "extra_current_time"
        const val EXTRA_USER_NAME = "extra_user_name"
        const val EXTRA_USER_EMAIL = "extra_user_email"
        const val EXTRA_USER_PHONE = "extra_user_phone"
        const val EXTRA_MESSAGE_FROM_WEAR = "extra_message_from_wear"
        private const val ALLOWED_CHARACTERS = "0123456789QWERTYUIOPASDFGHJKLZXCVBNM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityWearBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        mainBinding.sendDataButton.setOnClickListener { v ->
            sendRandomMessageToMobileApp()
        }
    }

    override fun onStart() {
        super.onStart()
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        }
        mGoogleApiClient!!.connect()
    }

    override fun onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.disconnect()
        }
        super.onStop()
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        Log.e(TAG, "----Status------->onConnectionFailed")
        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                result.errorCode, this, 0
            ) { retryConnecting() }!!.show()
        }

    }

    override fun onConnected(p0: Bundle?) {
        Log.e(TAG, "----Status------->onConnected successfully")
        val intent = Intent(this@WearActivity, MobileDataListenerService::class.java)
        startService(intent)
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.e(TAG, "----Status------->ConnectionSuspended")
        retryConnecting()
    }

    private fun sendRandomMessageToMobileApp() {
        val wearAvailable = mGoogleApiClient!!.hasConnectedApi(Wearable.API)
        Log.i(TAG, "----hasConnectedApi------->$wearAvailable")
        val dataMapRequest = PutDataMapRequest.create(PATH_FOR_MOBILE)
        val map = dataMapRequest.dataMap
        map.putString(EXTRA_MESSAGE_FROM_WEAR, getRandomString())
        map.putLong(EXTRA_CURRENT_TIME, Date().time)
        val putDataRequest = dataMapRequest.asPutDataRequest()
        putDataRequest.setUrgent()
        Wearable.DataApi.putDataItem(mGoogleApiClient!!, putDataRequest)
            .setResultCallback { dataItemResult: DataApi.DataItemResult ->
                if (dataItemResult.status.isSuccess) {
                    Log.i(TAG, "----sendRandomMessageToMobileApp------->Successfully!!")
                } else {
                    Log.i(TAG, "----sendRandomMessageToMobileApp------->Failed!!")
                }
            }
    }

    private fun retryConnecting() {
        if (!mGoogleApiClient!!.isConnecting) {
            mGoogleApiClient!!.connect()
        }
    }

    private fun getRandomString(): String {
        val random = Random()
        val sb = StringBuilder(15)
        for (i in 0 until 15)
            sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        return sb.toString()
    }
}