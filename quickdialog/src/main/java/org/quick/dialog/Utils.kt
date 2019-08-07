package org.quick.dialog

import android.app.Activity
import android.os.Build

object Utils {
    /**
     * 检查Activity是否运行，true:正在运行 false : 反之
     */
    fun checkActivityIsRunning(activity: Activity?): Boolean = !(activity == null || activity.isFinishing || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed)
}