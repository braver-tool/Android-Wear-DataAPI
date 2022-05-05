# Android-Wear-DataAPI

The Sample project describes the connection between the mobile app and the wear app.

* Wear App supports from Android N
* Passing data from the Mobile app to the Wear app using DataAPI and GoogleApiClient
* Using 'WearableListenerService' retrieve the data from the end
* 100% kotlin support


# Note

- Add Google play service dependencies on both build.gradle files
- The package name for both mobile and wear applications must same
- Add gsm version code on both manifest files
- Add hostName, path Prefix, and scheme for the connection on the manifest file
- Must use the same signatures for both (If any key store files are used in the mobile app to generate apk files, must use the same keystore file for wear app)