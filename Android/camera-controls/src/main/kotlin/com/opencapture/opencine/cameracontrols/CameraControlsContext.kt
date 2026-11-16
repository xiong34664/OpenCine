package com.opencapture.opencine.cameracontrols

import android.content.Context

/** Application-scoped context for resolving strings outside @Composable. Initialized by the host app. */
public object CameraControlsContext {
    @Volatile
    public lateinit var context: Context
        private set

    public fun init(c: Context) {
        if (!::context.isInitialized) context = c.applicationContext
    }
}
