/*
 *  Created by https://github.com/braver-tool on 05/05/22, 12:10 PM
 *  Copyright (c) 2022 . All rights reserved.
 */

package com.braver.wear.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.braver.wear.android.databinding.ActivityMainBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataApi.DataItemResult
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import java.util.*

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    private val TAG: String = "###Braver_Mobile_Side"
    private lateinit var mainBinding: ActivityMainBinding
    private var mGoogleApiClient: GoogleApiClient? = null

    companion object {
        const val PATH_FOR_WEAR = "/path_for_wear"
        const val PATH_FOR_MOBILE = "/path_for_mobile"
        const val EXTRA_CURRENT_TIME = "extra_current_time"
        const val EXTRA_USER_NAME = "extra_user_name"
        const val EXTRA_USER_EMAIL = "extra_user_email"
        const val EXTRA_USER_PHONE = "extra_user_phone"
        const val EXTRA_MESSAGE_FROM_WEAR = "extra_message_from_wear"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        mainBinding.sendDataButton.setOnClickListener { v ->
            sendDataToWearApp()
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
        val intent = Intent(this@MainActivity, WearDataListenerService::class.java)
        startService(intent)
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.e(TAG, "----Status------->ConnectionSuspended")
        retryConnecting()
    }

    private fun sendDataToWearApp() {
        val username = mainBinding.editTextTextPersonName.text.toString()
        val userEmail = mainBinding.editTextTextEmailAddress.text.toString()
        val userPhone = mainBinding.editTextPhone.text.toString()
        if (username.isEmpty() && userEmail.isEmpty() && userPhone.isEmpty()) {
            return
        }
        val wearAvailable = mGoogleApiClient!!.hasConnectedApi(Wearable.API)
        Log.i(TAG, "----hasConnectedApi------->$wearAvailable")
        val dataMapRequest = PutDataMapRequest.create(PATH_FOR_WEAR)
        val map = dataMapRequest.dataMap
        map.putString(EXTRA_USER_NAME, username)
        map.putString(EXTRA_USER_EMAIL, userEmail)
        map.putString(EXTRA_USER_PHONE, userPhone)
        map.putLong(EXTRA_CURRENT_TIME, Date().time)
        val putDataRequest = dataMapRequest.asPutDataRequest()
        putDataRequest.setUrgent()
        Wearable.DataApi.putDataItem(mGoogleApiClient!!, putDataRequest)
            .setResultCallback { dataItemResult: DataItemResult ->
                if (dataItemResult.status.isSuccess) {
                    Log.i(TAG, "----sendDataToWearApp------->Successfully!!")
                    mainBinding.editTextTextPersonName.setText("")
                    mainBinding.editTextTextEmailAddress.setText("")
                    mainBinding.editTextPhone.setText("")
                } else {
                    Log.i(TAG, "----sendDataToWearApp------->Failed!!")
                }
            }
    }

    private fun retryConnecting() {
        if (!mGoogleApiClient!!.isConnecting) {
            mGoogleApiClient!!.connect()
        }
    }
}