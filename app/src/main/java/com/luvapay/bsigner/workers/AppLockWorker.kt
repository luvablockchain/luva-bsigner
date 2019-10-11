package com.luvapay.bsigner.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.luvapay.bsigner.utils.getAppPin
import com.luvapay.bsigner.utils.lockApp

class AppLockWorker (context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        if (getAppPin().isNotBlank()) lockApp()
        return Result.success()
    }

    companion object {
        const val UNIQUE_WORK_NAME = "AppLockWorker"
    }

}