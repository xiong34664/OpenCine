package com.opencapture.opencine.cameracontrols

import com.opencapture.opencine.media.MonitorAnamorphicDesqueeze
import com.opencapture.opencine.media.MonitorDesqueezeDirection

/** Presentation only: the source pixels, scope samples and delivered files stay unchanged. */
object AnamorphicDesqueeze {
    val HELP get() = CameraControlsContext.context.getString(R.string.cc_anamorphic_desqueeze_help)

    fun snap(value: Double): Double = MonitorAnamorphicDesqueeze.snap(value)
    fun label(value: Double): String = MonitorAnamorphicDesqueeze.label(value)
    fun aspect(sourceAspect: Float, enabled: Boolean, factor: Double, direction: MonitorDesqueezeDirection): Float {
        val source = sourceAspect.takeIf { it.isFinite() && it > 0f } ?: (16f / 9f)
        return requireNotNull(MonitorAnamorphicDesqueeze.aspect(source, enabled, factor, direction))
    }
}
