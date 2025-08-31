package com.itdeveapps.stepsshare.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

internal class ActivityRecognitionPermissionActivity : ComponentActivity() {
    companion object {
        private var continuation: CancellableContinuation<Result<Boolean>>? = null

        suspend fun request(context: Context): Result<Boolean> = suspendCancellableCoroutine { cont ->
            continuation?.cancel()
            continuation = cont
            context.startActivity(
                Intent(context, ActivityRecognitionPermissionActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        continuation?.resume(Result.success(granted))
        continuation = null
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            continuation?.resume(Result.success(true))
            continuation = null
            finish()
            return
        }
        launcher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
    }

    override fun onDestroy() {
        super.onDestroy()
        continuation?.cancel()
        continuation = null
    }
}


