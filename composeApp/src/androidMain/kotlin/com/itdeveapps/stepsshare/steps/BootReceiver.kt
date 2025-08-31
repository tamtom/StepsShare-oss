package com.itdeveapps.stepsshare.steps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.itdeveapps.stepsshare.data.work.StepsSyncWorker

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Re-schedule periodic sync on boot
        StepsSyncWorker.schedulePeriodic(context)
    }
}


