/*
 *  Created by https://github.com/braver-tool on 05/05/22, 12:10 PM
 *  Copyright (c) 2022 . All rights reserved.
 */

package com.braver.wear.android

import android.util.Log
import android.widget.Toast
import com.braver.wear.android.MainActivity.Companion.EXTRA_MESSAGE_FROM_WEAR
import com.braver.wear.android.MainActivity.Companion.PATH_FOR_MOBILE
import com.google.android.gms.common.data.FreezableUtils
import com.google.android.gms.wearable.*

class WearDataListenerService : WearableListenerService(), DataApi.DataListener {
    var TAG = "WearListenerService"
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        Log.i(
            "##BTApp-Wear@@$TAG",
            "--------->onDataChanged"
        )
        val events: List<DataEvent> =
            FreezableUtils.freezeIterable(dataEvents)
        dataEvents.close()
        for (event in events) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (PATH_FOR_MOBILE == path) {
                    val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                    val wMessage = dataMapItem.dataMap.getString(EXTRA_MESSAGE_FROM_WEAR)
                    Log.i(
                        "##BTApp-Wear@@$TAG",
                        "----message----->$wMessage"
                    )
                    Toast.makeText(
                        this,
                        "Message from Wear device is :$wMessage",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.i(
                        "##BTApp-Wear@@$TAG",
                        "--------->path is none"
                    )
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(
            "##BTApp-Wear@@$TAG",
            "--------->onCreate"
        )
    }

    override fun onMessageReceived(p0: MessageEvent) {
        super.onMessageReceived(p0)
        Log.i(
            "##BTApp-Wear@@$TAG",
            "--------->onMessageReceived"
        )
    }
}
