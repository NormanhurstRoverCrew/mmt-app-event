package com.normorovers.mmt.app.event.mmtevent

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log

class AppPermissions{
    companion object {
        fun camera(context : Context): Boolean {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        }

        fun requestCamera(activity : Activity, requestCode : Int) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
//                                Manifest.permission.CAMERA)) {
//                    // Show an explanation to the user *asynchronously* -- don't block
//                    // this thread waiting for the user's response! After the user
//                    // sees the explanation, try again to request the permission.
//                    Log.d("AppPermissions", "This app requires Camera Permissions")
//                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity,
                            arrayOf(Manifest.permission.CAMERA),
                            requestCode)

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
//                }
        }
    }
}
